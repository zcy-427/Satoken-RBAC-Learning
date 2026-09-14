package io.github.zcy427.rbac.test.system.service;

import io.github.zcy427.rbac.common.exception.BusinessException;
import io.github.zcy427.rbac.common.exception.ErrorCode;
import io.github.zcy427.rbac.common.security.PasswordHasher;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import io.github.zcy427.rbac.system.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 系统用户业务集成测试
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class SysUserServiceTest {

    private final SysUserService sysUserService;
    private final SysUserMapper sysUserMapper;
    private final PasswordHasher passwordHasher;

    @Autowired
    SysUserServiceTest(
            SysUserService sysUserService,
            SysUserMapper sysUserMapper,
            PasswordHasher passwordHasher
    ) {
        this.sysUserService = sysUserService;
        this.sysUserMapper = sysUserMapper;
        this.passwordHasher = passwordHasher;
    }

    @Test
    void shouldCreateUserWithHashedPasswordAndDefaults() {
        String username = "test_" + UUID.randomUUID();
        String rawPassword = "TestPassword123!";
        UserCreateRequest request = createRequest("  " + username + "  ", rawPassword);

        Long userId = sysUserService.createUser(request);
        SysUser savedUser = sysUserMapper.selectById(userId);

        assertThat(userId).isNotNull();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getPasswordHash()).isNotEqualTo(rawPassword);
        assertThat(passwordHasher.matches(rawPassword, savedUser.getPasswordHash())).isTrue();
        assertThat(savedUser.getGender()).isZero();
        assertThat(savedUser.getStatus()).isEqualTo(1);
        assertThat(savedUser.getMustChangePassword()).isTrue();
        assertThat(savedUser.getLoginFailCount()).isZero();
    }

    @Test
    void shouldRejectDuplicateUsername() {
        String username = "test_" + UUID.randomUUID();
        UserCreateRequest request = createRequest(username, "TestPassword123!");

        sysUserService.createUser(request);

        assertThatThrownBy(() -> sysUserService.createUser(request))
                .isInstanceOfSatisfying(
                        BusinessException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS)
                );
    }

    private UserCreateRequest createRequest(String username, String password) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setNickname("测试用户");
        return request;
    }
}