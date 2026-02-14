package com.example.backend.service;

import com.example.backend.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.vo.comment.CommentVO;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author 
 * 
 */
public interface ICommentService extends IService<Comment> {
        /**
        * 新增
        * @param request
        * @return
        */
        Integer add(Comment request);

        /**
         * 更新攻略评论数
         *
         * @param strategyId 攻略ID
         * @param commentCount 评论数
         */
        void updateStrategyCommentCount(Integer strategyId, Integer commentCount);

        /**
         * 批量新增
         *
         * @param request
         * @return
         */
        Boolean batchAdd(List<Comment> request);

        /**
         * 删除
         * @param id
         * @return
         */
        Boolean del(Integer id);
        
        /**
         * 批量删除
         * @param ids 批量ID列表
         * @return 是否成功
         */
        Boolean batchDel(List<Integer> ids);

        /**
         * 编辑
         * @param request 请求参数
         * @return 是否成功
         */
        Boolean edit(Comment request);

        /**
         * 分页条件查询
         * @param pageNum 页码
         * @param pageSize 页大小
         * @param content 评论内容
         * @param strategyId 攻略ID
         * @return 分页结果
         */
        PageResult<List<CommentVO>> queryPage(Integer pageNum, Integer pageSize, String content, Integer strategyId);

        /**
         * 查询全部
         * @return 全部数据
         */
        List<Comment> getAll();

        /**
         * 根据ID查询详情
         * @param id ID
         * @return 详情数据
         */
        Comment getByIdDetail(Integer id);
}