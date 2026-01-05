package com.jiuliu.myblog_dev.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jiuliu.myblog_dev.dto.LoginDTO;
import com.jiuliu.myblog_dev.entity.SysUser;
import com.jiuliu.myblog_dev.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    private Map<String, Object> getSuccessResponse(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "成功");
        result.put("data", data);
        return result;
    }

    private Map<String, Object> getErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        return result;
    }
    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO dto) { // ← 改这里
        String username = dto.getUsername();
        String password = dto.getPassword();

        if (username == null || password == null) {
            return getErrorResponse("用户名或密码不能为空");
        }

        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                        .eq("username", username)
        );

        if (user == null) {
            return getErrorResponse("用户名不存在");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return getErrorResponse("密码错误");
        }

        StpUtil.login(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("updateTime", user.getUpdateTime());


        return getSuccessResponse(result);
    }
}