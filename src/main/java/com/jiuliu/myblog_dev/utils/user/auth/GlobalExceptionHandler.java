package com.jiuliu.myblog_dev.utils.user.auth;

import cn.dev33.satoken.exception.NotLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotLoginException.class)
    @SuppressWarnings("unused")
    public Map<String, Object> handleNotLoginException(NotLoginException e) {

        log.warn("用户未登录: {}", e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", "请先登录");
        result.put("data", null);
        return result;
    }
}