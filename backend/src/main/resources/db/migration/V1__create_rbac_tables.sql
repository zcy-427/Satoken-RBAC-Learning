-- 创建 RBAC 核心数据表

CREATE TABLE sys_dept (
                          id BIGINT NOT NULL PRIMARY KEY COMMENT '部门 ID',
                          parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门 ID，0 表示顶级部门',
                          ancestors VARCHAR(500) NOT NULL DEFAULT '' COMMENT '祖级路径',
                          dept_code VARCHAR(64) NOT NULL COMMENT '部门编码',
                          dept_name VARCHAR(64) NOT NULL COMMENT '部门名称',
                          sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
                          leader_user_id BIGINT NULL COMMENT '负责人用户 ID',
                          phone VARCHAR(32) NULL COMMENT '联系电话',
                          email VARCHAR(128) NULL COMMENT '邮箱',
                          status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 停用',
                          deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
                          created_by BIGINT NULL COMMENT '创建人',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_by BIGINT NULL COMMENT '更新人',
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          UNIQUE KEY uk_dept_code (dept_code),
                          KEY idx_dept_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE sys_post (
                          id BIGINT NOT NULL PRIMARY KEY COMMENT '岗位 ID',
                          post_code VARCHAR(64) NOT NULL COMMENT '岗位编码',
                          post_name VARCHAR(64) NOT NULL COMMENT '岗位名称',
                          sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
                          status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 停用',
                          remark VARCHAR(500) NULL COMMENT '备注',
                          deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
                          created_by BIGINT NULL COMMENT '创建人',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_by BIGINT NULL COMMENT '更新人',
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          UNIQUE KEY uk_post_code (post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

CREATE TABLE sys_user (
                          id BIGINT NOT NULL PRIMARY KEY COMMENT '用户 ID',
                          username VARCHAR(64) NOT NULL COMMENT '用户名',
                          password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码哈希',
                          nickname VARCHAR(64) NOT NULL COMMENT '昵称',
                          avatar VARCHAR(512) NULL COMMENT '头像地址',
                          email VARCHAR(128) NULL COMMENT '邮箱',
                          phone VARCHAR(32) NULL COMMENT '手机号',
                          gender TINYINT NOT NULL DEFAULT 0 COMMENT '性别：0 未设置，1 男，2 女',
                          dept_id BIGINT NULL COMMENT '所属部门 ID',
                          status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 停用',
                          last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
                          deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
                          created_by BIGINT NULL COMMENT '创建人',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_by BIGINT NULL COMMENT '更新人',
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          UNIQUE KEY uk_user_username (username),
                          KEY idx_user_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sys_role (
                          id BIGINT NOT NULL PRIMARY KEY COMMENT '角色 ID',
                          role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
                          role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
                          data_scope TINYINT NOT NULL DEFAULT 1 COMMENT '数据范围：1 全部，2 本部门及下级，3 本部门，4 本人，5 自定义',
                          status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 停用',
                          remark VARCHAR(500) NULL COMMENT '备注',
                          deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
                          created_by BIGINT NULL COMMENT '创建人',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_by BIGINT NULL COMMENT '更新人',
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE sys_permission (
                                id BIGINT NOT NULL PRIMARY KEY COMMENT '权限 ID',
                                parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父权限 ID，0 表示顶级权限',
                                permission_code VARCHAR(128) NOT NULL COMMENT '权限编码',
                                permission_name VARCHAR(64) NOT NULL COMMENT '权限名称',
                                permission_type VARCHAR(16) NOT NULL COMMENT '权限类型：MENU、BUTTON、API、DATA',
                                route_path VARCHAR(256) NULL COMMENT '前端路由路径',
                                component VARCHAR(256) NULL COMMENT '前端组件路径',
                                http_method VARCHAR(16) NULL COMMENT '接口请求方法',
                                api_path VARCHAR(256) NULL COMMENT '接口路径',
                                sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
                                visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见：1 是，0 否',
                                status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 停用',
                                deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 否，1 是',
                                created_by BIGINT NULL COMMENT '创建人',
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                updated_by BIGINT NULL COMMENT '更新人',
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                UNIQUE KEY uk_permission_code (permission_code),
                                KEY idx_permission_parent_id (parent_id),
                                KEY idx_permission_type (permission_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

CREATE TABLE sys_user_role (
                               user_id BIGINT NOT NULL COMMENT '用户 ID',
                               role_id BIGINT NOT NULL COMMENT '角色 ID',
                               created_by BIGINT NULL COMMENT '创建人',
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (user_id, role_id),
                               KEY idx_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE sys_user_post (
                               user_id BIGINT NOT NULL COMMENT '用户 ID',
                               post_id BIGINT NOT NULL COMMENT '岗位 ID',
                               created_by BIGINT NULL COMMENT '创建人',
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (user_id, post_id),
                               KEY idx_user_post_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

CREATE TABLE sys_role_permission (
                                     role_id BIGINT NOT NULL COMMENT '角色 ID',
                                     permission_id BIGINT NOT NULL COMMENT '权限 ID',
                                     created_by BIGINT NULL COMMENT '创建人',
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (role_id, permission_id),
                                     KEY idx_role_permission_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

CREATE TABLE sys_role_dept (
                               role_id BIGINT NOT NULL COMMENT '角色 ID',
                               dept_id BIGINT NOT NULL COMMENT '部门 ID',
                               created_by BIGINT NULL COMMENT '创建人',
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (role_id, dept_id),
                               KEY idx_role_dept_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色自定义数据范围部门关联表';