package com.jiuliu.myblog_dev.entity.user.role;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role") //角色
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;          // 角色编码（唯一）
    private String name;          // 角色名称
    private String description;   // 描述

    @TableField("is_super_admin")
    private Boolean superAdmin;   // 是否超级管理员（对应 TINYINT(1)）

    @TableField("is_system")
    private Boolean isSystem;      // 是否系统内置角色

    @TableField("sort_order")
    private Integer sortOrder;

    private Integer status;       // 0=禁用，1=启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}