package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.TicketOrder;
import com.example.backend.entity.vo.ticketorder.TicketOrderVO;

import java.util.List;

/**
 * @Description 门票订单表Service接口
 * @Version 1.0
 */
public interface ITicketOrderService extends IService<TicketOrder> {
    /**
     * 新增门票订单
     * @param ticketOrder 门票订单信息
     * @return 门票订单ID
     */
    Integer add(TicketOrder ticketOrder);

    /**
     * 删除门票订单
     * @param id 门票订单ID
     * @return 是否成功
     */
    Boolean del(Integer id);

    /**
     * 批量删除门票订单
     * @param ids 门票订单ID列表
     * @return 是否成功
     */
    Boolean batchDel(List<Integer> ids);

    /**
     * 编辑门票订单
     * @param ticketOrder 门票订单信息
     * @return 是否成功
     */
    Boolean edit(TicketOrder ticketOrder);

    /**
     * 分页查询门票订单
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param orderNo 订单号
     * @param contactName 联系人姓名
     * @param contactPhone 联系人手机号
     * @param status 订单状态
     * @param userId 用户ID
     * @param ticketId 门票ID
     * @return 分页结果
     */
    PageResult<List<TicketOrderVO>> queryPage(Integer pageNum, Integer pageSize, String orderNo, String contactName, String contactPhone, Integer status, Integer userId, Integer ticketId);

    /**
     * 查询所有门票订单
     * @return 门票订单列表
     */
    List<TicketOrder> getAll();

    /**
     * 根据ID查询门票订单详情
     * @param id 门票订单ID
     * @return 门票订单详情
     */
    TicketOrder getByIdDetail(Integer id);

    /**
     * 根据用户ID查询门票订单列表
     * @param userId 用户ID
     * @return 门票订单列表
     */
    List<TicketOrderVO> selectByUserId(Integer userId);


    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 是否成功
     */
    Boolean updateStatus(Integer id, Integer status);


     /**
     * 根据门票ID查询门票订单列表
     * @param ticketId 门票ID
     * @return 门票订单列表
     */
    List<TicketOrderVO> selectByTicketId(Integer ticketId);
}