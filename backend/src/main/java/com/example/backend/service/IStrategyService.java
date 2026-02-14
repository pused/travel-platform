package com.example.backend.service;

import com.example.backend.entity.Strategy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.model.PageResult;
import com.example.backend.entity.vo.strategy.StrategyVO;

import java.util.List;

/**
 * <p>
 * 旅游攻略表 服务类
 * </p>
 *
 * @author 
 * 
 */
public interface IStrategyService extends IService<Strategy> {
        /**
        * 新增
        * @param request
        * @return
        */
        Integer add(Strategy request);

        /**
         * 批量新增
         *
         * @param request
         * @return
         */
        Boolean batchAdd(List<Strategy> request);

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
         * @return
         */
        Boolean edit(Strategy request);

        /**
         * 分页条件查询
         * @return
         */
        PageResult<List<StrategyVO>> queryPage(Integer pageNum, Integer pageSize, String title, String budgetType, Integer userId);

        /**
         * 查询全部
         * @return
         */
        List<Strategy> getAll();

        /**
         * 根据id查询
         *
         * @param id
         * @return
         */
        Strategy getByIdDetail(Integer id);
}