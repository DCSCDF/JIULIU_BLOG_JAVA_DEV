package com.jiuliu.myblog_dev.service.user.auth;


import com.jiuliu.myblog_dev.dto.user.auth.ChangePasswordDTO;
import com.jiuliu.myblog_dev.dto.user.auth.LoginDTO;

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