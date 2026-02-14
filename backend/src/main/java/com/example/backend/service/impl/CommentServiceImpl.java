package com.example.backend.service.impl;

import com.example.backend.entity.Comment;
import com.example.backend.entity.Strategy;
import com.example.backend.entity.User;
import com.example.backend.entity.vo.comment.CommentVO;
import com.example.backend.entity.vo.user.UserLoginVO;
import com.example.backend.mapper.CommentMapper;
import com.example.backend.service.ICommentService;
import com.example.backend.service.IStrategyService;
import com.example.backend.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.model.PageResult;
import com.example.backend.exception.BusinessException;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

import com.example.backend.common.enums.CodeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private IUserService userService;
    @Resource
    private IStrategyService strategyService;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Override
    public Integer add(Comment request) {
        Comment saveData = new Comment();
        BeanUtils.copyProperties(request, saveData);
        UserLoginVO currentUserInfo = userService.getCurrentUserInfo();
        saveData.setUserId(currentUserInfo.getId());
        commentMapper.insert(saveData);
        // 更新攻略评论数
        updateStrategyCommentCount(saveData.getStrategyId(), 1);
        return saveData.getId();
    }

    /**
     * 更新攻略评论数
     *
     * @param strategyId 攻略ID
     */
    @Override
    public void updateStrategyCommentCount(Integer strategyId, Integer commentCount) {
        // 更新攻略评论数
        Strategy strategy = strategyService.getById(strategyId);
        if (strategy != null) {
            strategy.setCommentCount(strategy.getCommentCount() + 1);
            strategyService.updateById(strategy);
        }
    }

    /**
     * 批量新增
     *
     * @param request
     * @return
     */
    @Override
    public Boolean batchAdd(List<Comment> request) {
        // 获取当前登录用户信息
        UserLoginVO currentUserInfo = userService.getCurrentUserInfo();
        if (currentUserInfo != null) {
            request.forEach(item -> {
                item.setUserId(currentUserInfo.getId());
            });
        }
        boolean saveBatch = saveBatch(request);
        if (!saveBatch) {
            throw new BusinessException(CodeEnum.NULL_ERROR, "新增失败");
        }
        // 遍历新增的评论，更新对应攻略的评论数
        for (Comment comment : request) {
            updateStrategyCommentCount(comment.getStrategyId(), 1);
        }
        return saveBatch;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public Boolean del(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        // 删除评论
        Comment comment = getById(id);
        if (comment == null) {
            throw new BusinessException(CodeEnum.NULL_ERROR, "评论不存在");
        }
        // 更新攻略评论数
        updateStrategyCommentCount(comment.getStrategyId(), -1);
        return removeById(id);
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID列表
     * @return 是否成功
     */
    @Override
    public Boolean batchDel(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        // 遍历删除的评论，更新对应攻略的评论数
        for (Integer commentId : ids) {
            Comment comment = getById(commentId);
            if (comment != null) {
                updateStrategyCommentCount(comment.getStrategyId(), -1);
            }
        }
        return removeByIds(ids);
    }

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @Override
    public Boolean edit(Comment request) {
        return updateById(request);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public PageResult<List<CommentVO>> queryPage(Integer pageNum, Integer pageSize, String content, Integer strategyId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(content)) {
            queryWrapper.like("content", content);
        }
        if (strategyId != null && strategyId > 0) {
            queryWrapper.eq("strategy_id", strategyId);
        }
        // ID 降序
        queryWrapper.orderByDesc("id");
        Page<Comment> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回结果
        List<CommentVO> commentVOS = convertVO(page.getRecords());
        return new PageResult<>(commentVOS, page.getCurrent(), page.getSize(), page.getTotal());
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<Comment> getAll() {
        return list();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public Comment getByIdDetail(Integer id) {
        return getById(id);
    }


    // 转换VO
    private List<CommentVO> convertVO(List<Comment> comments) {
        ArrayList<CommentVO> list = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            // 获取用户信息
            if (comment.getUserId() != null) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    commentVO.setUserName(user.getNickName());
                    commentVO.setUserAvatar(user.getAvatar());
                }
            }
            // 获取景点信息
            if (comment.getStrategyId() != null) {
                Strategy strategy = strategyService.getById(comment.getStrategyId());
                if (strategy != null) {
                    commentVO.setStrategyName(strategy.getTitle());
                }
            }
            list.add(commentVO);
        }
        return list;
    }
}