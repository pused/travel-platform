package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.category.CategoryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.ICategoryService;
import com.example.backend.entity.Category;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 景点分类表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody Category request) {
        Integer res = categoryService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Category> request) {
        Boolean res = categoryService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = categoryService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除景点分类")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = categoryService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    private BaseResponse<Boolean> edit(@RequestBody Category request) {
        Boolean res = categoryService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<CategoryVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String name
    ) {
        PageResult<List<CategoryVO>> res = categoryService.queryPage(pageNum, pageSize, name);
        return Result.success(res);
    }

    @GetMapping("/list")
    public BaseResponse<List<Category>> all() {
        List<Category> res = categoryService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    public BaseResponse<Category> getById(@RequestParam Integer id) {
        Category res = categoryService.getByIdDetail(id);
        return Result.success(res);
    }
}