package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 景点门票表Mapper接口
 * @Version 1.0
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}