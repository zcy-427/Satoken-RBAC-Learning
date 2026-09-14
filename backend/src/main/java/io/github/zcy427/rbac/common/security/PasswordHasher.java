package io.github.zcy427.rbac.common.security;

import cn.dev33.satoken.secure.BCrypt;
import org.springframework.stereotype.Component;

// 密码哈希组件
@Component
@SuppressWarnings("deprecation")
public class PasswordHasher {
    private static final int LOG_ROUNDS = 10;

    public String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public boolean matches(String rawPassword, String passwordHash) {
        return BCrypt.checkpw(rawPassword, passwordHash);
    }
}
