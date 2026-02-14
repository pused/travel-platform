package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Ticket;
import com.example.backend.entity.vo.ticket.TicketVO;

import java.util.List;

/**
 * @Description 景点门票表服务接口
 * @Version 1.0
 */
public interface ITicketService extends IService<Ticket> {

    /**
     * 新增门票
     */
    Integer add(Ticket ticket);

    /**
     * 批量新增门票
     */
    Boolean batchAdd(List<Ticket> tickets);

    /**
     * 删除门票
     */
    Boolean del(Integer id);

    /**
     * 批量删除门票
     */
    Boolean batchDel(List<Integer> ids);

    /**
     * 编辑门票
     */
    Boolean edit(Ticket ticket);

    /**
     * 分页查询门票
     */
    PageResult<List<TicketVO>> queryPage(Integer pageNum, Integer pageSize, String name, Integer attractionId, Integer type);

    /**
     * 查询所有门票
     */
    List<Ticket> getAll();

    /**
     * 根据ID查询门票详情
     */
    Ticket getByIdDetail(Integer id);

    /**
     * 根据攻略ID查询门票列表
     */
    List<TicketVO> getByAttractionId(Integer attractionId);
}