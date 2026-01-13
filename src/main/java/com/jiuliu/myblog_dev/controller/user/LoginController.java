package com.jiuliu.myblog_dev.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import com.jiuliu.myblog_dev.config.user.RsaKeyConfig;
import com.jiuliu.myblog_dev.config.rateLimit.RateLimit;
import com.jiuliu.myblog_dev.dto.user.LoginDTO;
import com.jiuliu.myblog_dev.entity.user.SysUser;
import com.jiuliu.myblog_dev.mapper.user.SysUserMapper;
import com.jiuliu.myblog_dev.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

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

    // 获取当前公钥
    @GetMapping("/public-key")
    //    @RateLimit(count = 10, period = 800)
    public Map<String, Object> getPublicKey() {
        Map<String, Object> data = new HashMap<>();
        data.put("publicKey", rsaKeyConfig.getPublicKeyBase64());
        return getSuccessResponse(data);
    }

    @PostMapping("/login")
    @RateLimit(count = 10, period = 800)
    public Map<String, Object> login(@RequestBody LoginDTO dto) {
        String username = dto.getUsername();
        String encryptedPassword = dto.getPassword();

        if (!StringUtils.hasText(username)) {
            return getErrorResponse("用户名不能为空");
        }
        if (!StringUtils.hasText(encryptedPassword)) {
            return getErrorResponse("密码不能为空");
        }

        String rawPassword;
        try {
            // 尝试用私钥解密前端传来的加密密码
            rawPassword = RsaUtils.decryptByPrivateKey(encryptedPassword, rsaKeyConfig.getPrivateKeyBase64());

            if (!StringUtils.hasText(rawPassword)) {
                return getErrorResponse("密码格式错误");
            }
        } catch (Exception e) {
            // 任何解密异常（Base64非法、密文损坏、非本系统公钥加密等）都视为格式错误
            return getErrorResponse("密码格式错误");
        }

        // 查询用户
        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                        .eq("username", username.trim())
        );

        if (user == null) {
            return getErrorResponse("用户名不存在");
        }

        // 校验密码（使用解密后的明文）
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return getErrorResponse("密码错误");
        }

        // 登录成功，生成 Token
        StpUtil.login(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("username", user.getUsername());
        result.put("updateTime", user.getUpdateTime());

        return getSuccessResponse(result);
    }

    // 是否已登录
    @GetMapping("/is-logged-in")
    public Map<String, Object> isLoggedIn() {
        boolean loggedIn = StpUtil.isLogin();
        return getSuccessResponse(Map.of("loggedIn", loggedIn));
    }

    // 获取用户资料
    @GetMapping("/profile")
    public Map<String, Object> getUserProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return getErrorResponse("用户不存在");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("createTime", user.getCreateTime());
        profile.put("updateTime", user.getUpdateTime());
        profile.put("avatarUrl", user.getAvatarUrl());

        return getSuccessResponse(profile);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        //  检查登录状态
        if (!StpUtil.isLogin()) {
            return getErrorResponse("用户未登录");
        }

        try {
            // 执行登出
            StpUtil.logout();

            // 创建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("message", "登出成功");
            data.put("logoutTime", System.currentTimeMillis());

            // 返回成功响应
            return getSuccessResponse(data);
        } catch (Exception e) {
            // 异常处理
            return getErrorResponse("登出失败");
        }
    }
}