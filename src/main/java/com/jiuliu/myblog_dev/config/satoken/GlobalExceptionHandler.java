package com.jiuliu.myblog_dev.config.satoken;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理鉴权异常
     */
    @SuppressWarnings("unused")
    @ExceptionHandler(value = {NotLoginException.class, NotRoleException.class, NotPermissionException.class})
    public SaResult handleAuthException(Exception e) {

        log.warn("鉴权异常: {}", e.getClass().getSimpleName());
        log.info("鉴权异常详情: {}", e.getMessage()); // 不记录堆栈，避免日志过大

        //  返回标准化错误信息（不包含异常详情）
        if (e instanceof NotLoginException) {
            return SaResult.error("未登录，请先登录").setCode(401);
        } else if (e instanceof NotRoleException) {
            return SaResult.error("没有角色权限").setCode(403);
        } else if (e instanceof NotPermissionException) {
            return SaResult.error("没有权限").setCode(403);
        }
        return SaResult.error("系统错误").setCode(500);
    }

    /**
     * 处理其他系统异常
     */
    @SuppressWarnings("unused")
    @ExceptionHandler
    public SaResult handleException(Exception e) {
        // 1. 严格安全记录：只记录异常类型和关键信息
        log.error("系统异常: {}", e.getClass().getSimpleName());
        log.error("系统异常详情: {}", e.getMessage());

        // 2. 返回统一错误信息（避免暴露实现细节）
        return SaResult.error("系统错误").setCode(500);
    }
}