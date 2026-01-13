package com.jiuliu.myblog_dev.entity.blog.tag;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_tag")//标签表
public class SysTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;            // 标签名（唯一）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}