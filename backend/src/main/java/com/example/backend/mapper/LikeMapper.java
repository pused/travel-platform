package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Like;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 用户点赞表数据访问层
 * @Version 1.0
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
}