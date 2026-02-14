package com.example.backend.entity.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 图表数据VO
 * @Version 1.0
 */
@Data
@ApiModel(value = "ChartDataVO", description = "图表数据VO")
public class ChartDataVO {

    @ApiModelProperty("X轴数据（用于柱状图和折线图）")
    private List<String> xAxis;

    @ApiModelProperty("Y轴数据（用于柱状图和折线图）")
    private List<Object> yAxis;

    @ApiModelProperty("饼图数据")
    private List<PieChartItem> data;

    /**
     * 饼图数据项
     */
    @Data
    @ApiModel(value = "PieChartItem", description = "饼图数据项")
    public static class PieChartItem {
        @ApiModelProperty("数值")
        private Object value;
        
        @ApiModelProperty("名称")
        private String name;
    }
}