package com.example.backend.service.impl;

import com.example.backend.entity.Category;
import com.example.backend.entity.vo.category.CategoryVO;
import com.example.backend.mapper.CategoryMapper;
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
 * 景点分类表 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Override
    public Integer add(Category request) {
        Category saveData = new Category();
        BeanUtils.copyProperties(request, saveData);
        categoryMapper.insert(saveData);
        return saveData.getId();
    }

    /**
     * 批量新增
     *
     * @param request
     * @return
     */
    @Override
    public Boolean batchAdd(List<Category> request) {
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
    public Boolean edit(Category request) {
        return updateById(request);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public PageResult<List<CategoryVO>> queryPage(Integer pageNum, Integer pageSize, String name) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        // ID 降序
        queryWrapper.orderByDesc("id");
        Page<Category> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回结果
        List<CategoryVO> categoryVOS = convertVO(page.getRecords());
        return new PageResult<>(categoryVOS, page.getCurrent(), page.getSize(), page.getTotal());
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<Category> getAll() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        // ID 降序
        queryWrapper.orderByDesc("id");
        return list(queryWrapper);
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public Category getByIdDetail(Integer id) {
        return getById(id);
    }


    // 转换VO
    private List<CategoryVO> convertVO(List<Category> categories) {
        ArrayList<CategoryVO> list = new ArrayList<>();
        for (Category category : categories) {
            CategoryVO categoryVO = new CategoryVO();
            BeanUtils.copyProperties(category, categoryVO);
            list.add(categoryVO);
        }
        return list;
    }
}