package com.xyz.elasticsearchplus.core.api;

import com.xyz.elasticsearchplus.core.bean.DocMetaData;
import com.xyz.elasticsearchplus.core.bean.PageParam;
import com.xyz.elasticsearchplus.core.bean.PageResult;
import com.xyz.elasticsearchplus.core.operator.OperatorFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @author sxl
 * @version 2020/11/20
 */
public interface IElasticService<T> {

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
        return OperatorFactory.getOperator().getById(this.docMetaData(), id);
    }

    default List<T> getByIds(Collection<String> ids) {
        return OperatorFactory.getOperator().multiGet(this.docMetaData(), ids);
    }

    default List<T> listByParam(Object conditionBean, int size) {
        return OperatorFactory.getOperator().searchByParam(this.docMetaData(), conditionBean, size);
    }

    default List<T> listByParam(Object conditionBean) {
        return OperatorFactory.getOperator().searchByParam(this.docMetaData(), conditionBean, LIMIT);
    }

    default List<T> listByDsl(String dslJsonString) {
        return OperatorFactory.getOperator().listByDsl(this.docMetaData(), dslJsonString);
    }

    default PageResult<T> pageByParam(Object o, PageParam pageSearchDto) {
        return OperatorFactory.getOperator().pageByParam(this.docMetaData(), o, pageSearchDto);
    }

    default PageResult<T> pageByDsl(String dslJsonString, PageParam pageParam) {
        return OperatorFactory.getOperator().pageByDsl(this.docMetaData(), dslJsonString, pageParam);
    }

    default Long countByParam(Object conditionBean) {
        return OperatorFactory.getOperator().countByParam(this.docMetaData(), conditionBean);
    }

    default List<T> scrollByParam(Object conditionBean) {
        return OperatorFactory.getOperator().scrollByParam(this.docMetaData(), conditionBean);
    }

    default List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, String scrollId, int size) {
        return OperatorFactory.getOperator().scrollByParam(docMetaData, conditionBean, scrollId, size);
    }

    default List<T> scrollByDsl(String dslJsonString, String scrollId, int size) {
        return OperatorFactory.getOperator().scrollByDsl(this.docMetaData(), dslJsonString, scrollId, size);
    }

    default void saveBatchAsync(List<T> dataList) {
        OperatorFactory.getOperator().bulkInsertAsync(this.docMetaData(), dataList, BATCH_SIZE);
    }

    default void updateBatchAsync(List<T> dataList) {
        OperatorFactory.getOperator().bulkUpdateAsync(this.docMetaData(), dataList, BATCH_SIZE);
    }

    default void deleteBatchAsync(List<String> ids) {
        OperatorFactory.getOperator().bulkDeleteAsync(this.docMetaData(), ids, BATCH_SIZE);
    }

    default void saveAsync(T data) {
        OperatorFactory.getOperator().insertAsync(this.docMetaData(), data);
    }

    default void updateAsync(T data) {
        OperatorFactory.getOperator().updateAsync(this.docMetaData(), data);
    }

    default void deleteAsync(String id) {
        OperatorFactory.getOperator().deleteAsync(this.docMetaData(), id);
    }

    default void saveBatch(List<T> dataList) {
        OperatorFactory.getOperator().bulkInsert(this.docMetaData(), dataList, BATCH_SIZE);
    }

    default void updateBatch(List<T> dataList) {
        OperatorFactory.getOperator().bulkUpdate(this.docMetaData(), dataList, BATCH_SIZE);
    }

    default void deleteBatch(List<String> ids) {
        OperatorFactory.getOperator().bulkDelete(this.docMetaData(), ids, BATCH_SIZE);
    }

    default void save(T data) {
        OperatorFactory.getOperator().save(this.docMetaData(), data);
    }

    default void update(T data) {
        OperatorFactory.getOperator().update(this.docMetaData(), data);
    }

    default void delete(String id) {
        OperatorFactory.getOperator().delete(this.docMetaData(), id);
    }

}
