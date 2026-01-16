# 数据库设计文档

## 📁 数据库概述

| 项目       | 说明                     |
|------------|--------------------------|
| **项目**   | 博客系统                 |
| **数据库名** | `myblog_sql`             |
| **字符集** | `utf8mb4`                |
| **排序规则** | `utf8mb4_unicode_ci`     |
| **引擎**   | `InnoDB`                 |
| **用途**   | 博客系统数据库           |

---

## 📊 数据库表结构

### 1. 用户表 (`sys_user`)
**用途**：存储系统用户信息

| 字段名        | 类型         | 约束                          | 默认值                        | 说明                     |
|---------------|--------------|-------------------------------|-------------------------------|--------------------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 用户ID                   |
| username      | VARCHAR(50)  | UNIQUE, NOT NULL              | -                             | 用户名                   |
| password      | VARCHAR(100) | NOT NULL                      | -                             | 密码（BCrypt加密）       |
| email         | VARCHAR(50)  | UNIQUE                        | NULL                          | 邮箱                     |
| avatar_url    | VARCHAR(200) | -                             | NULL                          | 头像URL                  |
| status        | INT          | -                             | 1                             | 状态：0=禁用，1=启用     |
| create_time   | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间                 |
| update_time   | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间                 |

**索引**：
- `idx_user_status(status)` - 状态查询优化

---

### 2. 角色表 (`sys_role`)
**用途**：存储系统角色信息

| 字段名           | 类型         | 约束                          | 默认值                        | 说明                     |
|------------------|--------------|-------------------------------|-------------------------------|--------------------------|
| id               | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 角色ID                   |
| code             | VARCHAR(50)  | UNIQUE, NOT NULL              | -                             | 角色编码                 |
| name             | VARCHAR(50)  | NOT NULL                      | -                             | 角色名称                 |
| description      | VARCHAR(200) | -                             | NULL                          | 角色描述                 |
| is_super_admin   | TINYINT(1)   | -                             | 0                             | 是否超级管理员           |
| is_system        | TINYINT(1)   | -                             | 0                             | 是否系统内置             |
| sort_order       | INT          | -                             | 0                             | 排序顺序                 |
| status           | INT          | -                             | 1                             | 状态：0=禁用，1=启用     |
| create_time      | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间                 |
| update_time      | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间                 |

**索引**：
- `idx_role_code(code)` - 编码查询优化
- `idx_role_status(status)` - 状态查询优化
- `idx_role_super_admin(is_super_admin)` - 超级管理员查询

**默认角色**：
- `SUPER_ADMIN` - 超级管理员
- `ADMIN` - 普通管理员
- `AUTHOR` - 文章作者
- `USER` - 普通用户

---

### 3. 权限表 (`sys_permission`)
**用途**：存储系统权限信息，支持菜单、按钮、接口等类型

| 字段名            | 类型         | 约束                          | 默认值                        | 说明                     |
|-------------------|--------------|-------------------------------|-------------------------------|--------------------------|
| id                | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 权限ID                   |
| parent_id         | BIGINT       | -                             | 0                             | 父权限ID                 |
| code              | VARCHAR(100) | UNIQUE, NOT NULL              | -                             | 权限编码                 |
| name              | VARCHAR(50)  | NOT NULL                      | -                             | 权限名称                 |
| type              | VARCHAR(20)  | NOT NULL                      | -                             | 类型：MENU/BUTTON/API/FIELD |
| description       | VARCHAR(200) | -                             | NULL                          | 权限描述                 |
| icon              | VARCHAR(50)  | -                             | NULL                          | 图标                     |
| path              | VARCHAR(200) | -                             | NULL                          | 路径/URL                 |
| component         | VARCHAR(200) | -                             | NULL                          | 前端组件                 |
| is_hidden         | TINYINT(1)   | -                             | 0                             | 是否隐藏                 |
| is_affix          | TINYINT(1)   | -                             | 0                             | 是否固定页签             |
| is_keep_alive     | TINYINT(1)   | -                             | 0                             | 是否缓存                 |
| sort_order        | INT          | -                             | 0                             | 排序顺序                 |
| status            | INT          | -                             | 1                             | 状态：0=禁用，1=启用     |
| create_time       | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间                 |
| update_time       | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间                 |

**索引**：
- `idx_permission_code(code)`
- `idx_permission_parent(parent_id)`
- `idx_permission_type(type)`
- `idx_permission_status(status)`

**默认权限**：
- 系统管理权限 (`system.*`)
- 用户管理权限 (`system:user:*`)
- 角色管理权限 (`system:role:*`)
- 文章管理权限 (`article:*`)
- 分类管理权限 (`category:*`)
- 评论管理权限 (`comment:*`)

---

### 4. 用户-角色关联表 (`sys_user_role`)
**用途**：存储用户与角色的多对多关系

| 字段名       | 类型       | 约束                                      | 默认值                | 说明     |
|--------------|------------|-------------------------------------------|-----------------------|----------|
| id           | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 关联ID   |
| user_id      | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 用户ID   |
| role_id      | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 角色ID   |
| create_time  | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间 |

