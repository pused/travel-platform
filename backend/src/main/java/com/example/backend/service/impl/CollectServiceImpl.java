package com.example.backend.service.impl;

import com.example.backend.entity.Collect;
import com.example.backend.entity.Strategy;
import com.example.backend.entity.User;
import com.example.backend.entity.Attraction;
import com.example.backend.entity.vo.collect.CollectVO;
import com.example.backend.mapper.CollectMapper;
import com.example.backend.service.ICollectService;
import com.example.backend.service.IStrategyService;
import com.example.backend.service.IUserService;
import com.example.backend.service.IAttractionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
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
 * 用户收藏表 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements ICollectService {
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private IUserService userService;
    @Resource
    private IAttractionService attractionService;
    @Resource
    private IStrategyService strategyService;

    /**
     * 新增收藏
     *
     * @param request
     * @return
     */
    @Override
    public Integer add(Collect request) {
        Collect saveData = new Collect();
        BeanUtils.copyProperties(request, saveData);
        int res = collectMapper.insert(saveData);
        // 更新对应数据的收藏数量
        updateCollectCount(request.getTargetType(), request.getTargetId(), 1);

        if (res > 0) {
            return saveData.getId();
        } else {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 批量新增
     *
     * @param request
     * @return
     */
    @Override
    public Boolean batchAdd(List<Collect> request) {
        boolean saveBatch = saveBatch(request);
        if (!saveBatch) {
            throw new BusinessException(CodeEnum.SYSTEM_ERROR, "批量新增收藏失败");
        }
        // 更新对应数据的收藏数量
        for (Collect collect : request) {
            updateCollectCount(collect.getTargetType(), collect.getTargetId(), 1);
        }
        return saveBatch;
    }

    /**
     * 根据类型更新对应数据的收藏数量
     * @param targetType 收藏类型（1：景点，2：攻略）
     * @param targetId   收藏目标ID
     * @param delta      增量（1：增加，-1：减少）
     * @return 是否成功
     */
    @Override
    public Boolean updateCollectCount(Integer targetType, Integer targetId, Integer delta) {
        if (targetType == 1) {
            Attraction attraction = attractionService.getById(targetId);
            if (attraction != null) {
                attraction.setCollectCount(attraction.getCollectCount() + delta);
                return attractionService.updateById(attraction);
            }
        } else if (targetType == 2) {
            Strategy strategy = strategyService.getById(targetId);
            if (strategy != null) {
                strategy.setCollectCount(strategy.getCollectCount() + delta);
                return strategyService.updateById(strategy);
            }
        }
        return false;
    }

    /**
     * 删除收藏
     *
     * @param id
     * @return
     */
    @Override
    public Boolean del(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        Collect collect = getById(id);
        if (collect == null) {
            throw new BusinessException(CodeEnum.NULL_ERROR, "收藏不存在");
        }
        // 更新对应数据的收藏数量
        updateCollectCount(collect.getTargetType(), collect.getTargetId(), -1);
        return removeById(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean batchDel(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        // 遍历删除的收藏，更新对应数据的收藏数量
        for (Integer id : ids) {
            Collect collect = getById(id);
            if (collect != null) {
                updateCollectCount(collect.getTargetType(), collect.getTargetId(), -1);
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
    public Boolean edit(Collect request) {
        return updateById(request);
    }

    /**
     * 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param targetType
     * @return
     */
    @Override
    public PageResult<List<CollectVO>> queryPage(Integer pageNum, Integer pageSize, Integer targetType, Integer userId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        if (targetType != null) {
            queryWrapper.eq("target_type", targetType);
        }
        if (userId != null) {
            // 前台会传入当前用户 ID，只查询自己的收藏记录；后台若查看全部则不传
            queryWrapper.eq("user_id", userId);
        }
        // ID 降序
        queryWrapper.orderByDesc("id");
        Page<Collect> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回结果
        List<CollectVO> collectVOS = convertVO(page.getRecords());
        return new PageResult<>(collectVOS, page.getCurrent(), page.getSize(), page.getTotal());
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<Collect> getAll() {
        return list();
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public Collect getByIdDetail(Integer id) {
        return getById(id);
    }

    /**
     * 检查用户是否收藏了某个目标
     *
     * @param userId
     * @param targetType
     * @param targetId
     * @return
     */
    @Override
    public Boolean isCollected(Integer userId, Integer targetType, Integer targetId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_type", targetType)
                   .eq("target_id", targetId);
        return count(queryWrapper) > 0;
    }

    /**
     * 取消收藏
     *
     * @param userId
     * @param targetType
     * @param targetId
     * @return
     */
    @Override
    public Boolean cancelCollect(Integer userId, Integer targetType, Integer targetId) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_type", targetType)
                   .eq("target_id", targetId);
        // 检查是否存在收藏记录
        if (count(queryWrapper) == 0) {
            return false; // 未收藏，无法取消
        }

        // 更新对应数据的收藏数量
        updateCollectCount(targetType, targetId, -1);

        return remove(queryWrapper);
    }

    /**
     * 添加收藏
     *
     * @param userId
     * @param targetType
     * @param targetId
     * @return
     */
    @Override
    public Boolean addCollect(Integer userId, Integer targetType, Integer targetId) {
        // 检查是否已收藏
        if (isCollected(userId, targetType, targetId)) {
            return false; // 已收藏，不重复添加
        }
        
        Collect collect = new Collect();
        collect.setUserId(userId);
        collect.setTargetType(targetType);
        collect.setTargetId(targetId);
        // 更新对应数据的收藏数量
        updateCollectCount(targetType, targetId, 1);
        return save(collect);
    }

    // 转换VO
    private List<CollectVO> convertVO(List<Collect> collects) {
        ArrayList<CollectVO> list = new ArrayList<>();
        for (Collect collect : collects) {
            CollectVO collectVO = new CollectVO();
            BeanUtils.copyProperties(collect, collectVO);
            
            // 获取用户信息
            if (collect.getUserId() != null) {
                User user = userService.getById(collect.getUserId());
                if (user != null) {
                    collectVO.setUserName(user.getNickName());
                }
            }
            
            // 根据收藏类型获取目标信息
            if (collect.getTargetType() != null && collect.getTargetId() != null) {
                if (collect.getTargetType() == 1) { // 景点
                    collectVO.setTargetTypeName("景点");
                    Attraction attraction = attractionService.getById(collect.getTargetId());
                    if (attraction != null) {
                        collectVO.setTargetName(attraction.getName());
                    }
                } else if (collect.getTargetType() == 2) { // 攻略
                    collectVO.setTargetTypeName("攻略");
                    // 这里需要根据实际的攻略服务来获取攻略名称
                    // Strategy strategy = strategyService.getById(collect.getTargetId());
                    // if (strategy != null) {
                    //     collectVO.setTargetName(strategy.getTitle());
                    // }
                    collectVO.setTargetName("攻略-" + collect.getTargetId());
                }
            }
            
            list.add(collectVO);
        }
        return list;
    }
}