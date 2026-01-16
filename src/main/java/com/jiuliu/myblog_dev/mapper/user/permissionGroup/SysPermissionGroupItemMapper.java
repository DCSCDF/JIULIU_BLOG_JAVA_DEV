package com.jiuliu.myblog_dev.mapper.user.permissionGroup;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiuliu.myblog_dev.entity.user.role.SysRole;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface SysPermissionGroupItemMapper extends BaseMapper<SysRole> {
}