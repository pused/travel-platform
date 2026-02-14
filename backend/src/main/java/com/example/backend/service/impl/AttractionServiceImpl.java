package com.example.backend.service.impl;

import com.example.backend.entity.Attraction;
import com.example.backend.entity.Category;
import com.example.backend.entity.vo.attraction.AttractionVO;
import com.example.backend.mapper.AttractionMapper;
import com.example.backend.service.IAttractionService;
import com.example.backend.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * 景点表 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements IAttractionService {
    @Resource
    private AttractionMapper attractionMapper;
    @Resource
    private ICategoryService categoryService;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Override
    public Integer add(Attraction request) {
        Attraction saveData = new Attraction();
        BeanUtils.copyProperties(request, saveData);
        attractionMapper.insert(saveData);
        return saveData.getId();
    }

    /**
     * 批量新增
     *
     * @param request
     * @return
     */
    @Override
    public Boolean batchAdd(List<Attraction> request) {
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
    public Boolean edit(Attraction request) {
        return updateById(request);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public PageResult<List<AttractionVO>> queryPage(Integer pageNum, Integer pageSize, String name, Integer categoryId) {
        QueryWrapper<Attraction> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq("category_id", categoryId);
        }
        // ID 降序
        queryWrapper.orderByDesc("id");
        Page<Attraction> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回结果
        List<AttractionVO> attractionVOS = convertVO(page.getRecords());
        return new PageResult<>(attractionVOS, page.getCurrent(), page.getSize(), page.getTotal());
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<Attraction> getAll() {
        return list();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public Attraction getByIdDetail(Integer id) {
        Attraction attraction = getById(id);
        // 增加浏览数
        if (attraction != null) {
            attraction.setViewCount(attraction.getViewCount() + 1);
            updateById(attraction);
        }
        return attraction;
    }


    // 转换VO
    private List<AttractionVO> convertVO(List<Attraction> attractions) {
        ArrayList<AttractionVO> list = new ArrayList<>();
        for (Attraction attraction : attractions) {
            AttractionVO attractionVO = new AttractionVO();
            BeanUtils.copyProperties(attraction, attractionVO);
            // 获取分类信息
            if (attraction.getCategoryId() != null) {
                Category category = categoryService.getById(attraction.getCategoryId());
                if (category != null) {
                    attractionVO.setCategoryName(category.getName());
                }
            }
            list.add(attractionVO);
        }
        return list;
    }
}