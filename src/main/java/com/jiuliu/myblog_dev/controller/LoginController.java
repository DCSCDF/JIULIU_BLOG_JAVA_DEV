package com.jiuliu.myblog_dev.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.jiuliu.myblog_dev.config.RsaKeyConfig;
import com.jiuliu.myblog_dev.config.rateLimit.RateLimit;
import com.jiuliu.myblog_dev.dto.LoginDTO;
import com.jiuliu.myblog_dev.entity.SysUser;
import com.jiuliu.myblog_dev.mapper.SysUserMapper;
import com.jiuliu.myblog_dev.config.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
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
    @RateLimit(count = 10, period = 800) // （按IP+方法）
    public Map<String, Object> getPublicKey() {
        Map<String, Object> data = new HashMap<>();
        data.put("publicKey", rsaKeyConfig.getPublicKeyBase64());
        return getSuccessResponse(data);
    }


    @PostMapping("/login")
    @RateLimit(count = 10, period = 800) // （按IP+方法）
    public Map<String, Object> login(@RequestBody LoginDTO dto) {
        String username = dto.getUsername();
        String encryptedPassword = dto.getPassword(); // 注意：现在是加密后的 Base64 字符串

        if (username == null || encryptedPassword == null) {
            return getErrorResponse("用户名或密码不能为空");
        }

        // 使用私钥解密密码
        String rawPassword;
        try {
            rawPassword = RsaUtils.decryptByPrivateKey(encryptedPassword, rsaKeyConfig.getPrivateKeyBase64());
        } catch (Exception e) {
            return getErrorResponse("密码解密失败，请刷新页面重试");
        }

        // 查询用户
        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                        .eq("username", username)
        );

        if (user == null) {
            return getErrorResponse("用户名不存在");
        }

        // 校验密码（使用解密后的明文）
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return getErrorResponse("密码错误");
        }

        // 登录
        StpUtil.login(user.getId());

        // 返回安全信息（不含密码）
        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("username", user.getUsername());
        result.put("updateTime", user.getUpdateTime());

        return getSuccessResponse(result);
    }

    //是否登陆
    @GetMapping("/is-logged-in")
    public Map<String, Object> isLoggedIn() {
        if (StpUtil.isLogin()) {
            return getSuccessResponse(Map.of("loggedIn", true));
        } else {
            return getSuccessResponse(Map.of("loggedIn", false));
        }
    }


    @GetMapping("/profile")
    public Map<String, Object> getUserProfile() {
        // 直接获取，如果未登录，Sa-Token 会自动抛出 NotLoginException
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

}