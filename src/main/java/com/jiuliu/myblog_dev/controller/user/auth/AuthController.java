package com.jiuliu.myblog_dev.controller.user.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.jiuliu.myblog_dev.utils.rateLimit.RateLimit;
import com.jiuliu.myblog_dev.dto.user.auth.ChangePasswordDTO;
import com.jiuliu.myblog_dev.dto.user.auth.LoginDTO;
import com.jiuliu.myblog_dev.service.user.auth.AuthService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/public-key")
    public Map<String, Object> getPublicKey() {
        return authService.getPublicKey();
    }

    @PostMapping("/login")
    @RateLimit(count = 6, period = 15)
    public Map<String, Object> login(@RequestBody LoginDTO dto) {
        return authService.login(dto);
    }

    @GetMapping("/is-logged-in")
    @RateLimit(count = 40, period = 15)
    public Map<String, Object> isLoggedIn() {
        boolean loggedIn = authService.isLoggedIn();
        return Map.of("code", 200, "message", "成功", "data", Map.of("loggedIn", loggedIn));
    }

    @GetMapping("/profile")
    @RateLimit(count = 40, period = 15)
    public Map<String, Object> getUserProfile() {
        Long userId = StpUtil.getLoginIdAsLong(); // 这里仍需 Sa-Token 工具获取 ID
        return authService.getUserProfile(userId);
    }

    @PostMapping("/logout")
    @RateLimit(count = 10, period = 15)
    public Map<String, Object> logout() {
        return authService.logout();
    }

    @PostMapping("/update-password")
    @RateLimit(count = 1, period = 15)
    public Map<String, Object> updatePassword(@Nonnull @RequestBody ChangePasswordDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return authService.updatePassword(dto, currentUserId);
    }

    @PostMapping("/register")
    public Map<String, Object> register() {
        // TODO: 补充register逻辑 等等....
        return Map.of("code", 501, "message", "未实现", "data", "null");
    }
}