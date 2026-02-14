package com.example.backend.entity.vo.like;

import com.example.backend.entity.Like;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 用户点赞表视图对象
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LikeVO extends Like {

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 目标名称（景点名称或攻略标题）
     */
    private String targetName;

    /**
     * 点赞类型名称
     */
    private String targetTypeName;
}