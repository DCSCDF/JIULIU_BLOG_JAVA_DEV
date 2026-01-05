package com.jiuliu.myblog_dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
public class MyblogDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyblogDevApplication.class, args);
    }

}

