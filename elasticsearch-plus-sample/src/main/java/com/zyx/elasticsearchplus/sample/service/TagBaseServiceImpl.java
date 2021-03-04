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

    @Override
    public DocMetaData<TagBasePo> docContext() {
        return DocMetaData.<TagBasePo>builder().docType(TagBasePo.class).idField("id").index("tag_base").type("list").build();
    }


}
