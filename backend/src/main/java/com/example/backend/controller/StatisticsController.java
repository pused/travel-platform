package com.example.backend.controller;

import com.example.backend.common.result.BaseResponse;
import com.example.backend.common.result.Result;
import com.example.backend.entity.vo.statistics.BasicDataVO;
import com.example.backend.entity.vo.statistics.ChartDataVO;
import com.example.backend.service.IStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description 数据统计控制层
 * @Version 1.0
 */
@RestController
@RequestMapping("/statistics")
@Api(tags = "数据统计接口")
public class StatisticsController {

    @Resource
    private IStatisticsService statisticsService;

    @GetMapping("/basicData")
    @ApiOperation(value = "获取基础数据统计")
    public BaseResponse<BasicDataVO> getBasicData() {
        BasicDataVO res = statisticsService.getBasicData();
        return Result.success(res);
    }
}