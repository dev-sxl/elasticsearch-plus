package com.zyx.elasticsearchplus.sample.service;

import com.xyz.elasticsearchplus.annotation.ElasticFiled;
import com.xyz.elasticsearchplus.annotation.ElasticFiledSort;
import com.xyz.elasticsearchplus.annotation.ElasticSourceField;
import com.xyz.elasticsearchplus.core.bean.PageParam;
import com.xyz.utils.JsonUtils;
import com.zyx.elasticsearchplus.sample.ApplicationTests;
import com.zyx.elasticsearchplus.sample.po.TagBasePo;
import lombok.Data;
import lombok.experimental.Accessors;
import org.assertj.core.util.Lists;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author sxl
 * @since 2021/3/4 17:21
 */
class TagBaseServiceImplTest extends ApplicationTests {

    @Resource
    private TagBaseService tagBaseService;


    @Test
    void getById() {
        Optional<TagBasePo> basePo = tagBaseService.getById("NrnhzxsP");
        basePo.ifPresent(tag -> System.out.println("结果:" + JsonUtils.beanToJson(basePo)));
    }

    @Test
    void getByIds() {
        List<TagBasePo> basePos = tagBaseService.getByIds(Lists.newArrayList("NrnhzxsP", "WoPav1HN"));
        System.out.println("method: getByIds, param: basePos= " + JsonUtils.beanToJson(basePos));
    }

    @Test
    void listByParam() {
        TagSearchCondition condition = new TagSearchCondition();
        condition.setCategory("竞对类");
        condition.setValid(1);
        List<TagBasePo> tagBasePos = tagBaseService.listByParam(condition);
        System.out.println("method: listByParam, param: tagBasePos= " + JsonUtils.beanToJson(tagBasePos));
    }

    @Test
    void testListByParam() {
    }

    @Test
    void listByDsl() {
    }

    @Test
    void pageByParam() {
    }

    @Test
    void pageByDsl() {
    }

    @Test
    void countByParam() {
    }

    @Test
    void scrollByParam() {
    }

    @Test
    void testScrollByParam() {
    }

    @Test
    void scrollByDsl() {
    }

    @Test
    void pageBySourceBuilder() {
    }

    @Test
    void saveBatchAsync() {
    }

    @Test
    void saveAsync() {
    }

    @Test
    void updateBatchAsync() {
    }

    @Test
    void deleteBatchAsync() {
    }

    @Test
    void saveBatch() {
    }

    @Test
    void updateBatch() {
    }

    @Test
    void deleteBatch() {
    }

    @Test
    void docContext() {
    }

    /**
     * @author sxl
     * @since 2020/11/23 15:32
     */
    @Data
    @Accessors(chain = true)
    public static class TagSearchCondition extends PageParam {

        @ElasticFiled(model = ElasticFiled.Model.EQUALS)
        private String id;

        @ElasticFiled(key = "id", model = ElasticFiled.Model.EQUALS, method = ElasticFiled.Method.MUST)
        private List<String> ids;

        @ElasticFiled(model = ElasticFiled.Model.EQUALS)
        private Integer valid;
        /**
         * 标签分类
         */
        @ElasticFiled(model = ElasticFiled.Model.EQUALS)
        private String category;
        /**
         * 一级标签
         */
        @ElasticFiled(model = ElasticFiled.Model.EQUALS)
        private String level1;
        /**
         * 一级标签
         */
        @ElasticFiled(model = ElasticFiled.Model.EQUALS, key = "level1", method = ElasticFiled.Method.SHOULD)
        private List<String> level1sShould;
        /**
         * 二级标签
         */
        @ElasticFiled(model = ElasticFiled.Model.EQUALS)
        private String level2;
        /**
         * 搜索关键字
         */
        @ElasticFiled(model = ElasticFiled.Model.MATCH_PHRASE)
        private String searchKeywords;
        /**
         * 搜索关键字(IK分词)
         */
        @ElasticFiled(model = ElasticFiled.Model.MATCH)
        private String ikSearchKeywords;

        @ElasticFiledSort(fieldName = "create_time", orderBy = SortOrder.DESC)
        private Boolean createTimeDescSort;

        /** 返回字段列表 */
        @ElasticSourceField
        private List<String> includeFields;
        /**
         * 排除字段列表
         */
        @ElasticSourceField(type = ElasticSourceField.SourceType.EXCLUDE)
        private List<String> excludeFields;
    }
}