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

    DocMetaData<T> docMetaData();

    /**
     * 根据id获取doc
     *
     * @param id document.id
     * @return Optional
     */
    default Optional<T> getById(String id) {
        return DocOperator.getById(this.docMetaData(), id);
    }

    default List<T> getByIds(Collection<String> ids) {
        return DocOperator.multiGet(this.docMetaData(), ids);
    }

    default List<T> listByParam(Object conditionBean, int size) {
        return DocOperator.searchByParam(this.docMetaData(), conditionBean, size);
    }

    default List<T> listByParam(Object conditionBean) {
        return DocOperator.searchByParam(this.docMetaData(), conditionBean, LIMIT);
    }

    default List<T> listByDsl(String dslJsonString) {
        return DocOperator.listByDsl(this.docMetaData(), dslJsonString);
    }

    default PageResult<T> pageByParam(Object o, PageParam pageSearchDto) {
        return DocOperator.pageByParam(this.docMetaData(), o, pageSearchDto);
    }

    default PageResult<T> pageByDsl(String dslJsonString, PageParam pageParam) {
        return DocOperator.pageByDsl(this.docMetaData(), dslJsonString, pageParam);
    }

    default Long countByParam(Object conditionBean) {
        return DocOperator.countByParam(this.docMetaData(), conditionBean);
    }

    default List<T> scrollByParam(Object conditionBean) {
        return DocOperator.scrollByParam(this.docMetaData(), conditionBean);
    }

    default List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, String scrollId, int size) {
        return DocOperator.scrollByParam(docMetaData, conditionBean, scrollId, size);
    }

    default List<T> scrollByDsl(String dslJsonString, String scrollId, int size) {
        return DocOperator.scrollByDsl(this.docMetaData(), dslJsonString, scrollId, size);
    }

    default PageResult<T> pageBySourceBuilder(SearchSourceBuilder sourceBuilder) {
        return DocOperator.pageByParam(this.docMetaData(), sourceBuilder);
    }

    default void saveBatchAsync(List<T> dataList) {
        DocOperator.bulkInsertAsync(this.docMetaData(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void updateBatchAsync(List<T> dataList) {
        DocOperator.bulkUpdateAsync(this.docMetaData(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);

    }

    default void deleteBatchAsync(List<String> ids) {
        DocOperator.bulkDeleteAsync(this.docMetaData(), ids, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void saveAsync(T data) {
        DocOperator.insertAsync(this.docMetaData(), data, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void updateAsync(T data) {
        DocOperator.updateAsync(this.docMetaData(), data, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void deleteAsync(String id) {
        DocOperator.deleteAsync(this.docMetaData(), id, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void saveBatch(List<T> dataList) {
        DocOperator.bulkInsert(this.docMetaData(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void updateBatch(List<T> dataList) {
        DocOperator.bulkUpdate(this.docMetaData(), dataList, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);

    }

    default void deleteBatch(List<String> ids) {
        DocOperator.bulkDelete(this.docMetaData(), ids, BATCH_SIZE, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void save(T data) {
        DocOperator.save(this.docMetaData(), data, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void update(T data) {
        DocOperator.update(this.docMetaData(), data, BulkShardRequest.DEFAULT_TIMEOUT);
    }

    default void delete(String id) {
        DocOperator.delete(this.docMetaData(), id, BulkShardRequest.DEFAULT_TIMEOUT);
    }

}
