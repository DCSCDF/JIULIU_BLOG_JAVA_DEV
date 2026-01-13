package com.jiuliu.myblog_dev.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiuliu.myblog_dev.entity.user.SysUser;
import com.jiuliu.myblog_dev.mapper.user.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Order(2)  // 设置较低的优先级，确保在数据库初始化之后运行
public class DefaultAdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminInitializer.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void run(String... args) {
        // 延迟执行，等待数据库初始化完成
        try {
            Thread.sleep(5000);  // 等待5秒，确保数据库初始化完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 检查数据库表是否存在
        if (!isDatabaseInitialized()) {
            log.error("数据库表未初始化，跳过管理员创建");
            return;
        }

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

    /**
     * 检查数据库是否已初始化
     */
    private boolean isDatabaseInitialized() {
        try {
            // 尝试查询sys_user表，如果表不存在会抛出异常
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.last("LIMIT 1");
            sysUserMapper.selectOne(queryWrapper);
            return true;
        } catch (Exception e) {
            log.warn("数据库表尚未初始化: {}", e.getMessage());
            return false;
        }
    }
}