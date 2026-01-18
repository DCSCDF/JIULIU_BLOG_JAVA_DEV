package com.jiuliu.myblog_dev;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//@MapperScan
@SpringBootApplication
@EnableAspectJAutoProxy
public class MyblogDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyblogDevApplication.class, args);
    }

}

