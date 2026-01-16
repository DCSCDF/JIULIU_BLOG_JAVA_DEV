package com.jiuliu.myblog_dev.entity.blog.bloglike;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_blog_like")//文章点赞表
public class SysBlogLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("blog_id")
    private Long blogId;

    @TableField("user_id")
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}