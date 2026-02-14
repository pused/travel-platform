package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.collect.CollectVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.ICollectService;
import com.example.backend.entity.Collect;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 用户收藏表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/collect")
public class CollectController {

    @Resource
    private ICollectService collectService;

    @PostMapping("/add")
    @ApiOperation(value = "新增收藏")
    public BaseResponse<Integer> add(@RequestBody Collect request) {
        Integer res = collectService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    @ApiOperation(value = "批量新增收藏")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Collect> request) {
        Boolean res = collectService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    @ApiOperation(value = "删除收藏")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = collectService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除收藏")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = collectService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑收藏")
    private BaseResponse<Boolean> edit(@RequestBody Collect request) {
        Boolean res = collectService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询收藏")
    public BaseResponse<PageResult<List<CollectVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) Integer targetType,
            @RequestParam(required = false) Integer userId
    ) {
        PageResult<List<CollectVO>> res = collectService.queryPage(pageNum, pageSize, targetType, userId);
        return Result.success(res);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询所有收藏")
    public BaseResponse<List<Collect>> all() {
        List<Collect> res = collectService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    @ApiOperation(value = "根据ID查询收藏详情")
    public BaseResponse<Collect> getById(@RequestParam Integer id) {
        Collect res = collectService.getByIdDetail(id);
        return Result.success(res);
    }

    @GetMapping("/isCollected")
    @ApiOperation(value = "检查是否已收藏")
    public BaseResponse<Boolean> isCollected(
            @RequestParam Integer userId,
            @RequestParam Integer targetType,
            @RequestParam Integer targetId
    ) {
        Boolean res = collectService.isCollected(userId, targetType, targetId);
        return Result.success(res);
    }

    @PostMapping("/cancelCollect")
    @ApiOperation(value = "取消收藏")
    public BaseResponse<Boolean> cancelCollect(
            @RequestParam Integer userId,
            @RequestParam Integer targetType,
            @RequestParam Integer targetId
    ) {
        Boolean res = collectService.cancelCollect(userId, targetType, targetId);
        return Result.success(res);
    }

    @PostMapping("/addCollect")
    @ApiOperation(value = "添加收藏")
    public BaseResponse<Boolean> addCollect(
            @RequestParam Integer userId,
            @RequestParam Integer targetType,
            @RequestParam Integer targetId
    ) {
        Boolean res = collectService.addCollect(userId, targetType, targetId);
        return Result.success(res);
    }
}