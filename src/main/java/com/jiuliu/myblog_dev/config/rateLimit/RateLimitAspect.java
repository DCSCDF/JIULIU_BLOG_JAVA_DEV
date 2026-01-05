package com.jiuliu.myblog_dev.config.rateLimit;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class RateLimitAspect {

    // key -> 计数器
    private final ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();
    // key -> 过期时间（毫秒）
    private final ConcurrentHashMap<String, Long> expireTimeMap = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object doRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        String ip = request != null ? request.getRemoteAddr() : "unknown";

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String limitKey = String.format("%s:%s:%s.%s",
                rateLimit.prefix(), ip, className, methodName);

        long now = System.currentTimeMillis();
        long periodMs = rateLimit.period() * 1000L; // 转为毫秒

        // 清理过期 key（每次访问时检查）
        counterMap.entrySet().removeIf(entry -> {
            Long expire = expireTimeMap.get(entry.getKey());
            return expire != null && now > expire;
        });
        expireTimeMap.entrySet().removeIf(entry -> now > entry.getValue());

        // 获取或创建计数器
        AtomicInteger count = counterMap.computeIfAbsent(limitKey, k -> new AtomicInteger(0));
        expireTimeMap.putIfAbsent(limitKey, now + periodMs);

        // 判断是否超限
        if (count.incrementAndGet() > rateLimit.count()) {
            throw new RateLimitException("请求过于频繁，请稍后再试");
        }

        return joinPoint.proceed();
    }
}