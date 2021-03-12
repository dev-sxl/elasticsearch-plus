package com.xyz.elasticsearchplus.core.operator;

import com.xyz.elasticsearchplus.core.bean.DocMetaData;
import com.xyz.elasticsearchplus.core.bean.PageParam;
import com.xyz.elasticsearchplus.core.bean.PageResult;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author sxl
 * @since 2021/3/12 15:42
 */
public interface IDocOperator {

    <T> Optional<T> getById(DocMetaData<T> docMetaData, String id);

    <T> List<T> multiGet(DocMetaData<T> docMetaData, Collection<String> ids);

    <T> List<T> listByDsl(DocMetaData<T> docMetaData, String dslJsonString);

    <T> PageResult<T> pageByDsl(DocMetaData<T> docMetaData, String dslJsonString, PageParam pageSearchDto);

    <T> List<T> searchByParam(DocMetaData<T> docMetaData, Object conditionBean, int size);

    <T> Long countByParam(DocMetaData<T> docMetaData, Object conditionBean);

    <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean);

    <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, String scrollId, int size);

    <T> List<T> scrollByDsl(DocMetaData<T> docMetaData, String dslJsonString, String scrollId, int size);

    <T> void insertAsync(DocMetaData<T> docMetaData, T data);

    <T> void updateAsync(DocMetaData<T> docMetaData, T data);

    <T> void deleteAsync(DocMetaData<T> docMetaData, String data);

    <T> void bulkInsertAsync(DocMetaData<T> docMetaData, List<T> dataList, int count);

    <T> void bulkUpdateAsync(DocMetaData<T> docMetaData, List<T> dataList, int count);

    <T> void bulkDeleteAsync(DocMetaData<T> docMetaData, List<String> ids, int count);

    <T> void bulkInsert(DocMetaData<T> docMetaData, List<T> dataList, int count);

    <T> void bulkUpdate(DocMetaData<T> docMetaData, List<T> dataList, int count);

    <T> void bulkDelete(DocMetaData<T> docMetaData, List<String> ids, int count);

    <T> void save(DocMetaData<T> docMetaData, T data);

    <T> void update(DocMetaData<T> docMetaData, T data);

    <T> void delete(DocMetaData<T> docMetaData, String id);

    <T> PageResult<T> pageByParam(DocMetaData<T> docMetaData, Object o, PageParam pageSearchDto);

    <T> PageResult<T> pageByParam(DocMetaData<T> docMetaData, SearchSourceBuilder sourceBuilder);
}
