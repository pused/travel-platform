package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.strategy.StrategyVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.IStrategyService;
import com.example.backend.entity.Strategy;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 旅游攻略表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    @Resource
    private IStrategyService strategyService;

    @PostMapping("/add")
    @ApiOperation(value = "新增旅游攻略")
    public BaseResponse<Integer> add(@RequestBody Strategy request) {
        Integer res = strategyService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    @ApiOperation(value = "批量新增旅游攻略")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Strategy> request) {
        Boolean res = strategyService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    @ApiOperation(value = "删除旅游攻略")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = strategyService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除旅游攻略")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = strategyService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑旅游攻略")
    private BaseResponse<Boolean> edit(@RequestBody Strategy request) {
        Boolean res = strategyService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询旅游攻略")
    public BaseResponse<PageResult<List<StrategyVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String budgetType,
            @RequestParam(required = false) Integer userId
    ) {
        PageResult<List<StrategyVO>> res = strategyService.queryPage(pageNum, pageSize, title, budgetType, userId);
        return Result.success(res);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询所有旅游攻略")
    public BaseResponse<List<Strategy>> all() {
        List<Strategy> res = strategyService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    @ApiOperation(value = "根据ID查询旅游攻略详情")
    public BaseResponse<Strategy> getById(@RequestParam Integer id) {
        Strategy res = strategyService.getByIdDetail(id);
        return Result.success(res);
    }
}