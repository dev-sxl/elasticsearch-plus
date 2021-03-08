package com.xyz.elasticsearchplus.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.xyz.elasticsearchplus.core.annotation.FieldSource;
import com.xyz.elasticsearchplus.core.annotation.FiledSearch;
import com.xyz.elasticsearchplus.core.annotation.FiledSort;
import com.xyz.elasticsearchplus.core.annotation.NestedSearch;
import com.xyz.elasticsearchplus.core.bean.*;
import com.xyz.utils.BeanUtils;
import com.xyz.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.BulkShardRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


/**
 * @author sxl
 * @version 2019/11/15
 */
@Slf4j
public final class DocOperator {

    /**
     * 一次获取数据量
     */
    private static final int LIMIT = 1000;

    private static RestHighLevelClient highLevelClient;

    private static volatile boolean hasInit = false;

    private DocOperator() {
    }

    public static void init(RestHighLevelClient highLevelClient) {
        if (hasInit) {
            return;
        }

        synchronized (DocOperator.class) {
            if (hasInit) {
                return;
            }
            DocOperator.highLevelClient = highLevelClient;
            hasInit = true;
        }
    }

    public static RestHighLevelClient highLevelClient() {
        if (hasInit) {
            return DocOperator.highLevelClient;
        }
        throw new RuntimeException("DocOperator can use in after init");
    }

    static <T> Optional<T> getById(DocMetaData<T> docMetaData, String id) {
        GetRequest getRequest = new GetRequest(docMetaData.getIndex(), docMetaData.getType(), id);
        try {
            GetResponse response = highLevelClient().get(getRequest, RequestOptions.DEFAULT);
            if (!response.isExists()) {
                return Optional.empty();
            }
            return Optional.ofNullable(JSONObject.parseObject(response.getSourceAsString(), docMetaData.getDocType()));
        } catch (Exception e) {
            log.error("ElasticBaseService.getById执行异常", e);
            return Optional.empty();
        }
    }

    /**
     * 批量获取
     *
     * @param ids ids
     * @return List<T>
     */
    public static <T> List<T> multiGet(DocMetaData<T> docMetaData, Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        MultiGetRequest request = new MultiGetRequest();
        ids.forEach(id -> request.add(new MultiGetRequest.Item(docMetaData.getIndex(), docMetaData.getType(), id)));

        try {
            MultiGetResponse response = highLevelClient().mget(request, RequestOptions.DEFAULT);

            return Arrays.stream(response.getResponses())
                         .filter(f -> f.getResponse().isExists())
                         .map(res -> JSONObject.parseObject(res.getResponse().getSourceAsString(), docMetaData.getDocType()))
                         .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("ElasticBaseService.multiGet执行异常", e);
            return Collections.emptyList();
        }
    }

