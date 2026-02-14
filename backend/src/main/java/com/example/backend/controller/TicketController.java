package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.ticket.TicketVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.ITicketService;
import com.example.backend.entity.Ticket;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 景点门票表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Resource
    private ITicketService ticketService;

    @PostMapping("/add")
    @ApiOperation(value = "新增门票")
    public BaseResponse<Integer> add(@RequestBody Ticket request) {
        Integer res = ticketService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    @ApiOperation(value = "批量新增门票")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Ticket> request) {
        Boolean res = ticketService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    @ApiOperation(value = "删除门票")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = ticketService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除门票")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = ticketService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑门票")
    private BaseResponse<Boolean> edit(@RequestBody Ticket request) {
        Boolean res = ticketService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询门票")
    public BaseResponse<PageResult<List<TicketVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer attractionId,
            @RequestParam(required = false) Integer type
    ) {
        PageResult<List<TicketVO>> res = ticketService.queryPage(pageNum, pageSize, name, attractionId, type);
        return Result.success(res);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询所有门票")
    public BaseResponse<List<Ticket>> all() {
        List<Ticket> res = ticketService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    @ApiOperation(value = "根据ID查询门票详情")
    public BaseResponse<Ticket> getById(@RequestParam Integer id) {
        Ticket res = ticketService.getByIdDetail(id);
        return Result.success(res);
    }

    @GetMapping("/getByAttractionId")
    @ApiOperation(value = "根据攻略ID查询门票列表")
    public BaseResponse<List<TicketVO>> getByAttractionId(@RequestParam Integer attractionId) {
        List<TicketVO> res = ticketService.getByAttractionId(attractionId);
        return Result.success(res);
    }
}