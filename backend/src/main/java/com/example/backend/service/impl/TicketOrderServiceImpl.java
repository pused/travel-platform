package com.example.backend.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.enums.CodeEnum;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.TicketOrder;
import com.example.backend.entity.User;
import com.example.backend.entity.Ticket;
import com.example.backend.entity.Attraction;
import com.example.backend.entity.vo.ticketorder.TicketOrderVO;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.TicketOrderMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.mapper.TicketMapper;
import com.example.backend.mapper.AttractionMapper;
import com.example.backend.service.ITicketOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 门票订单表Service实现类
 * @Version 1.0
 */
@Service
public class TicketOrderServiceImpl extends ServiceImpl<TicketOrderMapper, TicketOrder> implements ITicketOrderService {

    @Resource
    private TicketOrderMapper ticketOrderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private AttractionMapper attractionMapper;

    @Override
    public Integer add(TicketOrder ticketOrder) {
        Ticket ticket = ticketMapper.selectById(ticketOrder.getTicketId());
        if (ticket == null) {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR, "门票不存在");
        }
        if (ticket.getInventory() < ticketOrder.getPayNum()) {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR, "库存不足");
        }
        int count = ticketOrderMapper.insert(ticketOrder);
        if (count <= 0) {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR, "新增门票订单失败");
        }
        // 减少库存
        ticket.setInventory(ticket.getInventory() - ticketOrder.getPayNum());
        ticketMapper.updateById(ticket);
        return ticketOrder.getId();
    }

    @Override
    public Boolean del(Integer id) {
        return ticketOrderMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean batchDel(List<Integer> ids) {
        return ticketOrderMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean edit(TicketOrder ticketOrder) {
        // 如果状态是取消，需要恢复库存数量
        if (ticketOrder.getStatus() == 3) {
            Ticket ticket = ticketMapper.selectById(ticketOrder.getTicketId());
            if (ticket == null) {
                throw new BusinessException(CodeEnum.SYSTEM_ERROR, "门票不存在");
            }
            // 增加库存
            ticket.setInventory(ticket.getInventory() + ticketOrder.getPayNum());
            ticketMapper.updateById(ticket);
        }
        return ticketOrderMapper.updateById(ticketOrder) > 0;
    }

    @Override
    public PageResult<List<TicketOrderVO>> queryPage(Integer pageNum, Integer pageSize, String orderNo, String contactName, String contactPhone, Integer status, Integer userId, Integer ticketId) {
        QueryWrapper<TicketOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        
        if (StrUtil.isNotBlank(orderNo)) {
            queryWrapper.like("order_no", orderNo);
        }
        if (StrUtil.isNotBlank(contactName)) {
            queryWrapper.like("contact_name", contactName);
        }
        if (StrUtil.isNotBlank(contactPhone)) {
            queryWrapper.like("contact_phone", contactPhone);
        }
        if (ObjectUtil.isNotNull(status)) {
            queryWrapper.eq("status", status);
        }
        if (ObjectUtil.isNotNull(userId)) {
            queryWrapper.eq("user_id", userId);
        }
        if (ObjectUtil.isNotNull(ticketId)) {
            queryWrapper.eq("ticket_id", ticketId);
        }

        IPage<TicketOrder> page = ticketOrderMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为VO
        List<TicketOrderVO> voList = page.getRecords().stream().map(this::convertVO).collect(Collectors.toList());
        

        return new PageResult<>(voList, page.getCurrent(), page.getSize(), page.getTotal());
    }

    @Override
    public List<TicketOrder> getAll() {
        QueryWrapper<TicketOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return ticketOrderMapper.selectList(queryWrapper);
    }

    @Override
    public TicketOrder getByIdDetail(Integer id) {
        return ticketOrderMapper.selectById(id);
    }

    @Override
    public List<TicketOrderVO> selectByUserId(Integer userId) {
        QueryWrapper<TicketOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<TicketOrder> ticketOrderList = ticketOrderMapper.selectList(queryWrapper);
        return ticketOrderList.stream().map(this::convertVO).collect(Collectors.toList());
    }

    @Override
    public Boolean updateStatus(Integer id, Integer status) {
        TicketOrder ticketOrder = new TicketOrder();
        ticketOrder.setId(id);
        ticketOrder.setStatus(status);
        // 如果状态是取消，需要恢复库存数量
        if (status == 3) {
            Ticket ticket = ticketMapper.selectById(ticketOrder.getTicketId());
            if (ticket == null) {
                throw new BusinessException(CodeEnum.SYSTEM_ERROR, "门票不存在");
            }
            // 增加库存
            ticket.setInventory(ticket.getInventory() + ticketOrder.getPayNum());
            ticketMapper.updateById(ticket);
        }
        return ticketOrderMapper.updateById(ticketOrder) > 0;
    }

    @Override
    public List<TicketOrderVO> selectByTicketId(Integer ticketId) {
        QueryWrapper<TicketOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ticket_id", ticketId);
        queryWrapper.orderByDesc("create_time");
        List<TicketOrder> ticketOrderList = ticketOrderMapper.selectList(queryWrapper);
        return ticketOrderList.stream().map(this::convertVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO对象
     */
    private TicketOrderVO convertVO(TicketOrder ticketOrder) {
        TicketOrderVO vo = new TicketOrderVO();
        BeanUtils.copyProperties(ticketOrder, vo);
        
        // 设置用户名
        if (ObjectUtil.isNotNull(ticketOrder.getUserId())) {
            User user = userMapper.selectById(ticketOrder.getUserId());
            if (ObjectUtil.isNotNull(user)) {
                vo.setUserName(user.getNickName());
            }
        }
        
        // 设置门票信息
        if (ObjectUtil.isNotNull(ticketOrder.getTicketId())) {
            Ticket ticket = ticketMapper.selectById(ticketOrder.getTicketId());
            if (ObjectUtil.isNotNull(ticket)) {
                vo.setTicketName(ticket.getName());
                vo.setTypeName(getTypeName(ticket.getType()));
                
                // 设置景点名称
                if (ObjectUtil.isNotNull(ticket.getAttractionId())) {
                    Attraction attraction = attractionMapper.selectById(ticket.getAttractionId());
                    if (ObjectUtil.isNotNull(attraction)) {
                        vo.setAttractionName(attraction.getName());
                    }
                }
            }
        }
        
        // 设置订单状态名称
        vo.setStatusName(getStatusName(ticketOrder.getStatus()));
        
        return vo;
    }

    /**
     * 获取票种类型名称
     */
    private String getTypeName(Integer type) {
        if (ObjectUtil.isNull(type)) {
            return "";
        }
        switch (type) {
            case 1:
                return "成人票";
            case 2:
                return "儿童票";
            case 3:
                return "学生票";
            case 4:
                return "老人票";
            default:
                return "其他";
        }
    }

    /**
     * 获取订单状态名称
     */
    private String getStatusName(Integer status) {
        if (ObjectUtil.isNull(status)) {
            return "";
        }
        switch (status) {
            case 1:
                return "已付款";
            case 2:
                return "已完成";
            case 3:
                return "已取消";
            default:
                return "未知状态";
        }
    }
}