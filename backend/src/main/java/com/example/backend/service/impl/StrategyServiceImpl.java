package com.example.backend.service.impl;

import com.example.backend.entity.Strategy;
import com.example.backend.entity.User;
import com.example.backend.entity.Attraction;
import com.example.backend.entity.vo.strategy.StrategyVO;
import com.example.backend.entity.vo.user.UserLoginVO;
import com.example.backend.mapper.StrategyMapper;
import com.example.backend.service.IStrategyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.service.IUserService;
import com.example.backend.service.IAttractionService;
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
 * 旅游攻略表 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements IStrategyService {
    @Resource
    private StrategyMapper strategyMapper;
    @Resource
    private IUserService userService;
    @Resource
    private IAttractionService attractionService;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Override
    public Integer add(Strategy request) {
        Strategy saveData = new Strategy();
        BeanUtils.copyProperties(request, saveData);
        UserLoginVO currentUserInfo = userService.getCurrentUserInfo();
        saveData.setUserId(currentUserInfo.getId());
        strategyMapper.insert(saveData);
        return saveData.getId();
    }

    /**
     * 批量新增
     *
     * @param request
     * @return
     */
    @Override
    public Boolean batchAdd(List<Strategy> request) {
        // 获取当前登录用户信息
        UserLoginVO currentUserInfo = userService.getCurrentUserInfo();
        if (currentUserInfo != null) {
            request.forEach(item -> {
                item.setUserId(currentUserInfo.getId());
            });
        }
        return saveBatch(request);
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
        return removeByIds(ids);
    }

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @Override
    public Boolean edit(Strategy request) {
        return updateById(request);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public PageResult<List<StrategyVO>> queryPage(Integer pageNum, Integer pageSize, String title, String budgetType, Integer userId) {
        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        if (CharSequenceUtil.isNotBlank(budgetType)) {
            queryWrapper.eq("budget_type", budgetType);
        }
        if (userId != null && userId > 0) {
            queryWrapper.eq("user_id", userId);
        }
        // ID 降序
        queryWrapper.orderByDesc("id");
        Page<Strategy> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回结果
        List<StrategyVO> strategyVOS = convertVO(page.getRecords());
        return new PageResult<>(strategyVOS, page.getCurrent(), page.getSize(), page.getTotal());
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<Strategy> getAll() {
        return list();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public Strategy getByIdDetail(Integer id) {
        return getById(id);
    }


    // 转换VO
    private List<StrategyVO> convertVO(List<Strategy> strategies) {
        ArrayList<StrategyVO> list = new ArrayList<>();
        for (Strategy strategy : strategies) {
            StrategyVO strategyVO = new StrategyVO();
            BeanUtils.copyProperties(strategy, strategyVO);
            // 获取作者信息
            if (strategy.getUserId() != null) {
                User user = userService.getById(strategy.getUserId());
                if (user != null) {
                    strategyVO.setAuthorName(user.getNickName());
                }
            }
            // 获取景点信息
            if (strategy.getAttractionId() != null) {
                Attraction attraction = attractionService.getById(strategy.getAttractionId());
                if (attraction != null) {
                    strategyVO.setAttractionName(attraction.getName());
                }
            }
            list.add(strategyVO);
        }
        return list;
    }
}