**约束**：
- `UNIQUE KEY uk_user_role(user_id, role_id)`
- `FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE`
- `FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE`

**索引**：
- `idx_user_role_user(user_id)`
- `idx_user_role_role(role_id)`

---

### 5. 角色-权限关联表 (`sys_role_permission`)
**用途**：存储角色与权限的多对多关系

| 字段名         | 类型       | 约束                                      | 默认值                | 说明     |
|----------------|------------|-------------------------------------------|-----------------------|----------|
| id             | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 关联ID   |
| role_id        | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 角色ID   |
| permission_id  | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 权限ID   |
| create_time    | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间 |

**约束**：
- `UNIQUE KEY uk_role_permission(role_id, permission_id)`
- `FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE`
- `FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE`

**索引**：
- `idx_role_permission_role(role_id)`
- `idx_role_permission_permission(permission_id)`

---

### 6. 权限组表 (`sys_permission_group`)
**用途**：用于组织和管理权限组

| 字段名        | 类型         | 约束                          | 默认值                        | 说明             |
|---------------|--------------|-------------------------------|-------------------------------|------------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 权限组ID         |
| name          | VARCHAR(50)  | NOT NULL                      | -                             | 权限组名称       |
| description   | VARCHAR(200) | -                             | NULL                          | 权限组描述       |
| sort_order    | INT          | -                             | 0                             | 排序顺序         |
| status        | INT          | -                             | 1                             | 状态             |
| create_time   | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间         |
| update_time   | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间         |

**默认权限组**：
- 系统管理组
- 文章管理组
- 用户管理组

---

### 7. 权限-权限组关联表 (`sys_permission_group_item`)
**用途**：关联权限和权限组

| 字段名        | 类型       | 约束                                      | 默认值                | 说明         |
|---------------|------------|-------------------------------------------|-----------------------|--------------|
| id            | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 关联ID       |
| group_id      | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 权限组ID     |
| permission_id | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 权限ID       |
| sort_order    | INT        | -                                         | 0                     | 排序顺序     |
| create_time   | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间     |

**约束**：
- `UNIQUE KEY uk_group_permission(group_id, permission_id)`
- `FOREIGN KEY (group_id) REFERENCES sys_permission_group(id) ON DELETE CASCADE`
- `FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE`

---

### 8. 角色-权限组关联表 (`sys_role_permission_group`)
**用途**：批量分配权限（通过权限组）

| 字段名       | 类型       | 约束                                      | 默认值                | 说明     |
|--------------|------------|-------------------------------------------|-----------------------|----------|
| id           | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 关联ID   |
| role_id      | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 角色ID   |
| group_id     | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 权限组ID |
| create_time  | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间 |

**约束**：
- `UNIQUE KEY uk_role_group(role_id, group_id)`
- `FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE`
- `FOREIGN KEY (group_id) REFERENCES sys_permission_group(id) ON DELETE CASCADE`

---

### 9. 分类表 (`sys_category`)
**用途**：文章分类管理

| 字段名        | 类型         | 约束                          | 默认值                        | 说明             |
|---------------|--------------|-------------------------------|-------------------------------|------------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 分类ID           |
| name          | VARCHAR(50)  | NOT NULL                      | -                             | 分类名称         |
| description   | VARCHAR(200) | -                             | NULL                          | 分类描述         |
| sort_order    | INT          | -                             | 0                             | 排序顺序         |
| create_time   | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间         |
| is_hidden     | TINYINT(1)   | -                             | 0                             | 是否隐藏         |
| update_time   | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间         |

**索引**：
- `idx_category_hidden(is_hidden)`
- `idx_category_sort(sort_order)`

---

### 10. 博客/文章表 (`sys_blog`)
**用途**：存储博客文章

| 字段名          | 类型         | 约束                          | 默认值                        | 说明             |
|-----------------|--------------|-------------------------------|-------------------------------|------------------|
| id              | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 文章ID           |
| category_id     | BIGINT       | FOREIGN KEY                   | NULL                          | 分类ID           |
| title           | VARCHAR(200) | NOT NULL                      | -                             | 文章标题         |
| summary         | VARCHAR(500) | -                             | NULL                          | 文章摘要         |
| content         | LONGTEXT     | -                             | NULL                          | 文章内容         |
| cover_image     | VARCHAR(200) | -                             | NULL                          | 封面图片         |
| tags            | VARCHAR(200) | -                             | NULL                          | 标签（逗号分隔） |
| author_id       | BIGINT       | FOREIGN KEY                   | NULL                          | 作者ID           |
| view_count      | INT          | -                             | 0                             | 浏览量           |
| comment_count   | INT          | -                             | 0                             | 评论数           |
| like_count      | INT          | -                             | 0                             | 点赞数           |
| is_hidden       | TINYINT(1)   | -                             | 0                             | 是否隐藏         |
| is_top          | TINYINT(1)   | -                             | 0                             | 是否置顶         |
| is_recommend    | TINYINT(1)   | -                             | 0                             | 是否推荐         |
| create_time     | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间         |
| update_time     | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间         |

