package com.jiuliu.myblog_dev.service.auth;


import com.jiuliu.myblog_dev.dto.user.ChangePasswordDTO;
import com.jiuliu.myblog_dev.dto.user.LoginDTO;
import com.jiuliu.myblog_dev.entity.user.SysUser;

import java.util.Map;

public interface AuthService {

    Map<String, Object> getPublicKey();

    Map<String, Object> login(LoginDTO dto);

    boolean isLoggedIn();

    Map<String, Object> getUserProfile(Long userId);

    Map<String, Object> logout();

    Map<String, Object> updatePassword(ChangePasswordDTO dto, Long currentUserId);

    // TODO: 后续补充 register 方法
}