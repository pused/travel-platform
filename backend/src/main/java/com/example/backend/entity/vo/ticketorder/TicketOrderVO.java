package com.example.backend.entity.vo.ticketorder;

import com.example.backend.entity.TicketOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 门票订单表VO类
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TicketOrderVO extends TicketOrder {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 门票名称
     */
    private String ticketName;

    /**
     * 景点名称
     */
    private String attractionName;

    /**
     * 票种类型名称
     */
    private String typeName;

    /**
     * 订单状态名称
     */
    private String statusName;
}