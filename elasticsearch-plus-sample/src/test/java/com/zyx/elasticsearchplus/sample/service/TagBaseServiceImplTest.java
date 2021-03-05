package com.zyx.elasticsearchplus.sample.service;

import com.google.common.collect.Lists;
import com.xyz.elasticsearchplus.annotation.ElasticFiled;
import com.xyz.elasticsearchplus.annotation.ElasticFiledSort;
import com.xyz.elasticsearchplus.annotation.ElasticSourceField;
import com.xyz.elasticsearchplus.core.bean.PageParam;
import com.xyz.utils.JsonUtils;
import com.zyx.elasticsearchplus.sample.ApplicationTests;
import com.zyx.elasticsearchplus.sample.po.TagBasePo;
import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.Date;
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
        condition.setCategory("测试");
        condition.setValid(1);
        List<TagBasePo> tagBasePos = tagBaseService.listByParam(condition);
        System.out.println("method: listByParam, param: tagBasePos= " + JsonUtils.beanToJson(tagBasePos));
    }

    @Test
    void testListByParam() {
    }

    @Test
    void listByDsl() {
        List<TagBasePo> basePos = tagBaseService.listByDsl("{\"size\":20,\"query\":{\"match_all\":{}}}");
        System.out.println("method: listByDsl, param: basePos= " + JsonUtils.beanToJson(basePos));
    }

    @Test
    void pageByParam() {
    }

    @Test
    void pageByDsl() {
    }

    @Test
    void countByParam() {
        TagSearchCondition condition = new TagSearchCondition();
        condition.setCategory("测试");
        condition.setValid(1);
        Long count = tagBaseService.countByParam(condition);
        System.out.println("count = " + count);
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
    void saveAsync() throws InterruptedException {
        TagBasePo basePo = new TagBasePo();
        basePo.setCategory("测试");
        basePo.setLevel1("测试1");
        basePo.setLevel2("测试2");
        basePo.setSearchKeywords("测试测试");
        basePo.setIkSearchKeywords("测试测试");
        basePo.setMatchDsl("");
        basePo.setId("1");
        basePo.setCreateTime(new Date());
        basePo.setUpdateTime(new Date());
        basePo.setValid(1);

        tagBaseService.saveAsync(basePo);
        Thread.sleep(5000);
    }

    @Test
    void save() {
        TagBasePo basePo = new TagBasePo();
        basePo.setCategory("测试");
        basePo.setLevel1("测试1");
        basePo.setLevel2("测试2");
        basePo.setSearchKeywords("测试测试");
        basePo.setIkSearchKeywords("测试测试");
        basePo.setMatchDsl("");
        basePo.setId("1");
        basePo.setCreateTime(new Date());
        basePo.setUpdateTime(new Date());
        basePo.setValid(1);

        tagBaseService.save(basePo);
    }

    @Test
    void updateBatchAsync() throws InterruptedException {
        TagBasePo basePo = new TagBasePo();
        basePo.setCategory("测试");
        basePo.setLevel1("测试异步批量更新");
        basePo.setLevel2("测试2");
        basePo.setSearchKeywords("测试测试");
        basePo.setIkSearchKeywords("测试测试");
        basePo.setMatchDsl("");
        basePo.setId("1");
        basePo.setCreateTime(new Date());
        basePo.setUpdateTime(new Date());
        basePo.setValid(1);

        tagBaseService.updateBatchAsync(Lists.newArrayList(basePo));
        Thread.sleep(5000);
    }

    @Test
    void updateAsync() throws InterruptedException {
        TagBasePo basePo = new TagBasePo();
        basePo.setCategory("测试");
        basePo.setLevel1("测试异步");
        basePo.setLevel2("测试2");
        basePo.setSearchKeywords("测试测试");
        basePo.setIkSearchKeywords("测试测试");
        basePo.setMatchDsl("");
        basePo.setId("1");
        basePo.setCreateTime(new Date());
        basePo.setUpdateTime(new Date());
        basePo.setValid(1);

        tagBaseService.updateAsync(basePo);
        Thread.sleep(5000);
    }

    @Test
    void deleteBatchAsync() {
    }

    @Test
    void saveBatch() {

        TagBasePo basePo = new TagBasePo();
        basePo.setCategory("测试");
        basePo.setLevel1("测试批量插入1");
        basePo.setLevel2("测试2");
        basePo.setSearchKeywords("测试测试");
        basePo.setIkSearchKeywords("测试测试");
        basePo.setMatchDsl("");
        basePo.setId("2");
        basePo.setCreateTime(new Date());
        basePo.setUpdateTime(new Date());
        basePo.setValid(1);

        TagBasePo basePo1 = new TagBasePo();
        basePo1.setCategory("测试");
        basePo1.setLevel1("测试批量插入2");
        basePo1.setLevel2("测试2");
        basePo1.setSearchKeywords("测试测试");
        basePo1.setIkSearchKeywords("测试测试");
        basePo1.setMatchDsl("");
        basePo1.setId("3");
        basePo1.setCreateTime(new Date());
        basePo1.setUpdateTime(new Date());
        basePo1.setValid(1);

        tagBaseService.saveBatch(Lists.newArrayList(basePo1,basePo));

    }

    @Test
    void updateBatch() {
    }

    @Test
    void deleteBatch() {
        tagBaseService.deleteBatch(Lists.newArrayList("1", "2", "3"));
        countByParam();
    }

    @Test
    void delete() {
        tagBaseService.delete("1");
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