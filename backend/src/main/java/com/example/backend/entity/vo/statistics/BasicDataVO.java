package com.example.backend.entity.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 基础数据统计VO
 * @Version 1.0
 */
@Data
@ApiModel(value = "BasicDataVO", description = "基础数据统计VO")
public class BasicDataVO {

    @ApiModelProperty("用户数量")
    private Integer userCount;
}