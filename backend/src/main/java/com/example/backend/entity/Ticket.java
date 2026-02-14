package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description 景点门票表实体类
 * @Version 1.0
 */
@Data
@TableName("ticket")
@ApiModel(value = "Ticket对象", description = "景点门票表")
public class Ticket {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "攻略ID")
    private Integer attractionId;

    @ApiModelProperty("封面图片")
    private String coverImage;

    @ApiModelProperty(value = "门票名称")
    private String name;

    @ApiModelProperty(value = "票种类型（1-成人票，2-儿童票，3-学生票，4-老人票）")
    private Integer type;

    @ApiModelProperty(value = "门票价格")
    private BigDecimal price;

    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "门票描述")
    private String description;

    @ApiModelProperty(value = "有效期（天数）")
    private Integer validityPeriod;

    @ApiModelProperty(value = "购票须知")
    private String purchaseNotes;

    @ApiModelProperty(value = "库存")
    private Integer inventory;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}