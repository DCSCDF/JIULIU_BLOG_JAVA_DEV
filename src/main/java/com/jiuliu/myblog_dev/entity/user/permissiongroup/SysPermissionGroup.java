package com.jiuliu.myblog_dev.entity.user.permissiongroup;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission_group") //权限组
public class SysPermissionGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;            // 权限组名称
    private String description;     // 描述

    @TableField("sort_order")
    private Integer sortOrder;
    private Integer status;         // 0=禁用，1=启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}