    public static <T> SearchResponse doSearch(DocMetaData<T> docMetaData, SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(docMetaData.getIndex());
        searchRequest.types(docMetaData.getType());
        searchRequest.source(sourceBuilder);
        SearchResponse response;
        try {
            response = highLevelClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private static <T> List<T> getDataByHits(SearchHits hits, Class<T> type) {
        List<T> resultList = new ArrayList<>(((int) (hits.getTotalHits())));
        T data;
        for (SearchHit hit : hits) {
            data = JSON.parseObject(hit.getSourceAsString(), type);
            resultList.add(data);
        }
        return resultList;
    }

    public static <T> List<T> listByDsl(DocMetaData<T> docMetaData, String dslJsonString) {
        String endpoint = docMetaData.getIndex() + "/" + docMetaData.getType() + "/_search";
        String responseStr = queryByDsl(dslJsonString, endpoint);
        if (StringUtils.isBlank(responseStr)) {
            return Collections.emptyList();
        }
        return hitSourcesFromResult(docMetaData, responseStr);
    }

    public static <T> PageResult<T> pageByDsl(DocMetaData<T> docMetaData, String dslJsonString, PageParam pageSearchDto) {

        JSONObject jsonObject = JsonUtils.jsonToBean(dslJsonString, JSONObject.class);
        jsonObject.put("from", pageSearchDto.getPageSize() * (pageSearchDto.getPageNo() - 1));
        jsonObject.put("size", pageSearchDto.getPageSize());
        String path = docMetaData.getIndex() + "/" + docMetaData.getType() + "/_search";
        String respStr = queryByDsl(jsonObject.toJSONString(), path);
        long total = Long.parseLong(JsonUtils.extractStrByPath(respStr, "$.hits.total"));
        return new PageResult<>(total, pageSearchDto.getPageNo(), pageSearchDto.getPageSize(), hitSourcesFromResult(docMetaData, respStr));
    }

    private static <T> List<T> hitSourcesFromResult(DocMetaData<T> docMetaData, String responseStr) {
        return JsonUtils.extraListByPath(responseStr, "$.hits.hits[*]._source", docMetaData.getDocType());
    }

    private static String queryByDsl(String dslJsonString, String endpoint) {
        RestClient restClient = highLevelClient().getLowLevelClient();
        try {
            Request request = new Request("GET", endpoint);
            request.setJsonEntity(dslJsonString);
            Response response = restClient.performRequest(request);
            log.info("method: ElasticBaseService.queryByDsl, param: statusCode= {}", response.getStatusLine().getStatusCode());
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            log.error("ElasticBaseService.queryByDsl error, e= {}", dslJsonString);
            log.error("ElasticBaseService.queryByDsl error, error stack:", e);
            throw new RuntimeException("es query exception ", e);
        }
    }

    /**
     * 根据注解搜索
     *
     * @param conditionBean 查询参数
     * @return 返回结果
     */
    public static <T> List<T> searchByParam(DocMetaData<T> docMetaData, Object conditionBean, int size) {
        SearchResponse response = doSearchByCondition(docMetaData, conditionBean, size);
        return getDataByHits(response.getHits(), docMetaData.getDocType());
    }

    public static <T> SearchResponse doSearchByCondition(DocMetaData<T> docMetaData,
                                                         Object conditionBean, int size) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        builderParam(sourceBuilder, conditionBean);
        sourceBuilder.size(size);
        return doSearch(docMetaData, sourceBuilder);
    }

    /**
     * 根据注解搜索(分页)
     *
     * @param conditionBean 查询参数
     * @return 返回结果
     */
    public static <T> List<T> searchByParam(DocMetaData<T> docMetaData, Object conditionBean) {
        return searchByParam(docMetaData, conditionBean, LIMIT);
    }

    /**
     * count
     *
     * @param conditionBean 查询参数
     * @return 返回结果
     */
    public static <T> Long countByParam(DocMetaData<T> docMetaData, Object conditionBean) {
        return doSearchByCondition(docMetaData, conditionBean, 0).getHits().totalHits;
    }

    public <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, TimeValue timeValue) {
        return scrollByParam(docMetaData, conditionBean, timeValue, LIMIT);
    }

