package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Like;
import com.example.backend.entity.vo.like.LikeVO;

import java.util.List;

/**
 * @Description 用户点赞表服务层接口
 * @Version 1.0
 */
public interface ILikeService extends IService<Like> {

    /**
     * 新增点赞
     */
    Integer add(Like like);

    void updateLikeCount(Integer targetType, Integer targetId, Integer count);

    /**
     * 批量新增点赞
     */
    Boolean batchAdd(List<Like> likes);

    /**
     * 删除点赞
     */
    Boolean del(Integer id);

    /**
     * 批量删除点赞
     */
    Boolean batchDel(List<Integer> ids);

    /**
     * 编辑点赞
     */
    Boolean edit(Like like);

    /**
     * 分页查询点赞
     */
    PageResult<List<LikeVO>> queryPage(Integer pageNum, Integer pageSize, Integer targetType, Integer userId);

    /**
     * 查询所有点赞
     */
    List<Like> getAll();

    /**
     * 根据ID查询点赞详情
     */
    Like getByIdDetail(Integer id);

    /**
     * 检查用户是否已点赞
     */
    Boolean isLiked(Integer userId, Integer targetType, Integer targetId);

    /**
     * 取消点赞
     */
    Boolean cancelLike(Integer userId, Integer targetType, Integer targetId);

    /**
     * 添加点赞
     */
    Boolean addLike(Integer userId, Integer targetType, Integer targetId);
}