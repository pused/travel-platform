package com.example.backend.controller;

import com.example.backend.common.model.PageResult;
import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.comment.CommentVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.backend.service.ICommentService;
import com.example.backend.entity.Comment;

import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 评论表控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

    @PostMapping("/add")
    public BaseResponse<Integer> add(@RequestBody Comment request) {
        Integer res = commentService.add(request);
        return Result.success(res);
    }

    @PostMapping("/batchAdd")
    public BaseResponse<Boolean> batchAdd(@RequestBody List<Comment> request) {
        Boolean res = commentService.batchAdd(request);
        return Result.success(res);
    }

    @GetMapping("/del")
    public BaseResponse<Boolean> del(@RequestParam Integer id) {
        Boolean res = commentService.del(id);
        return Result.success(res);
    }

    @PostMapping("/batchDel")
    @ApiOperation(value = "批量删除评论")
    public BaseResponse<Boolean> batchDel(@RequestBody List<Integer> ids) {
        Boolean res = commentService.batchDel(ids);
        return Result.success(res);
    }

    @PostMapping("/edit")
    private BaseResponse<Boolean> edit(@RequestBody Comment request) {
        Boolean res = commentService.edit(request);
        return Result.success(res);
    }

    @GetMapping("/page")
    public BaseResponse<PageResult<List<CommentVO>>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Integer strategyId
    ) {
        PageResult<List<CommentVO>> res = commentService.queryPage(pageNum, pageSize, content, strategyId);
        return Result.success(res);
    }

    @GetMapping("/list")
    public BaseResponse<List<Comment>> all() {
        List<Comment> res = commentService.getAll();
        return Result.success(res);
    }

    @GetMapping("/getById")
    public BaseResponse<Comment> getById(@RequestParam Integer id) {
        Comment res = commentService.getByIdDetail(id);
        return Result.success(res);
    }
}