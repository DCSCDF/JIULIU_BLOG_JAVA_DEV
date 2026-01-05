package com.jiuliu.myblog_dev.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiuliu.myblog_dev.entity.SysUser;
import com.jiuliu.myblog_dev.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已经存在管理员
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "admin");
        SysUser adminUser = sysUserMapper.selectOne(queryWrapper);

        if (adminUser == null) {
            // 创建管理员
            SysUser sysUser = new SysUser();
            sysUser.setUsername("admin");
            // 使用 BCrypt 加密密码
            sysUser.setPassword(new BCryptPasswordEncoder().encode("123456"));
            sysUser.setStatus(1); // 1表示启用
            sysUser.setCreateTime(LocalDateTime.now());
            sysUser.setUpdateTime(LocalDateTime.now());

            sysUserMapper.insert(sysUser);
            System.out.println("默认管理员已创建：admin/123456");
        }
    }
}