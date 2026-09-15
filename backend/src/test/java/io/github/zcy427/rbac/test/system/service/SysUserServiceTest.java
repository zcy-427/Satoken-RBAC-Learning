package io.github.zcy427.rbac.test.system.service;

import io.github.zcy427.rbac.common.exception.BusinessException;
import io.github.zcy427.rbac.common.exception.ErrorCode;
import io.github.zcy427.rbac.common.security.PasswordHasher;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import io.github.zcy427.rbac.system.service.SysUserService;
import io.github.zcy427.rbac.common.result.PageResult;
import io.github.zcy427.rbac.system.dto.UserQueryRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;
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

    @Test
    void shouldGetUserById() {
        String username = "detail_" + UUID.randomUUID();
        Long userId = sysUserService.createUser(
                createRequest(username, "TestPassword123!")
        );

        UserResponse response = sysUserService.getUserById(userId);

        assertThat(response.getId()).isEqualTo(userId.toString());
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getNickname()).isEqualTo("测试用户");
        assertThat(response.getStatus()).isEqualTo(1);
        assertThat(response.getMustChangePassword()).isTrue();
    }

    @Test
    void shouldRejectMissingUser() {
        assertThatThrownBy(
                () -> sysUserService.getUserById(Long.MAX_VALUE)
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Test
    void shouldReturnPagedUsers() {
        String prefix = uniqueQueryPrefix();

        sysUserService.createUser(
                createRequest(prefix + "_a", "TestPassword123!")
        );
        sysUserService.createUser(
                createRequest(prefix + "_b", "TestPassword123!")
        );

        UserQueryRequest query = new UserQueryRequest();
        query.setPage(1);
        query.setSize(1);
        query.setUsername("  " + prefix + "  ");

        PageResult<UserResponse> result =
                sysUserService.pageUsers(query);

        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getPages()).isEqualTo(2);
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().getFirst().getUsername())
                .startsWith(prefix);
    }

    @Test
    void shouldFilterUsersByUsernameNicknameAndStatus() {
        String prefix = uniqueQueryPrefix();

        UserCreateRequest matched =
                createRequest(prefix + "_matched", "TestPassword123!");
        matched.setNickname("目标用户");
        matched.setStatus(1);
        sysUserService.createUser(matched);

        UserCreateRequest unmatched =
                createRequest(prefix + "_unmatched", "TestPassword123!");
        unmatched.setNickname("其他用户");
        unmatched.setStatus(0);
        sysUserService.createUser(unmatched);

        UserQueryRequest query = new UserQueryRequest();
        query.setUsername(prefix);
        query.setNickname("目标");
        query.setStatus(1);

        PageResult<UserResponse> result =
                sysUserService.pageUsers(query);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getList())
                .singleElement()
                .satisfies(user -> {
                    assertThat(user.getUsername())
                            .isEqualTo(prefix + "_matched");
                    assertThat(user.getNickname())
                            .isEqualTo("目标用户");
                    assertThat(user.getStatus()).isEqualTo(1);
                    assertThat(user.getId()).isNotBlank();
                });
    }

    private String uniqueQueryPrefix() {
        return "query_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);
    }

    private UserCreateRequest createRequest(String username, String password) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setNickname("测试用户");
        return request;
    }
}
