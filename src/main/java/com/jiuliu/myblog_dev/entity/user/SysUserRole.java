package com.jiuliu.myblog_dev.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_role")
public class SysUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}