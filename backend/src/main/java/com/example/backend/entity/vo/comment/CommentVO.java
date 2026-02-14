package com.example.backend.entity.vo.comment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.backend.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author caiya
 * @Description 评论VO类
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentVO extends Comment {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userAvatar;
    
    @TableField(exist = false)
    private String strategyName;
}