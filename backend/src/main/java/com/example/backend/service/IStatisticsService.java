package com.example.backend.service;

import com.example.backend.entity.vo.statistics.BasicDataVO;
import com.example.backend.entity.vo.statistics.ChartDataVO;

/**
 * @Description 数据统计服务接口
 * @Version 1.0
 */
public interface IStatisticsService {

    /**
     * 获取基础数据统计
     * @return 基础数据统计
     */
    BasicDataVO getBasicData();
}