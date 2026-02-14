package com.example.backend.entity.vo.ticket;

import com.example.backend.entity.Ticket;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 景点门票表VO类
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TicketVO对象", description = "景点门票表VO")
public class TicketVO extends Ticket {

    @ApiModelProperty(value = "景点名称")
    private String attractionName;

    @ApiModelProperty(value = "票种类型名称")
    private String typeName;
}