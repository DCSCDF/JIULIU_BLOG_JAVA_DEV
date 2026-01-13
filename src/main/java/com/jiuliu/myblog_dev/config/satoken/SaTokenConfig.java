package com.jiuliu.myblog_dev.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验登录状态
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")// 拦截所有请求 只会放行公共接口
                .excludePathPatterns(
                        "/user/login",// 放行登录
                        "/user/public-key" // 放行公钥接口

                );
    }
}