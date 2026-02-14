package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Attraction;
import com.example.backend.entity.Ticket;
import com.example.backend.entity.TicketOrder;
import com.example.backend.entity.vo.ticket.TicketVO;
import com.example.backend.mapper.TicketMapper;
import com.example.backend.mapper.TicketOrderMapper;
import com.example.backend.service.IAttractionService;
import com.example.backend.service.ITicketOrderService;
import com.example.backend.service.ITicketService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 景点门票表服务实现类
 * @Version 1.0
 */
@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements ITicketService {

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private IAttractionService attractionService;

    @Override
    public Integer add(Ticket ticket) {
        ticketMapper.insert(ticket);
        return ticket.getId();
    }

    @Override
    public Boolean batchAdd(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            ticketMapper.insert(ticket);
        }
        return true;
    }

    @Override
    public Boolean del(Integer id) {
        return ticketMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean batchDel(List<Integer> ids) {
        return ticketMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean edit(Ticket ticket) {
        return ticketMapper.updateById(ticket) > 0;
    }

    @Override
    public PageResult<List<TicketVO>> queryPage(Integer pageNum, Integer pageSize, String name, Integer attractionId, Integer type) {
        Page<Ticket> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Ticket> queryWrapper = new QueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (attractionId != null) {
            queryWrapper.eq("attraction_id", attractionId);
        }
        if (type != null) {
            queryWrapper.eq("type", type);
        }
        
        queryWrapper.orderByDesc("id");
        
        Page<Ticket> ticketPage = ticketMapper.selectPage(page, queryWrapper);
        List<Ticket> tickets = ticketPage.getRecords();
        
        List<TicketVO> ticketVOs = new ArrayList<>();
        for (Ticket ticket : tickets) {
            TicketVO ticketVO = convertVO(ticket);
            ticketVOs.add(ticketVO);
        }

        return new PageResult<>(ticketVOs, ticketPage.getTotal(), ticketPage.getCurrent(), ticketPage.getSize());
    }

    @Override
    public List<Ticket> getAll() {
        QueryWrapper<Ticket> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return ticketMapper.selectList(queryWrapper);
    }

    @Override
    public Ticket getByIdDetail(Integer id) {
        return ticketMapper.selectById(id);
    }

    @Override
    public List<TicketVO> getByAttractionId(Integer attractionId) {
        QueryWrapper<Ticket> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attraction_id", attractionId);
        queryWrapper.orderByDesc("id");
        
        List<Ticket> tickets = ticketMapper.selectList(queryWrapper);
        List<TicketVO> ticketVOs = new ArrayList<>();
        
        for (Ticket ticket : tickets) {
            TicketVO ticketVO = convertVO(ticket);
            ticketVOs.add(ticketVO);
        }
        
        return ticketVOs;
    }

    /**
     * 将Ticket转换为TicketVO
     */
    private TicketVO convertVO(Ticket ticket) {
        TicketVO ticketVO = new TicketVO();
        BeanUtils.copyProperties(ticket, ticketVO);
        
        // 获取景点名称
        if (ticket.getAttractionId() != null) {
            Attraction attraction = attractionService.getByIdDetail(ticket.getAttractionId());
            if (attraction != null) {
                ticketVO.setAttractionName(attraction.getName());
            }
        }
        
        // 设置票种类型名称
        ticketVO.setTypeName(getTypeName(ticket.getType()));
        
        return ticketVO;
    }

    /**
     * 获取票种类型名称
     */
    private String getTypeName(Integer type) {
        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(1, "成人票");
        typeMap.put(2, "儿童票");
        typeMap.put(3, "学生票");
        typeMap.put(4, "老人票");
        
        return typeMap.getOrDefault(type, "未知类型");
    }
}