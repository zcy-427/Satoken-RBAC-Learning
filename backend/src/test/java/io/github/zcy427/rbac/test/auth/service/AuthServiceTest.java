package io.github.zcy427.rbac.test.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.zcy427.rbac.auth.dto.LoginRequest;
import io.github.zcy427.rbac.auth.dto.LoginResponse;
import io.github.zcy427.rbac.auth.service.AuthService;
import io.github.zcy427.rbac.common.exception.BusinessException;
import io.github.zcy427.rbac.common.exception.ErrorCode;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import io.github.zcy427.rbac.system.service.SysUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 登录认证业务集成测试
@SpringBootTest
@ActiveProfiles("dev")
class AuthServiceTest {

    private static final String USERNAME_PREFIX = "auth_test_";
    private static final String RAW_PASSWORD = "TestPassword123!";

    private final AuthService authService;
    private final SysUserService sysUserService;
    private final SysUserMapper sysUserMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    AuthServiceTest(
            AuthService authService,
            SysUserService sysUserService,
            SysUserMapper sysUserMapper,
            JdbcTemplate jdbcTemplate
    ) {
        this.authService = authService;
        this.sysUserService = sysUserService;
        this.sysUserMapper = sysUserMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void cleanBeforeTest() {
        deleteTestUsers();
    }

    @AfterEach
    void cleanAfterTest() {
        deleteTestUsers();
    }

    @Test
    void shouldLoginAndResetSecurityState() {
        SysUser user = createUser(1);
        LocalDateTime expiredLockTime = LocalDateTime.now().minusMinutes(1);
        sysUserMapper.update(
                null,
                Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getId, user.getId())
                        .set(SysUser::getLoginFailCount, 4)
                        .set(SysUser::getLockedUntil, expiredLockTime)
        );

        SaTokenInfo tokenInfo = Mockito.mock(SaTokenInfo.class);
        Mockito.when(tokenInfo.getTokenTimeout()).thenReturn(604800L);

        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getTokenInfo).thenReturn(tokenInfo);
            stpUtil.when(StpUtil::getTokenValue).thenReturn("test-token");

            LoginResponse response = authService.login(
                    loginRequest(user.getUsername(), RAW_PASSWORD)
            );

            stpUtil.verify(() -> StpUtil.login(user.getId()));
            assertThat(response.getToken()).isEqualTo("test-token");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getExpiresIn()).isEqualTo(604800L);
            assertThat(response.getMustChangePassword()).isTrue();
        }

        SysUser loggedInUser = sysUserMapper.selectById(user.getId());
        assertThat(loggedInUser.getLoginFailCount()).isZero();
        assertThat(loggedInUser.getLockedUntil()).isNull();
        assertThat(loggedInUser.getLastLoginAt()).isNotNull();
    }

    @Test
    void shouldPersistLoginFailureCount() {
        SysUser user = createUser(1);

        assertThatThrownBy(
                () -> authService.login(
                        loginRequest(user.getUsername(), "WrongPassword123!")
                )
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_CREDENTIALS)
        );

        SysUser failedUser = sysUserMapper.selectById(user.getId());
        assertThat(failedUser.getLoginFailCount()).isEqualTo(1);
        assertThat(failedUser.getLockedUntil()).isNull();
    }

    @Test
    void shouldLockAccountAfterFifthFailure() {
        SysUser user = createUser(1);
        sysUserMapper.update(
                null,
                Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getId, user.getId())
                        .set(SysUser::getLoginFailCount, 4)
        );

        assertThatThrownBy(
                () -> authService.login(
                        loginRequest(user.getUsername(), "WrongPassword123!")
                )
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_CREDENTIALS)
        );

        SysUser lockedUser = sysUserMapper.selectById(user.getId());
        assertThat(lockedUser.getLoginFailCount()).isEqualTo(5);
        assertThat(lockedUser.getLockedUntil())
                .isAfter(LocalDateTime.now().plusMinutes(14))
                .isBefore(LocalDateTime.now().plusMinutes(16));
    }

    @Test
    void shouldRejectLockedAccount() {
        SysUser user = createUser(1);
        sysUserMapper.update(
                null,
                Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getId, user.getId())
                        .set(
                                SysUser::getLockedUntil,
                                LocalDateTime.now().plusMinutes(10)
                        )
        );

        assertThatThrownBy(
                () -> authService.login(
                        loginRequest(user.getUsername(), RAW_PASSWORD)
                )
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.ACCOUNT_LOCKED)
        );
    }

    @Test
    void shouldRejectDisabledAccount() {
        SysUser user = createUser(0);

        assertThatThrownBy(
                () -> authService.login(
                        loginRequest(user.getUsername(), RAW_PASSWORD)
                )
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.ACCOUNT_DISABLED)
        );
    }

    @Test
    void shouldHideWhetherUsernameExists() {
        assertThatThrownBy(
                () -> authService.login(
                        loginRequest(uniqueUsername(), RAW_PASSWORD)
                )
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_CREDENTIALS)
        );
    }

    private SysUser createUser(Integer status) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(uniqueUsername());
        request.setPassword(RAW_PASSWORD);
        request.setNickname("登录测试用户");
        request.setStatus(status);

        Long userId = sysUserService.createUser(request);
        return sysUserMapper.selectById(userId);
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private String uniqueUsername() {
        return USERNAME_PREFIX
                + UUID.randomUUID().toString().replace("-", "");
    }

    private void deleteTestUsers() {
        jdbcTemplate.update(
                "DELETE FROM sys_user WHERE username LIKE ?",
                USERNAME_PREFIX + "%"
        );
    }
}
