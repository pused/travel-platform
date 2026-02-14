package com.example.backend.entity.vo.attraction;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.backend.entity.Attraction;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author 
 * @Description 景点VO类
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AttractionVO extends Attraction {
    @TableField(exist = false)
    private String categoryName;
}