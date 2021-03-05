package com.zyx.elasticsearchplus.sample.service;

import com.xyz.elasticsearchplus.core.bean.DocMetaData;
import com.zyx.elasticsearchplus.sample.po.TagBasePo;
import org.springframework.stereotype.Service;

/**
 * @author sxl
 * @since 2021/3/4 17:13
 */
@Service
public class TagBaseServiceImpl implements TagBaseService {

    private final DocMetaData<TagBasePo> docMetaData = buildMetaData();

    /**
     * @return 文档元数据
     */
    private DocMetaData<TagBasePo> buildMetaData() {
        return DocMetaData.<TagBasePo>builder().docType(TagBasePo.class)
                                               .idField("id")
                                               .index("tag_base")
                                               .type("list")
                                               .build();
    }

    @Override
    public DocMetaData<TagBasePo> docMetaData() {
        return docMetaData;
    }


}
