package com.xyz.elasticsearchplus.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuli
 * @version 2019/11/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoBoundingBox {
    /**
     * 左上角坐标
     */
    private String topLeftPoint;
    /**
     * 右下角坐标
     */
    private String bottomRightPoint;
}
