package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.Attraction;
import com.example.backend.entity.Like;
import com.example.backend.entity.Strategy;
import com.example.backend.entity.vo.like.LikeVO;
import com.example.backend.mapper.LikeMapper;
import com.example.backend.service.IAttractionService;
import com.example.backend.service.ILikeService;
import com.example.backend.service.IStrategyService;
import com.example.backend.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 用户点赞表服务层实现类
 * @Version 1.0
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements ILikeService {

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private IUserService userService;

    @Resource
    private IAttractionService attractionService;

    @Resource
    private IStrategyService strategyService;

    @Override
    public Integer add(Like like) {
        like.setCreateTime(LocalDateTime.now());
        // 更新对应数据的点赞数量
        updateLikeCount(like.getTargetType(), like.getTargetId(), 1);
        return likeMapper.insert(like);
    }

    /**
     * 更新对应数据的点赞数量
     * @param targetType 目标类型（1：景点，2：攻略）
     * @param targetId 目标ID
     * @param count 点赞数量（增加或减少）
     */
    @Override
    public void updateLikeCount(Integer targetType, Integer targetId, Integer count) {
        if (targetType == 1) {
            Attraction attraction = attractionService.getById(targetId);
            if (attraction != null) {
                attraction.setLikeCount(attraction.getLikeCount() + count);
                attractionService.updateById(attraction);
            }
        } else if (targetType == 2) {
            Strategy strategy = strategyService.getById(targetId);
            if (strategy != null) {
                strategy.setLikeCount(strategy.getLikeCount() + count);
                strategyService.updateById(strategy);
            }
        }
    }

    @Override
    public Boolean batchAdd(List<Like> likes) {
        for (Like like : likes) {
            like.setCreateTime(LocalDateTime.now());
        }
        // 批量新增点赞时，更新对应数据的点赞数量
        for (Like like : likes) {
            updateLikeCount(like.getTargetType(), like.getTargetId(), 1);
        }
        return this.saveBatch(likes);
    }

    @Override
    public Boolean del(Integer id) {
        Like like = likeMapper.selectById(id);
        if (like == null) {
            return false;
        }
        int result = likeMapper.deleteById(id);
        // 更新对应数据的点赞数量
        if (result > 0) {
            updateLikeCount(like.getTargetType(), like.getTargetId(), -1);
        }
        return result > 0;
    }

    @Override
    public Boolean batchDel(List<Integer> ids) {
        // 批量删除点赞时，更新对应数据的点赞数量
        for (Integer id : ids) {
            Like like = likeMapper.selectById(id);
            if (like != null) {
                updateLikeCount(like.getTargetType(), like.getTargetId(), -1);
            }
        }
        return likeMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public Boolean edit(Like like) {
        return likeMapper.updateById(like) > 0;
    }

    @Override
    public PageResult<List<LikeVO>> queryPage(Integer pageNum, Integer pageSize, Integer targetType, Integer userId) {
        Page<Like> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        
        if (targetType != null) {
            queryWrapper.eq("target_type", targetType);
        }
        if (userId != null) {
            // 前台会传入当前用户 ID，只查询自己的点赞记录；后台若查看全部则不传
            queryWrapper.eq("user_id", userId);
        }
        
        queryWrapper.orderByDesc("id");
        
        Page<Like> pageResult = likeMapper.selectPage(page, queryWrapper);
        
        List<LikeVO> likeVOList = pageResult.getRecords().stream()
                .map(this::convertVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(
                likeVOList,
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getTotal()
        );
    }

    @Override
    public List<Like> getAll() {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return likeMapper.selectList(queryWrapper);
    }

    @Override
    public Like getByIdDetail(Integer id) {
        return likeMapper.selectById(id);
    }

    @Override
    public Boolean isLiked(Integer userId, Integer targetType, Integer targetId) {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_type", targetType)
                   .eq("target_id", targetId);
        return likeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Boolean cancelLike(Integer userId, Integer targetType, Integer targetId) {
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_type", targetType)
                   .eq("target_id", targetId);
        // 更新对应数据的点赞数量
        updateLikeCount(targetType, targetId, -1);
        return likeMapper.delete(queryWrapper) > 0;
    }

    @Override
    public Boolean addLike(Integer userId, Integer targetType, Integer targetId) {
        // 检查是否已经点赞
        if (isLiked(userId, targetType, targetId)) {
            return false;
        }
        
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        like.setCreateTime(LocalDateTime.now());
        // 更新对应数据的点赞数量
        updateLikeCount(targetType, targetId, 1);
        return likeMapper.insert(like) > 0;
    }

    /**
     * 转换为VO对象
     */
    private LikeVO convertVO(Like like) {
        LikeVO likeVO = new LikeVO();
        BeanUtils.copyProperties(like, likeVO);
        
        // 设置用户名称
        if (like.getUserId() != null) {
            try {
                likeVO.setUserName(userService.getById(like.getUserId()).getNickName());
            } catch (Exception e) {
                likeVO.setUserName("未知用户");
            }
        }
        
        // 设置目标名称和类型名称
        if (like.getTargetType() != null && like.getTargetId() != null) {
            if (like.getTargetType() == 1) {
                // 景点
                likeVO.setTargetTypeName("景点");
                try {
                    likeVO.setTargetName(attractionService.getById(like.getTargetId()).getName());
                } catch (Exception e) {
                    likeVO.setTargetName("未知景点");
                }
            } else if (like.getTargetType() == 2) {
                // 攻略
                likeVO.setTargetTypeName("攻略");
                try {
                    likeVO.setTargetName(strategyService.getById(like.getTargetId()).getTitle());
                } catch (Exception e) {
                    likeVO.setTargetName("未知攻略");
                }
            }
        }
        
        return likeVO;
    }
}