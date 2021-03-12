package com.xyz.elasticsearchplus.operator7.impl;

import com.xyz.elasticsearchplus.core.bean.DocMetaData;
import com.xyz.elasticsearchplus.core.bean.PageParam;
import com.xyz.elasticsearchplus.core.bean.PageResult;
import com.xyz.elasticsearchplus.core.operator.IDocOperator;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author sxl
 * @since 2021/3/12 16:40
 */
public class DocOperator implements IDocOperator {

    private RestHighLevelClient highLevelClient;

    public DocOperator(RestHighLevelClient highLevelClient) {
        this.highLevelClient = Objects.requireNonNull(highLevelClient);
    }

    public RestHighLevelClient highLevelClient() {
        return highLevelClient;
    }

    @Override
    public <T> Optional<T> getById(DocMetaData<T> docMetaData, String id) {
        return Optional.empty();
    }

    @Override
    public <T> List<T> multiGet(DocMetaData<T> docMetaData, Collection<String> ids) {
        return null;
    }

    @Override
    public <T> List<T> listByDsl(DocMetaData<T> docMetaData, String dslJsonString) {
        return null;
    }

    @Override
    public <T> PageResult<T> pageByDsl(DocMetaData<T> docMetaData, String dslJsonString, PageParam pageSearchDto) {
        return null;
    }

    @Override
    public <T> List<T> searchByParam(DocMetaData<T> docMetaData, Object conditionBean, int size) {
        return null;
    }

    @Override
    public <T> Long countByParam(DocMetaData<T> docMetaData, Object conditionBean) {
        return null;
    }

    @Override
    public <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean) {
        return null;
    }

    @Override
    public <T> List<T> scrollByParam(DocMetaData<T> docMetaData, Object conditionBean, String scrollId, int size) {
        return null;
    }

    @Override
    public <T> List<T> scrollByDsl(DocMetaData<T> docMetaData, String dslJsonString, String scrollId, int size) {
        return null;
    }

    @Override
    public <T> void insertAsync(DocMetaData<T> docMetaData, T data) {

    }

    @Override
    public <T> void updateAsync(DocMetaData<T> docMetaData, T data) {

    }

    @Override
    public <T> void deleteAsync(DocMetaData<T> docMetaData, String data) {

    }

    @Override
    public <T> void bulkInsertAsync(DocMetaData<T> docMetaData, List<T> dataList, int count) {

    }

    @Override
    public <T> void bulkUpdateAsync(DocMetaData<T> docMetaData, List<T> dataList, int count) {

    }

    @Override
    public <T> void bulkDeleteAsync(DocMetaData<T> docMetaData, List<String> ids, int count) {

    }

    @Override
    public <T> void bulkUpdate(DocMetaData<T> docMetaData, List<T> dataList, int count) {

    }

    @Override
    public <T> void bulkDelete(DocMetaData<T> docMetaData, List<String> ids, int count) {

    }

    @Override
    public <T> void save(DocMetaData<T> docMetaData, T data) {

    }

    @Override
    public <T> void update(DocMetaData<T> docMetaData, T data) {

    }

    @Override
    public <T> void delete(DocMetaData<T> docMetaData, String id) {

    }

    @Override
    public <T> void bulkInsert(DocMetaData<T> docMetaData, List<T> dataList, int count) {

    }

    @Override
    public <T> PageResult<T> pageByParam(DocMetaData<T> docMetaData, Object o, PageParam pageSearchDto) {
        return null;
    }

    @Override
    public <T> PageResult<T> pageByParam(DocMetaData<T> docMetaData, SearchSourceBuilder sourceBuilder) {
        return null;
    }
}
