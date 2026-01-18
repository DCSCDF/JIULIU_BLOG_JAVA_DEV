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
    @ExceptionHandler(value = {NotLoginException.class, NotRoleException.class, NotPermissionException.class})
    public SaResult handleAuthException(Exception e) {
        // 安全记录异常 不返回堆栈给前端
        log.warn("鉴权异常: {}", e.getMessage());
        log.debug("鉴权异常详情:", e);

        // 根据异常类型返回不同的错误信息
        if (e instanceof NotLoginException) {
            SaResult result = SaResult.error("未登录，请先登录");
            result.setCode(401);
            return result;
        } else if (e instanceof NotRoleException) {
            SaResult result = SaResult.error("没有角色权限");
            result.setCode(403);
            return result;
        } else if (e instanceof NotPermissionException) {
            SaResult result = SaResult.error("没有权限");
            result.setCode(403);
            return result;
        }
        SaResult result = SaResult.error("系统错误");
        result.setCode(500);
        return result;
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler
    public SaResult handleException(Exception e) {
        // 安全记录异常
        log.error("系统异常: {}", e.getMessage());
        log.error("系统异常详情:", e);

        SaResult result = SaResult.error("系统错误: " + e.getMessage());
        result.setCode(500);
        return result;
    }
}