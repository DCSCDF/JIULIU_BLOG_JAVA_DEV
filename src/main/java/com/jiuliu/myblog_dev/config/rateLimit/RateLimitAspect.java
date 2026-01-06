package com.jiuliu.myblog_dev.config.rateLimit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class RateLimitAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitAspect.class);
    private final ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> expireTimeMap = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    @SuppressWarnings("unused")
    public Object doRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("RateLimit 注解只能用于 Web 请求方法");
        }
        HttpServletRequest request = attributes.getRequest();

        String ip = getClientIpAddress(request);
        String limitKey = buildLimitKey(joinPoint, rateLimit, ip);
        long now = System.currentTimeMillis();
        long periodMs = rateLimit.period() * 1000L;

        // 记录限流键构建信息（生产环境默认不开启 DEBUG）
        log.debug("构建限流键: [key={}, ip={}, method={}]", limitKey, ip, getMethodSignature(joinPoint));

        // 惰性检查并重置过期计数器
        AtomicInteger count = getOrCreateCounter(limitKey, now, periodMs);

        // 检查是否超过阈值
        if (count.incrementAndGet() > rateLimit.count()) {
            // 限流触发
            log.warn("请求被限流: [ip={}, key={}, method={}]", ip, limitKey, getMethodSignature(joinPoint));
            throw new RateLimitException("请求过于频繁，请稍后再试");
        }

        return joinPoint.proceed();
    }

    private String buildLimitKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit, String ip) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        return String.format("%s:%s:%s.%s",
                rateLimit.prefix(), ip, className, methodName);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 仅记录第一个 IP
            ip = ip.split(",")[0].trim();
            log.debug("通过 X-Forwarded-For 获取IP: [ip={}]", ip);
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            log.debug("通过 X-Real-IP 获取IP: [ip={}]", ip);
            return ip;
        }

        ip = request.getRemoteAddr();
        log.debug("获取IP: [ip={}]", ip);
        return ip;
    }

    private String getMethodSignature(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }

    private AtomicInteger getOrCreateCounter(String key, long now, long periodMs) {
        Long expire = expireTimeMap.get(key);
        if (expire != null && now > expire) {

            log.debug("重置限流计数器: [key={}]", key);
            AtomicInteger newCounter = new AtomicInteger(0);
            counterMap.put(key, newCounter);
            expireTimeMap.put(key, now + periodMs);
            return newCounter;
        }

        AtomicInteger counter = counterMap.computeIfAbsent(key, k -> new AtomicInteger(0));
        expireTimeMap.putIfAbsent(key, now + periodMs);
        return counter;
    }
}