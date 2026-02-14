package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.attraction.AttractionVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.IAttractionService;
import com.example.backend.entity.Attraction;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 景点表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/attraction")
public class AttractionController {

    @Resource
    private IAttractionService attractionService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody Attraction request) {
        Integer res = attractionService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Attraction> request) {
        Boolean res = attractionService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = attractionService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除景点")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = attractionService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    private BaseResponse<Boolean> edit(@RequestBody Attraction request) {
        Boolean res = attractionService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<AttractionVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId
    ) {
        PageResult<List<AttractionVO>> res = attractionService.queryPage(pageNum, pageSize, name, categoryId);
        return Result.success(res);
    }

    @GetMapping("/list")
    public BaseResponse<List<Attraction>> all() {
        List<Attraction> res = attractionService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    public BaseResponse<Attraction> getById(@RequestParam Integer id) {
        Attraction res = attractionService.getByIdDetail(id);
        return Result.success(res);
    }
}