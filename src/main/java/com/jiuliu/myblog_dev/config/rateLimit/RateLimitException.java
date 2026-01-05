package com.jiuliu.myblog_dev.config.rateLimit;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}