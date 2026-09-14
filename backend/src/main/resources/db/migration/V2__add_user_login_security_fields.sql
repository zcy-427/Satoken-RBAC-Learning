ALTER TABLE sys_user
    ADD COLUMN must_change_password TINYINT NOT NULL DEFAULT 1
        COMMENT '是否必须修改密码：0 否，1 是' AFTER password_hash,
    ADD COLUMN login_fail_count INT NOT NULL DEFAULT 0
        COMMENT '连续登录失败次数' AFTER must_change_password,
    ADD COLUMN locked_until TIMESTAMP NULL
        COMMENT '账号锁定截止时间' AFTER login_fail_count;
