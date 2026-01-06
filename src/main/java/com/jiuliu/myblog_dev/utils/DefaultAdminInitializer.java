package com.jiuliu.myblog_dev.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiuliu.myblog_dev.entity.SysUser;
import com.jiuliu.myblog_dev.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminInitializer.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void run(String... args) {
        // 检查是否已经存在管理员
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "admin");
        SysUser adminUser = sysUserMapper.selectOne(queryWrapper);

        if (adminUser == null) {
            // 创建管理员
            SysUser sysUser = new SysUser();
            sysUser.setUsername("admin");
            sysUser.setPassword(passwordEncoder.encode("123456"));
            sysUser.setStatus(1); // 1表示启用
            sysUser.setCreateTime(LocalDateTime.now());
            sysUser.setUpdateTime(LocalDateTime.now());

            sysUserMapper.insert(sysUser);


            log.info("默认管理员已创建: username: admin (密码: 123456, 请首次登录后修改)");
        } else {

            log.info("管理员已存在，跳过初始化");
        }
    }
}