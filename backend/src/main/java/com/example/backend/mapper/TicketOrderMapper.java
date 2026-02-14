package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 门票订单表Mapper接口
 * @Version 1.0
 */
@Mapper
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {
}