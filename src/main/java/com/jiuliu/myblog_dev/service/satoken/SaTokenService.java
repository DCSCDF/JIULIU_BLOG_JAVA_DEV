package com.jiuliu.myblog_dev.service.satoken;


import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiuliu.myblog_dev.entity.user.permission.SysPermission;
import com.jiuliu.myblog_dev.entity.user.role.SysRole;
import com.jiuliu.myblog_dev.mapper.user.permission.SysPermissionMapper;
import com.jiuliu.myblog_dev.mapper.user.role.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证接口实现
 */
@Service
public class SaTokenService implements StpInterface {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    /**
     * 返回当前账号拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 根据 loginId 获取用户ID
        Long userId = (Long) loginId;

        // 2. 查询用户拥有的角色
        List<SysRole> roleList = sysRoleMapper.selectRolesByUserId(userId);

        // 3. 查询角色拥有的权限
        List<String> permissionList = roleList.stream()
                .flatMap(role -> {
                    List<SysPermission> permissions = sysPermissionMapper.selectPermissionsByRoleId(role.getId());
                    return permissions.stream().map(SysPermission::getCode);
                })
                .collect(Collectors.toList());

        return permissionList;
    }

    /**
     * 返回当前账号拥有的角色集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 1. 根据 loginId 获取用户ID
        Long userId = (Long) loginId;

        // 2. 查询用户拥有的角色
        List<SysRole> roleList = sysRoleMapper.selectRolesByUserId(userId);

        return roleList.stream().map(SysRole::getCode).collect(Collectors.toList());
    }
}