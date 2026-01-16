package com.jiuliu.myblog_dev.mapper.blog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiuliu.myblog_dev.entity.user.role.SysRole;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface SysBlogMapper extends BaseMapper<SysRole> {
}