package com.xyz.elasticsearchplus.core;

import com.xyz.elasticsearchplus.core.bean.BaseElasticPo;
import com.xyz.elasticsearchplus.core.bean.DocMetaData;
import com.xyz.elasticsearchplus.core.bean.PageParam;
import com.xyz.elasticsearchplus.core.bean.PageResult;
import org.elasticsearch.action.bulk.BulkShardRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @author sxl
 * @version 2020/11/20
 */
public interface IElasticService<T extends BaseElasticPo> {

    int BATCH_SIZE = 1000;
    /**
     * 一次获取数据量
     */
    int LIMIT = 1000;

    DocMetaData<T> docContext();

    /**
     * 根据id获取doc
     *
     * @param id document.id
     * @return Optional
     */
    default Optional<T> getById(String id) {
        return DocOperator.getById(this.docContext(), id);
    }

    default List<T> getByIds(Collection<String> ids) {
        return DocOperator.multiGet(this.docContext(), ids);
    }

    default List<T> listByParam(Object conditionBean, int size) {
        return DocOperator.searchByParam(this.docContext(), conditionBean, size);
    }

    default List<T> listByParam(Object conditionBean) {
        return DocOperator.searchByParam(this.docContext(), conditionBean, LIMIT);
    }

    default List<T> listByDsl(String dslJsonString) {
        return DocOperator.listByDsl(this.docContext(), dslJsonString);
    }

    default PageResult<T> pageByParam(Object o, PageParam pageSearchDto) {
        return DocOperator.pageByParam(this.docContext(), o, pageSearchDto);
    }

    default PageResult<T> pageByDsl(String dslJsonString, PageParam pageParam) {
        return DocOperator.pageByDsl(this.docContext(), dslJsonString, pageParam);
    }

    default Long countByParam(Object conditionBean) {
        return DocOperator.countByParam(this.docContext(), conditionBean);
    }

    default List<T> scrollByParam(Object conditionBean) {
        return DocOperator.scrollByParam(this.docContext(), conditionBean);
    }

    default List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, String scrollId, int size) {
        return DocOperator.scrollByParam(docMetaData, conditionBean, scrollId, size);
    }

    default List<T> scrollByDsl(String dslJsonString, String scrollId, int size) {
        return DocOperator.scrollByDsl(this.docContext(), dslJsonString, scrollId, size);
    }

    default PageResult<T> pageBySourceBuilder(SearchSourceBuilder sourceBuilder) {
        return DocOperator.pageByParam(this.docContext(), sourceBuilder);
    }

    default void saveBatchAsync(List<T> dataList) {
        DocOperator.bulkInsertAsync(this.docContext(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void saveAsync(List<T> dataList) {
        DocOperator.bulkInsertAsync(this.docContext(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void updateBatchAsync(List<T> dataList) {
        DocOperator.bulkUpdateAsync(this.docContext(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);

    }

    default void deleteBatchAsync(List<String> ids) {
        DocOperator.bulkDeleteAsync(this.docContext(), ids, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void saveBatch(List<T> dataList) {
        DocOperator.bulkInsert(this.docContext(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void updateBatch(List<T> dataList) {
        DocOperator.bulkUpdate(this.docContext(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);

    }

    default void deleteBatch(List<String> ids) {
        DocOperator.bulkDelete(this.docContext(), ids, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

}
