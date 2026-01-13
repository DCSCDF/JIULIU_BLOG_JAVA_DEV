package com.jiuliu.myblog_dev.entity.user.role;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission_group")//角色-权限组关联表
public class SysRolePermissionGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("role_id")
    private Long roleId;

    @TableField("group_id")
    private Long groupId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}