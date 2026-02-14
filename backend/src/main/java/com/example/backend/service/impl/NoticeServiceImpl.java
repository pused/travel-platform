package com.example.backend.service.impl;

import com.example.backend.entity.Notice;
import com.example.backend.entity.User;
import com.example.backend.entity.vo.notice.NoticeVO;
import com.example.backend.entity.vo.user.UserLoginVO;
import com.example.backend.mapper.NoticeMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.INoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.service.IUserService;
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
 * 公告表 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private IUserService userService;

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @Override
    public Integer add(Notice request) {
        Notice saveData = new Notice();
        BeanUtils.copyProperties(request, saveData);
        UserLoginVO currentUserInfo = userService.getCurrentUserInfo();
        saveData.setUserId(currentUserInfo.getId());
        noticeMapper.insert(saveData);
        return saveData.getId();
    }

    /**
     * 批量新增
     *
     * @param request
     * @return
     */
    @Override
    public Boolean batchAdd(List<Notice> request) {
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
    public Boolean edit(Notice request) {
        return updateById(request);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @Override
    public PageResult<List<NoticeVO>> queryPage(Integer pageNum, Integer pageSize, String title) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        // ID 降序
        queryWrapper.orderByDesc("id");
        Page<Notice> page = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 返回结果
        List<NoticeVO> noticeVOS = convertVO(page.getRecords());
        return new PageResult<>(noticeVOS, page.getCurrent(), page.getSize(), page.getTotal());
    }

    /**
     * 查询全部
     *
     * @return
     */
    @Override
    public List<Notice> getAll() {
        return list();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public Notice getByIdDetail(Integer id) {
        return getById(id);
    }


    // 转换VO
    private List<NoticeVO> convertVO(List<Notice> notices) {
        ArrayList<NoticeVO> list = new ArrayList<>();
        for (Notice notice : notices) {
            NoticeVO noticeVO = new NoticeVO();
            BeanUtils.copyProperties(notice, noticeVO);
            // 获取发布人信息
            if (notice.getUserId() != null) {
                User user = userService.getById(notice.getUserId());
                if (user != null) {
                    noticeVO.setPublishName(user.getNickName());
                }
            }
            list.add(noticeVO);
        }
        return list;
    }
}
