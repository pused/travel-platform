package com.example.backend.service;

import com.example.backend.entity.Collect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.vo.collect.CollectVO;

import java.util.List;

/**
 * <p>
 * 用户收藏表 服务类
 * </p>
 *
 * @author 
 * 
 */
public interface ICollectService extends IService<Collect> {
        /**
        * 新增收藏
        * @param request
        * @return
        */
        Integer add(Collect request);

        /**
         * 批量新增
         *
         * @param request
         * @return
         */
        Boolean batchAdd(List<Collect> request);

        // 根据类型更新对应数据的收藏数量
        Boolean updateCollectCount(Integer targetType, Integer targetId, Integer delta);

        /**
         * 删除收藏
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
        Boolean edit(Collect request);

        /**
         * 分页查询
         * @param pageNum 页码
         * @param pageSize 页大小
         * @param targetType 收藏类型
         * @param userId 用户ID（前台传入仅查询本人，后台不传即可查看全部）
         * @return 分页结果
         */
        PageResult<List<CollectVO>> queryPage(Integer pageNum, Integer pageSize, Integer targetType, Integer userId);

        /**
         * 查询所有
         * @return 所有收藏记录
         */
        List<Collect> getAll();

        /**
         * 根据ID查询详情
         * @param id ID
         * @return 收藏详情
         */
        Collect getByIdDetail(Integer id);
        
        /**
         * 检查用户是否收藏了某个目标
         * @param userId 用户ID
         * @param targetType 收藏类型
         * @param targetId 目标ID
         * @return 是否已收藏
         */
        Boolean isCollected(Integer userId, Integer targetType, Integer targetId);
        
        /**
         * 取消收藏
         * @param userId 用户ID
         * @param targetType 收藏类型
         * @param targetId 目标ID
         * @return 是否成功
         */
        Boolean cancelCollect(Integer userId, Integer targetType, Integer targetId);
        
        /**
         * 添加收藏
         * @param userId 用户ID
         * @param targetType 收藏类型
         * @param targetId 目标ID
         * @return 是否成功
         */
        Boolean addCollect(Integer userId, Integer targetType, Integer targetId);
}