package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 旅游攻略表
 * </p>
 *
 * @author 
 * 
 */
@Getter
@Setter
@ApiModel(value = "Strategy对象", description = "旅游攻略表")
public class Strategy implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("作者ID")
    private Integer userId;

    @ApiModelProperty("攻略ID")
    private Integer attractionId;

    @ApiModelProperty("封面图片")
    private String coverImage;

    @ApiModelProperty("攻略摘要")
    private String intro;

    @ApiModelProperty("攻略内容")
    private String content;

    @ApiModelProperty("行程天数")
    private Integer days;

    @ApiModelProperty("预算类型（1-经济，2-舒适，3-豪华）")
    private Integer budgetType;

    @ApiModelProperty("预估预算")
    private BigDecimal estimatedBudget;

    @ApiModelProperty("适合季节")
    private String season;

    @ApiModelProperty("浏览数")
    private Integer viewCount;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("收藏数")
    private Integer collectCount;

    @ApiModelProperty("评论数")
    private Integer commentCount;

    @ApiModelProperty("是否推荐")
    private Integer isRecommend;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}