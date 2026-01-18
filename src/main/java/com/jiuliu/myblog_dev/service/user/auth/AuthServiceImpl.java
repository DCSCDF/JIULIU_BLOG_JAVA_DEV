package com.jiuliu.myblog_dev.service.user.auth;


import cn.dev33.satoken.stp.StpUtil;
import com.jiuliu.myblog_dev.config.RsaKeyConfig;
import com.jiuliu.myblog_dev.dto.user.auth.ChangePasswordDTO;
import com.jiuliu.myblog_dev.dto.user.auth.LoginDTO;
import com.jiuliu.myblog_dev.entity.user.SysUser;
import com.jiuliu.myblog_dev.mapper.user.SysUserMapper;
import com.jiuliu.myblog_dev.utils.RsaUtils;
import com.jiuliu.myblog_dev.utils.Validation.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 成功响应
    private Map<String, Object> getSuccessResponse(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "成功");
        result.put("data", data);
        return result;
    }

    // 错误响应
    private Map<String, Object> getErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        return result;
    }

    @Override
    public Map<String, Object> getPublicKey() {
        Map<String, Object> data = new HashMap<>();
        data.put("publicKey", rsaKeyConfig.getPublicKeyBase64());
        return getSuccessResponse(data);
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        String username = dto.getUsername();
        String encryptedPassword = dto.getPassword();

        if (!StringUtils.hasText(username)) {
            return getErrorResponse("用户名不能为空");
        }
        if (!ValidationHelper.validateUsername(username)) {
            return getErrorResponse("用户名格式错误");
        }
        if (!StringUtils.hasText(encryptedPassword)) {
            return getErrorResponse("密码不能为空");
        }

        String rawPassword;
        try {
            rawPassword = RsaUtils.decryptByPrivateKey(encryptedPassword, rsaKeyConfig.getPrivateKeyBase64());
        } catch (Exception e) {
            return getErrorResponse("密码格式错误");
        }

        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                        .eq("username", username.trim())
        );

        if (user == null) {
            return getErrorResponse("用户名不存在");
        }

        if (!ValidationHelper.validatePassword(rawPassword)) {
            return getErrorResponse("密码格式错误");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return getErrorResponse("密码错误");
        }

        StpUtil.login(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("updateTime", user.getUpdateTime());

        return getSuccessResponse(result);
    }

    @Override
    public boolean isLoggedIn() {
        return StpUtil.isLogin();
    }

    @Override
    public Map<String, Object> getUserProfile(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return getErrorResponse("用户不存在");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("nickname", user.getNickname());
        profile.put("email", user.getEmail());
        profile.put("createTime", user.getCreateTime());
        profile.put("updateTime", user.getUpdateTime());
        profile.put("avatarUrl", user.getAvatarUrl());

        return getSuccessResponse(profile);
    }

    @Override
    public Map<String, Object> logout() {
        if (!StpUtil.isLogin()) {
            return getErrorResponse("用户未登录");
        }

        try {
            StpUtil.logout();
            Map<String, Object> data = new HashMap<>();
            data.put("message", "登出成功");
            data.put("logoutTime", System.currentTimeMillis());
            return getSuccessResponse(data);
        } catch (Exception e) {
            return getErrorResponse("登出失败");
        }
    }

    @Override
    public Map<String, Object> updatePassword(ChangePasswordDTO dto, Long currentUserId) {
        String encryptedOldPassword = dto.getOld_password();
        String encryptedNewPassword = dto.getNew_password();

        if (!StringUtils.hasText(encryptedOldPassword) || !StringUtils.hasText(encryptedNewPassword)) {
            return getErrorResponse("原密码或新密码不能为空");
        }

        String rawOldPassword, rawNewPassword;
        try {
            rawOldPassword = RsaUtils.decryptByPrivateKey(encryptedOldPassword, rsaKeyConfig.getPrivateKeyBase64());
            rawNewPassword = RsaUtils.decryptByPrivateKey(encryptedNewPassword, rsaKeyConfig.getPrivateKeyBase64());
            if (!StringUtils.hasText(rawOldPassword) || !StringUtils.hasText(rawNewPassword)) {
                return getErrorResponse("密码格式错误");
            }
        } catch (Exception e) {
            return getErrorResponse("密码格式错误");
        }

        if (!ValidationHelper.validatePassword(rawNewPassword)) {
            return getErrorResponse("新密码格式不符合要求");
        }

        if (rawOldPassword.equals(rawNewPassword)) {
            return getErrorResponse("新密码不能与原密码相同");
        }

        SysUser user = sysUserMapper.selectById(currentUserId);
        if (user == null) {
            return getErrorResponse("用户不存在");
        }

        if (!passwordEncoder.matches(rawOldPassword, user.getPassword())) {
            return getErrorResponse("原密码错误");
        }

        String encodedNewPassword = passwordEncoder.encode(rawNewPassword);
        user.setPassword(encodedNewPassword);
        long timestamp = System.currentTimeMillis();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
        user.setUpdateTime(localDateTime);

        int rows = sysUserMapper.updateById(user);
        if (rows != 1) {
            return getErrorResponse("密码修改失败，请重试");
        }

        StpUtil.logout(currentUserId);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "密码修改成功，请重新登录");
        return getSuccessResponse(data);
    }
}