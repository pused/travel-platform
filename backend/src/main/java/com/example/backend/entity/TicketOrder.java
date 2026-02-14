package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description 门票订单表实体类
 * @Version 1.0
 */
@Data
@TableName("ticket_order")
public class TicketOrder {

    /**
     * 订单号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 门票ID
     */
    private Integer ticketId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单数量
     */
    private Integer payNum;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态(1-已付款,2-已完成,3-已取消)
     */
    private Integer status;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 身份证号
     */
    private String cardNo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}