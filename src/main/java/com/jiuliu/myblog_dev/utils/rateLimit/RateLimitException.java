package com.jiuliu.myblog_dev.utils.rateLimit;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}