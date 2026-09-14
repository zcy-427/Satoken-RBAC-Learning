package io.github.zcy427.rbac.test.common.security;

import io.github.zcy427.rbac.common.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// 密码哈希组件单元测试
class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void shouldHashAndMatchPassword() {
        String rawPassword = "TestPassword123!";

        String firstHash = passwordHasher.hash(rawPassword);
        String secondHash = passwordHasher.hash(rawPassword);

        assertThat(firstHash).isNotEqualTo(rawPassword);
        assertThat(firstHash).isNotEqualTo(secondHash);
        assertThat(passwordHasher.matches(rawPassword, firstHash)).isTrue();
        assertThat(passwordHasher.matches("WrongPassword123!", firstHash)).isFalse();
    }
}