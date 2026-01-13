-- schema.sql
CREATE DATABASE IF NOT EXISTS myblog_sql CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE myblog_sql;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                                        username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
    email VARCHAR(50) UNIQUE COMMENT '邮箱（唯一）',
    avatar_url VARCHAR(200) COMMENT '头像URL',
    status INT DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
                                        code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码（唯一）',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    is_super_admin TINYINT(1) DEFAULT 0 COMMENT '是否超级管理员：0=否，1=是（只能有一个）',
    is_system TINYINT(1) DEFAULT 0 COMMENT '是否系统内置角色：0=否，1=是（不可删除）',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status INT DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
                                              parent_id BIGINT DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
                                              code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码（唯一）',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    type VARCHAR(20) NOT NULL COMMENT '权限类型：MENU=菜单，BUTTON=按钮，API=接口，FIELD=字段',
    description VARCHAR(200) COMMENT '权限描述',
    icon VARCHAR(50) COMMENT '图标',
    path VARCHAR(200) COMMENT '路径/URL',
    component VARCHAR(200) COMMENT '前端组件',
    is_hidden TINYINT(1) DEFAULT 0 COMMENT '是否隐藏：0=显示，1=隐藏',
    is_affix TINYINT(1) DEFAULT 0 COMMENT '是否固定页签：0=否，1=是',
    is_keep_alive TINYINT(1) DEFAULT 0 COMMENT '是否缓存：0=否，1=是',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status INT DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
                                             user_id BIGINT NOT NULL COMMENT '用户ID',
                                             role_id BIGINT NOT NULL COMMENT '角色ID',
                                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                             UNIQUE KEY uk_user_role (user_id, role_id) COMMENT '防止重复关联',
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
                                                   role_id BIGINT NOT NULL COMMENT '角色ID',
                                                   permission_id BIGINT NOT NULL COMMENT '权限ID',
                                                   create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                                   UNIQUE KEY uk_role_permission (role_id, permission_id) COMMENT '防止重复关联',
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- 权限组表（用于组织权限）
CREATE TABLE IF NOT EXISTS sys_permission_group (
                                                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限组ID',
                                                    name VARCHAR(50) NOT NULL COMMENT '权限组名称',
    description VARCHAR(200) COMMENT '权限组描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status INT DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限组表';

-- 权限-权限组关联表
CREATE TABLE IF NOT EXISTS sys_permission_group_item (
                                                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
                                                         group_id BIGINT NOT NULL COMMENT '权限组ID',
                                                         permission_id BIGINT NOT NULL COMMENT '权限ID',
                                                         sort_order INT DEFAULT 0 COMMENT '排序顺序',
                                                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                                         UNIQUE KEY uk_group_permission (group_id, permission_id) COMMENT '防止重复关联',
    FOREIGN KEY (group_id) REFERENCES sys_permission_group(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限-权限组关联表';

-- 角色权限组关联表（批量分配权限）
CREATE TABLE IF NOT EXISTS sys_role_permission_group (
                                                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
                                                         role_id BIGINT NOT NULL COMMENT '角色ID',
                                                         group_id BIGINT NOT NULL COMMENT '权限组ID',
                                                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                                         UNIQUE KEY uk_role_group (role_id, group_id) COMMENT '防止重复关联',
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES sys_permission_group(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限组关联表';

-- 分类表
CREATE TABLE IF NOT EXISTS sys_category (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
                                            name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序（数字越大越靠前）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_hidden TINYINT(1) DEFAULT 0 COMMENT '是否隐藏：0=显示，1=隐藏',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 博客/文章表
CREATE TABLE IF NOT EXISTS sys_blog (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
                                        category_id BIGINT COMMENT '分类ID',
                                        title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) COMMENT '文章摘要',
    content LONGTEXT COMMENT '文章内容',
    cover_image VARCHAR(200) COMMENT '封面图片URL',
    tags VARCHAR(200) COMMENT '标签（逗号分隔）',
    author_id BIGINT COMMENT '作者ID',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_hidden TINYINT(1) DEFAULT 0 COMMENT '是否隐藏：0=公开，1=私密',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
    is_recommend TINYINT(1) DEFAULT 0 COMMENT '是否推荐：0=否，1=是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (category_id) REFERENCES sys_category(id) ON DELETE SET NULL,
    FOREIGN KEY (author_id) REFERENCES sys_user(id) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客文章表';

-- 评论表
CREATE TABLE IF NOT EXISTS sys_comment (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
                                           blog_id BIGINT NOT NULL COMMENT '关联的文章ID',
                                           parent_id BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
                                           user_id BIGINT COMMENT '用户ID（已登录用户）',
                                           username VARCHAR(50) NOT NULL COMMENT '评论者名称',
    email VARCHAR(100) COMMENT '邮箱',
    avatar_url VARCHAR(200) COMMENT '头像URL',
    website VARCHAR(200) COMMENT '个人网站',
    content TEXT NOT NULL COMMENT '评论内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0=待审核，1=已通过，2=垃圾评论，3=已删除',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    is_admin TINYINT(1) DEFAULT 0 COMMENT '是否管理员评论：0=否，1=是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (blog_id) REFERENCES sys_blog(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 评论点赞表
CREATE TABLE IF NOT EXISTS sys_comment_like (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞记录ID',
                                                comment_id BIGINT NOT NULL COMMENT '评论ID',
                                                user_id BIGINT NOT NULL COMMENT '用户ID',
                                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                                UNIQUE KEY uk_comment_user (comment_id, user_id) COMMENT '防止重复点赞',
    FOREIGN KEY (comment_id) REFERENCES sys_comment(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- 标签表
CREATE TABLE IF NOT EXISTS sys_tag (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
                                       name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 文章-标签关联表
CREATE TABLE IF NOT EXISTS sys_blog_tag (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
                                            blog_id BIGINT NOT NULL COMMENT '文章ID',
                                            tag_id BIGINT NOT NULL COMMENT '标签ID',
                                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                            UNIQUE KEY uk_blog_tag (blog_id, tag_id) COMMENT '防止重复关联',
    FOREIGN KEY (blog_id) REFERENCES sys_blog(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES sys_tag(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关联表';

-- 文章点赞表
CREATE TABLE IF NOT EXISTS sys_blog_like (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞记录ID',
                                             blog_id BIGINT NOT NULL COMMENT '文章ID',
                                             user_id BIGINT NOT NULL COMMENT '用户ID',
                                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                                             UNIQUE KEY uk_blog_user (blog_id, user_id) COMMENT '防止重复点赞',
    FOREIGN KEY (blog_id) REFERENCES sys_blog(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞表';

-- 创建索引以提高查询性能
-- 使用存储过程检查并创建索引
DELIMITER //
CREATE PROCEDURE create_index_if_not_exists(
    IN table_name VARCHAR(64),
    IN index_name VARCHAR(64),
    IN index_columns VARCHAR(255)
)
BEGIN
    DECLARE index_exists INT;

SELECT COUNT(1) INTO index_exists
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name = table_name
  AND index_name = index_name;

IF index_exists = 0 THEN
        SET @sql = CONCAT('CREATE INDEX ', index_name, ' ON ', table_name, ' (', index_columns, ')');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
END IF;
END//
DELIMITER ;

-- 调用存储过程创建索引
CALL create_index_if_not_exists('sys_user', 'idx_user_status', 'status');
CALL create_index_if_not_exists('sys_role', 'idx_role_code', 'code');
CALL create_index_if_not_exists('sys_role', 'idx_role_status', 'status');
CALL create_index_if_not_exists('sys_role', 'idx_role_super_admin', 'is_super_admin');
CALL create_index_if_not_exists('sys_permission', 'idx_permission_code', 'code');
CALL create_index_if_not_exists('sys_permission', 'idx_permission_parent', 'parent_id');
CALL create_index_if_not_exists('sys_permission', 'idx_permission_type', 'type');
CALL create_index_if_not_exists('sys_permission', 'idx_permission_status', 'status');
CALL create_index_if_not_exists('sys_user_role', 'idx_user_role_user', 'user_id');
CALL create_index_if_not_exists('sys_user_role', 'idx_user_role_role', 'role_id');
CALL create_index_if_not_exists('sys_role_permission', 'idx_role_permission_role', 'role_id');
CALL create_index_if_not_exists('sys_role_permission', 'idx_role_permission_permission', 'permission_id');
CALL create_index_if_not_exists('sys_blog', 'idx_blog_category', 'category_id');
CALL create_index_if_not_exists('sys_blog', 'idx_blog_author', 'author_id');
CALL create_index_if_not_exists('sys_blog', 'idx_blog_create_time', 'create_time');
CALL create_index_if_not_exists('sys_blog', 'idx_blog_hidden', 'is_hidden');
CALL create_index_if_not_exists('sys_blog', 'idx_blog_top', 'is_top');
CALL create_index_if_not_exists('sys_category', 'idx_category_hidden', 'is_hidden');
CALL create_index_if_not_exists('sys_category', 'idx_category_sort', 'sort_order');
CALL create_index_if_not_exists('sys_comment', 'idx_comment_blog', 'blog_id');
CALL create_index_if_not_exists('sys_comment', 'idx_comment_parent', 'parent_id');
CALL create_index_if_not_exists('sys_comment', 'idx_comment_user', 'user_id');
CALL create_index_if_not_exists('sys_comment', 'idx_comment_status', 'status');
CALL create_index_if_not_exists('sys_comment', 'idx_comment_create_time', 'create_time');
CALL create_index_if_not_exists('sys_comment_like', 'idx_comment_like_comment', 'comment_id');
CALL create_index_if_not_exists('sys_comment_like', 'idx_comment_like_user', 'user_id');

-- 删除存储过程
DROP PROCEDURE IF EXISTS create_index_if_not_exists;

-- 插入默认数据 - 只在表为空时插入
-- 1. 插入默认角色（只在角色表为空时插入）
SET @role_count = (SELECT COUNT(*) FROM sys_role);
IF @role_count = 0 THEN
    INSERT INTO sys_role (code, name, description, is_super_admin, is_system, sort_order, status) VALUES
    ('SUPER_ADMIN', '超级管理员', '拥有系统所有权限，只能有一个', 1, 1, 100, 1),
    ('ADMIN', '普通管理员', '拥有系统大部分管理权限', 0, 1, 90, 1),
    ('AUTHOR', '文章作者', '可以发布和管理自己的文章', 0, 1, 80, 1),
    ('USER', '普通用户', '可以评论、点赞、收藏文章', 0, 1, 70, 1);
END IF;

-- 2. 插入默认权限（只在权限表为空时插入）
SET @permission_count = (SELECT COUNT(*) FROM sys_permission);
IF @permission_count = 0 THEN
    -- 系统管理权限
    INSERT INTO sys_permission (parent_id, code, name, type, description, sort_order) VALUES
    (0, 'system', '系统管理', 'MENU', '系统管理菜单', 100),
    (1, 'system:user', '用户管理', 'MENU', '用户管理', 101),
    (1, 'system:role', '角色管理', 'MENU', '角色管理', 102),
    (1, 'system:permission', '权限管理', 'MENU', '权限管理', 103);

    -- 用户管理权限
INSERT INTO sys_permission (parent_id, code, name, type, description, sort_order) VALUES
                                                                                      (2, 'system:user:list', '用户列表', 'API', '查看用户列表', 1),
                                                                                      (2, 'system:user:create', '创建用户', 'API', '创建用户', 2),
                                                                                      (2, 'system:user:edit', '编辑用户', 'API', '编辑用户', 3),
                                                                                      (2, 'system:user:delete', '删除用户', 'API', '删除用户', 4),
                                                                                      (2, 'system:user:assignRole', '分配角色', 'API', '为用户分配角色', 5);

-- 角色管理权限
INSERT INTO sys_permission (parent_id, code, name, type, description, sort_order) VALUES
                                                                                      (3, 'system:role:list', '角色列表', 'API', '查看角色列表', 1),
                                                                                      (3, 'system:role:create', '创建角色', 'API', '创建角色', 2),
                                                                                      (3, 'system:role:edit', '编辑角色', 'API', '编辑角色', 3),
                                                                                      (3, 'system:role:delete', '删除角色', 'API', '删除角色', 4),
                                                                                      (3, 'system:role:assignPermission', '分配权限', 'API', '为角色分配权限', 5);

-- 文章管理权限
INSERT INTO sys_permission (parent_id, code, name, type, description, sort_order) VALUES
                                                                                      (0, 'article', '文章管理', 'MENU', '文章管理菜单', 90),
                                                                                      (15, 'article:list', '文章列表', 'API', '查看文章列表', 1),
                                                                                      (15, 'article:create', '创建文章', 'API', '创建文章', 2),
                                                                                      (15, 'article:edit', '编辑文章', 'API', '编辑文章', 3),
                                                                                      (15, 'article:delete', '删除文章', 'API', '删除文章', 4),
                                                                                      (15, 'article:publish', '发布文章', 'API', '发布文章', 5);

-- 分类管理权限
INSERT INTO sys_permission (parent_id, code, name, type, description, sort_order) VALUES
                                                                                      (0, 'category', '分类管理', 'MENU', '分类管理菜单', 80),
                                                                                      (22, 'category:list', '分类列表', 'API', '查看分类列表', 1),
                                                                                      (22, 'category:create', '创建分类', 'API', '创建分类', 2),
                                                                                      (22, 'category:edit', '编辑分类', 'API', '编辑分类', 3),
                                                                                      (22, 'category:delete', '删除分类', 'API', '删除分类', 4);

-- 评论管理权限
INSERT INTO sys_permission (parent_id, code, name, type, description, sort_order) VALUES
                                                                                      (0, 'comment', '评论管理', 'MENU', '评论管理菜单', 70),
                                                                                      (27, 'comment:list', '评论列表', 'API', '查看评论列表', 1),
                                                                                      (27, 'comment:create', '创建评论', 'API', '创建评论', 2),
                                                                                      (27, 'comment:edit', '编辑评论', 'API', '编辑评论', 3),
                                                                                      (27, 'comment:delete', '删除评论', 'API', '删除评论', 4),
                                                                                      (27, 'comment:approve', '审核评论', 'API', '审核评论', 5);
END IF;

-- 3. 为超级管理员角色分配所有权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.code = 'SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- 4. 为普通管理员角色分配基本管理权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.code = 'ADMIN'
  AND p.code NOT LIKE 'system:permission%'
  AND p.code NOT LIKE 'system:role:assignPermission%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- 5. 为作者角色分配文章相关权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.code = 'AUTHOR'
  AND (p.code LIKE 'article:%' OR p.code LIKE 'category:list%')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- 6. 为普通用户角色分配基本权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.code = 'USER'
  AND (p.code IN ('article:list', 'comment:create', 'comment:list'))
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- 7. 插入默认权限组（只在权限组表为空时插入）
SET @group_count = (SELECT COUNT(*) FROM sys_permission_group);
IF @group_count = 0 THEN
    INSERT INTO sys_permission_group (name, description, sort_order, status) VALUES
    ('系统管理组', '包含所有系统管理权限', 100, 1),
    ('文章管理组', '包含所有文章管理权限', 90, 1),
    ('用户管理组', '包含用户管理相关权限', 80, 1);
END IF;

-- 8. 为权限组添加权限
-- 系统管理组权限
INSERT IGNORE INTO sys_permission_group_item (group_id, permission_id, sort_order)
SELECT g.id, p.id, 1
FROM sys_permission_group g, sys_permission p
WHERE g.name = '系统管理组' AND p.code LIKE 'system:%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_permission_group_item pgi
    WHERE pgi.group_id = g.id AND pgi.permission_id = p.id
);

-- 文章管理组权限
INSERT IGNORE INTO sys_permission_group_item (group_id, permission_id, sort_order)
SELECT g.id, p.id, 1
FROM sys_permission_group g, sys_permission p
WHERE g.name = '文章管理组' AND p.code LIKE 'article:%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_permission_group_item pgi
    WHERE pgi.group_id = g.id AND pgi.permission_id = p.id
);

-- 用户管理组权限
INSERT IGNORE INTO sys_permission_group_item (group_id, permission_id, sort_order)
SELECT g.id, p.id, 1
FROM sys_permission_group g, sys_permission p
WHERE g.name = '用户管理组' AND p.code LIKE 'system:user:%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_permission_group_item pgi
    WHERE pgi.group_id = g.id AND pgi.permission_id = p.id
);

-- 9. 为角色分配权限组
INSERT IGNORE INTO sys_role_permission_group (role_id, group_id)
SELECT r.id, g.id
FROM sys_role r, sys_permission_group g
WHERE r.code = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission_group rpg
    WHERE rpg.role_id = r.id AND rpg.group_id = g.id
);

-- 10. 创建默认管理员用户（只在管理员用户不存在时创建）
IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin') THEN
    INSERT INTO sys_user (username, password, email, status) VALUES
    ('admin', '$2a$10$你的BCrypt加密密码', 'admin@example.com', 1);
END IF;

-- 11. 为管理员用户分配超级管理员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND r.code = 'SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_role ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);