    public static <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean) {
        return scrollByParam(docMetaData, conditionBean, BulkShardRequest.DEFAULT_TIMEOUT, LIMIT);
    }

    public static <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, String scrollId, int size) {
        Scroll scroll = new Scroll(BulkShardRequest.DEFAULT_TIMEOUT);
        SearchResponse searchResponse;
        try {
            // 第一次查询没有游标，获取数据并记录游标
            if (StringUtils.isBlank(scrollId)) {
                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                builderParam(sourceBuilder, conditionBean);
                sourceBuilder.size(size);
                SearchRequest searchRequest = new SearchRequest(docMetaData.getIndex());
                searchRequest.source(sourceBuilder);
                searchRequest.scroll(scroll);
                searchResponse = highLevelClient().search(searchRequest, RequestOptions.DEFAULT);
            } else {
                // 有游标按游标进行查询
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                searchResponse = highLevelClient().scroll(scrollRequest.scroll(scroll), RequestOptions.DEFAULT);
            }

            // 处理返回结果

            // 记录游标
            scrollId = searchResponse.getScrollId();
            List<T> resultData = Collections.emptyList();
            int hitSize = searchResponse.getHits().getHits() == null ? 0 : searchResponse.getHits().getHits().length;
            if (hitSize > 0) {
                resultData = getDataByHits(searchResponse.getHits(), docMetaData.getDocType());
            } else {
                // 已经到最后数据,进行clear scroll
                clearScroll(scrollId);
            }
            return resultData;
        } catch (Exception e) {
            log.error("listByScroll execute error , conditionBean:{} , scrollId: {} , size: {}",
                    conditionBean, scrollId, size);
            log.error("listByScroll execute error, exception: ", e);
            throw new RuntimeException("listByScroll execute error", e);
        }
    }

    public static <T> List<T> scrollByDsl(DocMetaData<T> docMetaData, String dslJsonString, String scrollId, int size) {
        String scrollContextKeepAliveTime = "3m";
        String respStr;
        if (StringUtils.isBlank(scrollId)) {
            // 第一次查询没有游标
            JSONObject jsonObject = JsonUtils.jsonToBean(dslJsonString, JSONObject.class);
            jsonObject.put("size", size);
            String endpoint = docMetaData.getIndex() + "/" + docMetaData.getType() + "/_search?scroll=" + scrollContextKeepAliveTime;
            respStr = queryByDsl(jsonObject.toJSONString(), endpoint);
        } else {
            // 有游标按游标进行查询
            String endpoint = "/_search/scroll";
            Map<String, String> scrollParam = new HashMap<>();
            scrollParam.put("scroll_id", scrollId);
            // scroll上下文再保持3分钟
            scrollParam.put("scroll", scrollContextKeepAliveTime);
            respStr = queryByDsl(JsonUtils.beanToJson(scrollParam), endpoint);
        }

        // 处理返回结果,获取数据并记录游标
        String respScrollId = JsonUtils.extractStrByPath(respStr, "$._scroll_id");
        List<T> data = hitSourcesFromResult(docMetaData, respStr);
        // 当超出了 scroll timeout 时，搜索上下文会被自动删除。
        // 但是，保持 scrolls 打开是有成本的，当不再使用 scroll 时应当使用 clear-scroll API 进行显式清除。
        if (CollectionUtils.isEmpty(data)) {
            clearScroll(respScrollId);
        }

        return data;
    }

    private static void clearScroll(String scrollId) {
        try {
            // 进行clear scroll
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = highLevelClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            if (!clearScrollResponse.isSucceeded()) {
                log.error("clearScrollResponse error : {}", clearScrollResponse);
            }
        } catch (IOException e) {
            log.error("clearScrollResponse error,respScrollId: {}", scrollId);
            log.error("clearScrollResponse error,exception: ", e);
        }
    }

    /**
     * scroll查询
     *
     * @param conditionBean 参数
     * @param timeValue     超时
     * @param size          批次数量
     * @return 数据
     */
    public static <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, TimeValue timeValue, int size) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        builderParam(sourceBuilder, conditionBean);
        return scrollBySearchSourceBuilder(docMetaData, sourceBuilder, timeValue, size);
    }

    public static <T> List<T> scrollBySearchSourceBuilder(DocMetaData<T> docMetaData, SearchSourceBuilder sourceBuilder,
                                                          TimeValue timeValue, int size) {
        sourceBuilder.size(size);
        String scrollId = null;
        long hitSize = 0;
        SearchResponse searchResponse;
        List<T> resultList = null;
        SearchRequest searchRequest = new SearchRequest(docMetaData.getIndex());
        final Scroll scroll = new Scroll(timeValue);
        searchRequest.scroll(scroll);
        SearchScrollRequest scrollRequest;
        long totalHits = 0L;
        try {
            do {
                searchRequest.source(sourceBuilder);
                if (hitSize == 0) {
                    searchResponse = highLevelClient().search(searchRequest, RequestOptions.DEFAULT);
                    totalHits = searchResponse.getHits().getTotalHits();
                    resultList = getContainer(totalHits);
                } else {
                    scrollRequest = new SearchScrollRequest(scrollId);
                    searchResponse = highLevelClient().scroll(scrollRequest.scroll(scroll), RequestOptions.DEFAULT);
                }
                scrollId = searchResponse.getScrollId();
                hitSize = searchResponse.getHits().getHits() == null ? 0 : searchResponse.getHits().getHits().length;
                if (totalHits > 0) {
                    resultList.addAll(getDataByHits(searchResponse.getHits(), docMetaData.getDocType()));
                }
            } while (hitSize > 0);
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = highLevelClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            clearScrollResponse.isSucceeded();
        } catch (Exception e) {
            log.error("scrollByParam scroll error, conditionBean:{}", sourceBuilder.toString(), e);
            return Collections.emptyList();
        }
        return resultList;
    }

    private static <T> List<T> getContainer(long size) {
        try {
            return new ArrayList<>(Math.toIntExact(size));
        } catch (ArithmeticException e) {
            return new ArrayList<>();
        }
    }

    public static <T> void insertAsync(DocMetaData<T> docMetaData, T data, TimeValue timeout) {
        saveAsync(timeout, () -> buildInsertRequest(docMetaData, data));
    }

    public static <T> void updateAsync(DocMetaData<T> docMetaData, T data, TimeValue timeout) {
        saveAsync(timeout, () -> buildUpdateRequest(docMetaData, data));
    }

    public static <T> void deleteAsync(DocMetaData<T> docMetaData,
                                       String data, TimeValue timeout) {
        saveAsync(timeout, () -> buildDeleteRequest(docMetaData, data));
    }


    public static <T> void bulkInsertAsync(DocMetaData<T> docMetaData,
                                           List<T> dataList, int count, TimeValue timeout) {
        bulkAsync(count, timeout, dataList, (T doc) -> buildInsertRequest(docMetaData, doc));
    }

    public static <T> void bulkUpdateAsync(DocMetaData<T> docMetaData, List<T> dataList, int count, TimeValue timeout) {
        bulkAsync(count, timeout, dataList, (T doc) -> buildUpdateRequest(docMetaData, doc));
    }

    public static <T> void bulkDeleteAsync(DocMetaData<T> docMetaData, List<String> ids, int count, TimeValue timeout) {
        bulkAsync(count, timeout, ids, (String id) -> buildDeleteRequest(docMetaData, id));
    }

    public static <T> void bulkAsync(int count, TimeValue timeout, List<T> dataList,
                                     Function<T, DocWriteRequest<?>> requestFunction) {
        List<DocWriteRequest<?>> actionList = buildWriteRequests(dataList, requestFunction);
        if (CollectionUtils.isEmpty(actionList)) {
            return;
        }
        Lists.partition(actionList, count).forEach(data -> bulkAsync(timeout, data));
    }

    public static <T> void bulkAsync(TimeValue timeout, Iterable<DocWriteRequest<?>> data) {
        BulkRequest request = new BulkRequest();
        request.timeout(timeout).add(data);
        highLevelClient().bulkAsync(request, RequestOptions.DEFAULT, getListener());
    }

    private static <T> void saveAsync(TimeValue timeout, Supplier<DocWriteRequest<T>> requestFunction) {
        BulkRequest request = new BulkRequest().timeout(timeout).add(requestFunction.get());
        highLevelClient().bulkAsync(request, RequestOptions.DEFAULT, getListener());
    }

    public static <T> List<DocWriteRequest<?>> buildWriteRequests(List<T> dataList,
                                                                  Function<T, DocWriteRequest<?>> requestFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        return dataList.stream().map(requestFunction).collect(Collectors.toList());
    }

    public static <T> void bulkInsert(DocMetaData<T> docMetaData,
                                      List<T> dataList, int count, TimeValue timeout) {
        bulk(dataList, count, timeout, (T doc) -> buildInsertRequest(docMetaData, doc));
    }

    public static <T> void bulkUpdate(DocMetaData<T> docMetaData, List<T> dataList, int count, TimeValue timeout) {
        bulk(dataList, count, timeout, (T doc) -> buildUpdateRequest(docMetaData, doc));
    }

    public static <T> void bulkDelete(DocMetaData<T> docMetaData, List<String> ids, int count, TimeValue timeout) {
        bulk(ids, count, timeout, (String id) -> buildDeleteRequest(docMetaData, id));
    }

    public static <T> void save(DocMetaData<T> docMetaData, T data, TimeValue timeout) {
        commit(timeout, () -> buildInsertRequest(docMetaData, data));
    }

    public static <T> void update(DocMetaData<T> docMetaData, T data, TimeValue timeout) {
        commit(timeout, () -> buildUpdateRequest(docMetaData, data));
    }

    public static <T> void delete(DocMetaData<T> docMetaData, String id, TimeValue timeout) {
        commit(timeout, () -> buildDeleteRequest(docMetaData, id));
    }

    public static <T> void bulk(List<T> dataList, int count, TimeValue timeout, Function<T, DocWriteRequest<?>> requestFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        List<DocWriteRequest<?>> actionList = buildWriteRequests(dataList, requestFunction);
        Lists.partition(actionList, count).forEach(data -> bulk(timeout, data));
    }

    public static void bulk(TimeValue timeout, List<DocWriteRequest<?>> data) {
        BulkRequest request = new BulkRequest();
        request.timeout(timeout).add(data);
        //写入完成立即刷新
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            BulkResponse bulk = highLevelClient().bulk(request, RequestOptions.DEFAULT);
            log.info(JSON.toJSONString(bulk.status()));
        } catch (IOException e) {
            log.error("bulk error", e);
        }
    }

    private static <T> void commit(TimeValue timeout, Supplier<DocWriteRequest<T>> writeRequestSupplier) {
        commit(timeout, writeRequestSupplier.get());
    }

    public static <T> void commit(TimeValue timeout, DocWriteRequest<T> data) {
        BulkRequest request = new BulkRequest().timeout(timeout).add(data);
        try {
            BulkResponse bulk = highLevelClient().bulk(request, RequestOptions.DEFAULT);
            log.info(JSON.toJSONString(bulk.status()));
        } catch (IOException e) {
            log.error("bulk error", e);
        }
    }

    private static <T> DeleteRequest buildDeleteRequest(DocMetaData<T> docMetaData, String id) {
        return new DeleteRequest(docMetaData.getIndex(), docMetaData.getType(), id);
    }

    private static <T> UpdateRequest buildUpdateRequest(DocMetaData<T> docMetaData, T doc) {
        String source = JsonUtils.beanToJson(doc);
        String id = extractId(docMetaData, source);
        return new UpdateRequest(docMetaData.getIndex(), docMetaData.getType(), id).doc(source, XContentType.JSON);
    }

    private static <T> IndexRequest buildInsertRequest(DocMetaData<T> docMetaData, T doc) {
        String source = JsonUtils.beanToJson(doc);
        String id = extractId(docMetaData, source);
        if (StringUtils.isBlank(id)) {
            return new IndexRequest(docMetaData.getIndex(), docMetaData.getType()).source(source, XContentType.JSON);
        }
        return new IndexRequest(docMetaData.getIndex(), docMetaData.getType(), id).source(source, XContentType.JSON);
    }

    private static <T> String extractId(DocMetaData<T> docMetaData, String source) {
        return JsonUtils.extractStrByPath(source, docMetaData.getIdSourceName());
    }

    private static ActionListener<BulkResponse> getListener() {
        return new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {
                log.info("bulkAsync onResponse bulkResponse:{}", JSON.toJSONString(bulkResponse.status()));
            }

            @Override
            public void onFailure(Exception e) {
                log.error("bulkAsync onFailure", e);
            }
        };
    }

    /**
     * 根据注解搜索(分页)
     *
     * @param o 查询参数
     * @return 返回结果
     */
    public static <T> PageResult<T> pageByParam(DocMetaData<T> docMetaData, Object o, PageParam pageSearchDto) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from((pageSearchDto.getPageNo() - 1) * pageSearchDto.getPageSize());
        sourceBuilder.size(pageSearchDto.getPageSize());
        builderParam(sourceBuilder, o);
        return pageByParam(docMetaData, sourceBuilder);
    }

    /**
     * 根据条件搜索
     *
     * @param sourceBuilder SearchSourceBuilder
     * @return List<T>
     */
    public static <T> PageResult<T> pageByParam(DocMetaData<T> docMetaData, SearchSourceBuilder sourceBuilder) {
        SearchResponse response = doSearch(docMetaData, sourceBuilder);
        long totalHits = response.getHits().totalHits;
        List<T> dataList = getDataByHits(response.getHits(), docMetaData.getDocType());
        return new PageResult<>(totalHits, sourceBuilder.from() / sourceBuilder.size() + 1, sourceBuilder.size(), dataList);
    }

    private static void appendSort(SearchSourceBuilder sourceBuilder, FiledSortDto sortDto) {
        if (sortDto.getType() == GeoDistanceSortBuilder.class) {
            GeoPoint geoPoint = new GeoPoint(sortDto.getValue().toString());
            GeoDistanceSortBuilder sortBuilder = new GeoDistanceSortBuilder(sortDto.getFieldName(), geoPoint).order(sortDto.getOrderBy())
                                                                                                             .unit(sortDto.getUnit());
            sourceBuilder.sort(sortBuilder);
        } else if (sortDto.getType() == FieldSortBuilder.class) {
            sourceBuilder.sort(new FieldSortBuilder(sortDto.getFieldName()).order(sortDto.getOrderBy()));
        }
    }

    /**
     * 设置相关参数
     *
     * @param o 入参
     * @return boolean
     */
    public static void builderParam(SearchSourceBuilder sourceBuilder, Object o) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQueryBuilder);
        build(sourceBuilder, o, boolQueryBuilder, StringUtils.EMPTY);
    }

    private static void build(SearchSourceBuilder sourceBuilder, Object conditionBean, BoolQueryBuilder boolQueryBuilder, String path) {
        Map<String, Object> conditionMap = BeanUtils.beanToMap(conditionBean);
        for (Field field : conditionBean.getClass().getDeclaredFields()) {
            if (valueIsEmpty(conditionMap.get(field.getName()))) {
                // 空集合,数组,对象都跳过
                continue;
            }

            if (field.getAnnotation(FiledSearch.class) != null) {
                filedSearchBuild(boolQueryBuilder, path, conditionMap, field);
                continue;
            }

            if (field.getAnnotation(NestedSearch.class) != null) {
                nestedSearchBuild(boolQueryBuilder, path, conditionMap, field);
                continue;
            }

            if (field.getAnnotation(FiledSort.class) != null) {
                filedSortBuild(sourceBuilder, conditionMap, field);
                continue;
            }

            if (field.getAnnotation(FieldSource.class) != null) {
                sourceFiledBuild(sourceBuilder, conditionMap, field);
            }
        }
    }

    public static boolean valueIsEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Iterable<?>) {
            return IterableUtils.isEmpty((Iterable<?>) object);
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Object[]) {
            return ((Object[]) object).length == 0;
        }

        if (object instanceof Iterator<?>) {
            return !((Iterator<?>) object).hasNext();
        }

        if (object instanceof Enumeration<?>) {
            return !((Enumeration<?>) object).hasMoreElements();
        }

        try {
            return Array.getLength(object) == 0;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }

    private static void sourceFiledBuild(SearchSourceBuilder sourceBuilder, Map<String, Object> conditionMap, Field field) {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) conditionMap.get(field.getName());
        if (CollectionUtils.isNotEmpty(list)) {
            String[] temps = new String[list.size()];
            if (field.getAnnotation(FieldSource.class).type() == FieldSource.SourceType.EXCLUDE) {
                sourceBuilder.fetchSource(null, list.toArray(temps));
            } else {
                sourceBuilder.fetchSource(list.toArray(temps), null);
            }
        }
    }

    private static void filedSortBuild(SearchSourceBuilder sourceBuilder, Map<String, Object> dataMap, Field field) {
        FiledSort sortAnnotation;
        FiledSortDto sortDto;
        sortAnnotation = field.getAnnotation(FiledSort.class);
        sortDto = new FiledSortDto(sortAnnotation.type(), sortAnnotation.orderBy(),
                sortAnnotation.fieldName(), sortAnnotation.unit(), dataMap.get(field.getName()));
        appendSort(sourceBuilder, sortDto);
    }

    private static void nestedSearchBuild(BoolQueryBuilder boolQueryBuilder, String path, Map<String, Object> dataMap, Field field) {
        NestedSearch nestedAnnotation;
        nestedAnnotation = field.getAnnotation(NestedSearch.class);
        FiledDto filedDto = nestedSearchFiledDto(path, nestedAnnotation, field);
        appendField(filedDto, boolQueryBuilder, JSON.parseObject(JSON.toJSONString(dataMap.get(field.getName())), field.getType()));
    }

    private static FiledDto nestedSearchFiledDto(String path, NestedSearch nestedAnnotation, Field field) {
        FiledDto filedDto = new FiledDto();
        filedDto.setPath(StringUtils.isNotBlank(nestedAnnotation.path()) ? path :
                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
        filedDto.setType(NestedSearch.class);
        filedDto.setCombined(true);
        filedDto.setFieldName(field.getName());
        filedDto.setMethod(nestedAnnotation.method());
        return filedDto;
    }

    private static void filedSearchBuild(BoolQueryBuilder boolQueryBuilder, String path, Map<String, Object> conditionMap, Field field) {
        FiledSearch fieldAnnotation;
        fieldAnnotation = field.getAnnotation(FiledSearch.class);
        FiledDto filedDto = filedSearchFiledDto(path, conditionMap, fieldAnnotation, field);
        Object o1 = fieldAnnotation.combined() ?
                JSON.parseObject(JSON.toJSONString(conditionMap.get(field.getName())), field.getType()) : conditionMap.get(field.getName());
        appendField(filedDto, boolQueryBuilder, o1);
    }

    private static FiledDto filedSearchFiledDto(String path, Map dataMap, FiledSearch fieldAnnotation, Field field) {
        return new FiledDto(field.getType(), fieldAnnotation.model(), fieldAnnotation.method(),
                fieldAnnotation.matchOperator(), fieldAnnotation.multiMatchType(),
                fieldAnnotation.key(), field.getName(), dataMap.get(field.getName()), fieldAnnotation.combined(),
                fieldAnnotation.exists(), path, field.getDeclaringClass());
    }

    /**
     * 添加查询条件
     *
     * @param filedDto         数据字段
     * @param boolQueryBuilder 查询条件
     */
    private static void appendField(FiledDto filedDto, BoolQueryBuilder boolQueryBuilder, Object o) {
        QueryBuilder queryBuilder;
        if (StringUtils.isNotBlank(filedDto.getPath()) && filedDto.getType().equals(NestedSearch.class)) {
            queryBuilder = new NestedQueryBuilder(filedDto.getPath(), getQueryBuilder(filedDto, o), ScoreMode.None);
        } else {
            queryBuilder = getQueryBuilder(filedDto, o);
        }
        appendBuilder(filedDto, boolQueryBuilder, queryBuilder);
    }

    private static void appendBuilder(FiledDto filedDto, BoolQueryBuilder boolQueryBuilder, QueryBuilder queryBuilder) {
        switch (filedDto.getMethod()) {
            case MUST:
                boolQueryBuilder.must(queryBuilder);
                break;
            case FILTER:
                boolQueryBuilder.filter(queryBuilder);
                break;
            case SHOULD:
                boolQueryBuilder.should(queryBuilder);
                break;
            case MUST_NOT:
                boolQueryBuilder.mustNot(queryBuilder);
                break;
        }
    }

    private static QueryBuilder getQueryBuilder(FiledDto dto, Object o) {
        String field = dto.queryField();

        if (dto.getType() == String.class) {
            return matchModelHandle(dto, field);
        }

        if (BooleanUtils.isTrue(dto.isCombined())) {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if (dto.getType() == List.class) {
                ((List) dto.getValue()).forEach(data -> {
                    BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
                    build(null, JSON.parseObject(JSON.toJSONString(data), dto.getSubType()), subQuery, dto.getPath());
                    appendBuilder(new FiledDto(FiledSearch.Method.SHOULD), boolQuery, subQuery);
                });
            } else {
                build(null, o, boolQuery, dto.getPath());
            }
            return boolQuery;
        }

        if (dto.getType() == List.class) {
            return QueryBuilders.termsQuery(field, (List) dto.getValue());
        }

        if (dto.getType() == GeoLocation.class) {
            GeoLocation geoLocation = new ObjectMapper().convertValue(dto.getValue(), GeoLocation.class);
            return QueryBuilders.geoDistanceQuery(field).
                    distance(geoLocation.getDistance()).point(new GeoPoint(geoLocation.getLocation()));
        }

        if (dto.getType() == GeoBoundingBox.class) {
            GeoBoundingBox box = new ObjectMapper().convertValue(dto.getValue(), GeoBoundingBox.class);
            return QueryBuilders.geoBoundingBoxQuery(field).setCorners(new GeoPoint(box.getTopLeftPoint()),
                    new GeoPoint(box.getBottomRightPoint()));
        }

        if (dto.getType() == RangeQuery.class) {
            return rangeQueryBuilder(dto, field);
        }

        if (BooleanUtils.isTrue(dto.isExists())) {
            return QueryBuilders.existsQuery(field);
        }

        if (dto.getModel() == FiledSearch.Model.GTE && StringUtils.isNotBlank(field)) {
            return QueryBuilders.rangeQuery(field).gte(dto.getValue());
        }

        if (dto.getModel() == FiledSearch.Model.LTE && StringUtils.isNotBlank(field)) {
            return QueryBuilders.rangeQuery(field).lte(dto.getValue());
        }

        return QueryBuilders.termQuery(field, dto.getValue());
    }

    private static RangeQueryBuilder rangeQueryBuilder(FiledDto dto, String field) {
        RangeQuery rangeQuery = new ObjectMapper().convertValue(dto.getValue(), RangeQuery.class);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);

        if (StringUtils.isNotBlank(rangeQuery.getGt())) {
            rangeQueryBuilder.gt(rangeQuery.getGt());
        } else if (StringUtils.isNotBlank(rangeQuery.getGte())) {
            rangeQueryBuilder.gte(rangeQuery.getGte());
        }

        if (StringUtils.isNotBlank(rangeQuery.getLt())) {
            rangeQueryBuilder.lt(rangeQuery.getLt());
        } else if (StringUtils.isNotBlank(rangeQuery.getLte())) {
            rangeQueryBuilder.lte(rangeQuery.getLte());
        }

        return rangeQueryBuilder;
    }

    private static QueryBuilder matchModelHandle(FiledDto dto, String field) {
        if (dto.getModel() == FiledSearch.Model.LIKE) {
            return QueryBuilders.queryStringQuery("*" + dto.getValue() + "*").defaultField(field);
        }

        if (dto.getModel() == FiledSearch.Model.START_WITH) {
            return QueryBuilders.queryStringQuery(dto.getValue() + "*").defaultField(field);
        }

        if (dto.getModel() == FiledSearch.Model.MATCH_PHRASE) {
            return QueryBuilders.matchPhraseQuery(field, dto.getValue());
        }

        if (dto.getModel() == FiledSearch.Model.MATCH) {
            return matchQuery(field, dto.getValue()).operator(dto.getMatchOperator());
        }

        if (dto.getModel() == FiledSearch.Model.MULTI_MATCH) {
            return QueryBuilders.multiMatchQuery(dto.getValue(), field.split(","))
                                .type(dto.getMultiMatchType())
                                .operator(dto.getMatchOperator());
        }

        return QueryBuilders.termQuery(field, dto.getValue());
    }

}
