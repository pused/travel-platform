package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.TicketOrder;
import com.example.backend.entity.vo.ticketorder.TicketOrderVO;
import com.example.backend.service.ITicketOrderService;
import com.example.backend.utils.OrderExpirationTask;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 门票订单表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/ticketOrder")
public class TicketOrderController {

    @Resource
    private ITicketOrderService ticketOrderService;
    @Resource
    private OrderExpirationTask orderExpirationTask;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody TicketOrder request) {
        Integer res = ticketOrderService.add(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = ticketOrderService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除门票订单")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = ticketOrderService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> edit(@RequestBody TicketOrder request) {
        Boolean res = ticketOrderService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<TicketOrderVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer ticketId
    ) {
        PageResult<List<TicketOrderVO>> res = ticketOrderService.queryPage(pageNum, pageSize, orderNo, contactName, contactPhone, status, userId, ticketId);
        return Result.success(res);
    }

    @GetMapping("/list")
    public BaseResponse<List<TicketOrder>> all() {
        List<TicketOrder> res = ticketOrderService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    public BaseResponse<TicketOrder> getById(@RequestParam Integer id) {
        TicketOrder res = ticketOrderService.getByIdDetail(id);
        return Result.success(res);
    }

    @GetMapping("/selectByUserId")
    public BaseResponse<List<TicketOrderVO>> selectByUserId(@RequestParam Integer userId) {
        List<TicketOrderVO> res = ticketOrderService.selectByUserId(userId);
        return Result.success(res);
    }

    @GetMapping("/selectByTicketId")
    public BaseResponse<List<TicketOrderVO>> selectByTicketId(@RequestParam Integer ticketId) {
        List<TicketOrderVO> res = ticketOrderService.selectByTicketId(ticketId);
        return Result.success(res);
    }

    @PostMapping("/processExpiredOrders")
    @ApiOperation(value = "手动处理过期订单")
    public BaseResponse<String> processExpiredOrders() {
        orderExpirationTask.manualProcessExpiredOrders();
        return Result.success("过期订单处理完成");
    }
}