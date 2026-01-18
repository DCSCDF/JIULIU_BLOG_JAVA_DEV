package com.jiuliu.myblog_dev.mapper.user.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiuliu.myblog_dev.entity.user.permission.SysPermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}