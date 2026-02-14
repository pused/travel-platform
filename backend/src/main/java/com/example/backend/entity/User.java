package com.example.backend.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ExcelProperty(value = "人员ID", index = 0)
    private Integer id;

    /**
     * 账号
     */
    @ExcelProperty(value = "账号", index = 1)
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称", index = 2)
    private String nickName;

    /**
     * 年龄
     */
    @ExcelProperty(value = "年龄", index = 3)
    private Integer age;

    /**
     * 性别（0-男，1-女，2-保密）
     */
    @ExcelProperty(value = "性别", index = 4)
    private Integer gender;

    /**
     * 头像
     */
    @ExcelProperty(value = "头像", index = 5)
    private String avatar;

    /**
     * 个人介绍
     */
    @ExcelProperty(value = "个人介绍", index = 6)
    private String profile;

    /**
     * 账号状态（0-封禁，1-正常）
     */
    @ExcelProperty(value = "账号状态", index = 7)
    private Integer status;

    /**
     * 角色
     */
    @ExcelProperty(value = "角色", index = 8)
    private String role;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "创建时间", index = 9)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "更新时间", index = 10)
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}