**约束**：
- `FOREIGN KEY (category_id) REFERENCES sys_category(id) ON DELETE SET NULL`
- `FOREIGN KEY (author_id) REFERENCES sys_user(id) ON DELETE SET NULL`

**索引**：
- `idx_blog_category(category_id)`
- `idx_blog_author(author_id)`
- `idx_blog_create_time(create_time)`
- `idx_blog_hidden(is_hidden)`
- `idx_blog_top(is_top)`

---

### 11. 评论表 (`sys_comment`)
**用途**：文章评论管理

| 字段名        | 类型         | 约束                          | 默认值                        | 说明             |
|---------------|--------------|-------------------------------|-------------------------------|------------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 评论ID           |
| blog_id       | BIGINT       | FOREIGN KEY, NOT NULL         | -                             | 文章ID           |
| parent_id     | BIGINT       | -                             | 0                             | 父评论ID         |
| user_id       | BIGINT       | FOREIGN KEY                   | NULL                          | 用户ID           |
| username      | VARCHAR(50)  | NOT NULL                      | -                             | 评论者名称       |
| email         | VARCHAR(100) | -                             | NULL                          | 邮箱             |
| avatar_url    | VARCHAR(200) | -                             | NULL                          | 头像URL          |
| website       | VARCHAR(200) | -                             | NULL                          | 个人网站         |
| content       | TEXT         | NOT NULL                      | -                             | 评论内容         |
| status        | TINYINT      | -                             | 0                             | 状态：0=待审核，1=已通过 |
| like_count    | INT          | -                             | 0                             | 点赞数           |
| device_info   | VARCHAR(200) | -                             | NULL                          | 设备信息         |
| ip_address    | VARCHAR(50)  | -                             | NULL                          | IP地址           |
| is_admin      | TINYINT(1)   | -                             | 0                             | 是否管理员评论   |
| create_time   | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间         |
| update_time   | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间         |

**约束**：
- `FOREIGN KEY (blog_id) REFERENCES sys_blog(id) ON DELETE CASCADE`
- `FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE SET NULL`

**索引**：
- `idx_comment_blog(blog_id)`
- `idx_comment_parent(parent_id)`
- `idx_comment_user(user_id)`
- `idx_comment_status(status)`
- `idx_comment_create_time(create_time)`

---

### 12. 评论点赞表 (`sys_comment_like`)
**用途**：记录评论点赞信息

| 字段名        | 类型       | 约束                                      | 默认值                | 说明         |
|---------------|------------|-------------------------------------------|-----------------------|--------------|
| id            | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 点赞记录ID   |
| comment_id    | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 评论ID       |
| user_id       | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 用户ID       |
| create_time   | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间     |

**约束**：
- `UNIQUE KEY uk_comment_user(comment_id, user_id)`
- `FOREIGN KEY (comment_id) REFERENCES sys_comment(id) ON DELETE CASCADE`
- `FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE`

**索引**：
- `idx_comment_like_comment(comment_id)`
- `idx_comment_like_user(user_id)`

---

### 13. 标签表 (`sys_tag`)
**用途**：文章标签管理

| 字段名        | 类型         | 约束                          | 默认值                        | 说明         |
|---------------|--------------|-------------------------------|-------------------------------|--------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT   | -                             | 标签ID       |
| name          | VARCHAR(50)  | UNIQUE, NOT NULL              | -                             | 标签名称     |
| create_time   | DATETIME     | -                             | CURRENT_TIMESTAMP             | 创建时间     |
| update_time   | DATETIME     | -                             | CURRENT_TIMESTAMP ON UPDATE   | 更新时间     |

---

### 14. 文章-标签关联表 (`sys_blog_tag`)
**用途**：文章与标签的多对多关系

| 字段名        | 类型       | 约束                                      | 默认值                | 说明     |
|---------------|------------|-------------------------------------------|-----------------------|----------|
| id            | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 关联ID   |
| blog_id       | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 文章ID   |
| tag_id        | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 标签ID   |
| create_time   | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间 |

**约束**：
- `UNIQUE KEY uk_blog_tag(blog_id, tag_id)`
- `FOREIGN KEY (blog_id) REFERENCES sys_blog(id) ON DELETE CASCADE`
- `FOREIGN KEY (tag_id) REFERENCES sys_tag(id) ON DELETE CASCADE`

---

### 15. 文章点赞表 (`sys_blog_like`)
**用途**：记录文章点赞信息

| 字段名        | 类型       | 约束                                      | 默认值                | 说明     |
|---------------|------------|-------------------------------------------|-----------------------|----------|
| id            | BIGINT     | PRIMARY KEY, AUTO_INCREMENT               | -                     | 点赞记录ID |
| blog_id       | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 文章ID   |
| user_id       | BIGINT     | FOREIGN KEY, NOT NULL                     | -                     | 用户ID   |
| create_time   | DATETIME   | -                                         | CURRENT_TIMESTAMP     | 创建时间 |

**约束**：
- `UNIQUE KEY uk_blog_user(blog_id, user_id)`
- `FOREIGN KEY (blog_id) REFERENCES sys_blog(id) ON DELETE CASCADE`
- `FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE`  