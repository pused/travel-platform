package com.example.backend.entity.vo.strategy;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.backend.entity.Strategy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author 
 * @Description 旅游攻略VO类
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StrategyVO extends Strategy {
    
    @TableField(exist = false)
    private String authorName;
    
    @TableField(exist = false)
    private String attractionName;
}