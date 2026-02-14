package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.like.LikeVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.ILikeService;
import com.example.backend.entity.Like;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 用户点赞表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/like")
public class LikeController {

    @Resource
    private ILikeService likeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增点赞")
    public BaseResponse<Integer> add(@RequestBody Like request) {
        Integer res = likeService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    @ApiOperation(value = "批量新增点赞")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Like> request) {
        Boolean res = likeService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    @ApiOperation(value = "删除点赞")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = likeService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除点赞")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = likeService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑点赞")
    private BaseResponse<Boolean> edit(@RequestBody Like request) {
        Boolean res = likeService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询点赞")
    public BaseResponse<PageResult<List<LikeVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) Integer targetType,
            @RequestParam(required = false) Integer userId
    ) {
        PageResult<List<LikeVO>> res = likeService.queryPage(pageNum, pageSize, targetType, userId);
        return Result.success(res);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询所有点赞")
    public BaseResponse<List<Like>> all() {
        List<Like> res = likeService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    @ApiOperation(value = "根据ID查询点赞详情")
    public BaseResponse<Like> getById(@RequestParam Integer id) {
        Like res = likeService.getByIdDetail(id);
        return Result.success(res);
    }

    @GetMapping("/isLiked")
    @ApiOperation(value = "检查是否已点赞")
    public BaseResponse<Boolean> isLiked(
            @RequestParam Integer userId,
            @RequestParam Integer targetType,
            @RequestParam Integer targetId
    ) {
        Boolean res = likeService.isLiked(userId, targetType, targetId);
        return Result.success(res);
    }

    @PostMapping("/cancelLike")
    @ApiOperation(value = "取消点赞")
    public BaseResponse<Boolean> cancelLike(
            @RequestParam Integer userId,
            @RequestParam Integer targetType,
            @RequestParam Integer targetId
    ) {
        Boolean res = likeService.cancelLike(userId, targetType, targetId);
        return Result.success(res);
    }

    @PostMapping("/addLike")
    @ApiOperation(value = "添加点赞")
    public BaseResponse<Boolean> addLike(
            @RequestParam Integer userId,
            @RequestParam Integer targetType,
            @RequestParam Integer targetId
    ) {
        Boolean res = likeService.addLike(userId, targetType, targetId);
        return Result.success(res);
    }
}