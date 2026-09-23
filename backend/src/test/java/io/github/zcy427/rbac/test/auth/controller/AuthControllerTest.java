package io.github.zcy427.rbac.test.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.service.SysUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 用户认证接口集成测试
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthControllerTest {

    private static final String USERNAME_PREFIX = "auth_controller_";
    private static final String RAW_PASSWORD = "TestPassword123!";

    private final MockMvc mockMvc;
    private final SysUserService sysUserService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    AuthControllerTest(
            MockMvc mockMvc,
            SysUserService sysUserService,
            JdbcTemplate jdbcTemplate
    ) {
        this.mockMvc = mockMvc;
        this.sysUserService = sysUserService;
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
    void loginShouldReturnTokenResponse() throws Exception {
        String username = uniqueUsername();
        Long userId = createUser(username, 1);

        SaTokenInfo tokenInfo = Mockito.mock(SaTokenInfo.class);
        Mockito.when(tokenInfo.getTokenTimeout()).thenReturn(604800L);

        try (MockedStatic<StpUtil> stpUtil = Mockito.mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getTokenInfo).thenReturn(tokenInfo);
            stpUtil.when(StpUtil::getTokenValue).thenReturn("test-token");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody(username, RAW_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data.token")
                            .value("test-token"))
                    .andExpect(jsonPath("$.data.tokenType")
                            .value("Bearer"))
                    .andExpect(jsonPath("$.data.expiresIn")
                            .value(604800))
                    .andExpect(jsonPath("$.data.mustChangePassword")
                            .value(true))
                    .andExpect(jsonPath("$.traceId").isNotEmpty())
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            stpUtil.verify(() -> StpUtil.login(userId));
        }
    }

    @Test
    void loginShouldValidateRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordShouldReturnUnauthorized() throws Exception {
        String username = uniqueUsername();
        createUser(username, 1);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(username, "WrongPassword123!")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message")
                        .value("用户名或密码错误"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void loginWithDisabledAccountShouldReturnForbidden() throws Exception {
        String username = uniqueUsername();
        createUser(username, 0);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(username, RAW_PASSWORD)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code")
                        .value("ACCOUNT_DISABLED"))
                .andExpect(jsonPath("$.message")
                        .value("账号已禁用"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    private Long createUser(String username, Integer status) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setPassword(RAW_PASSWORD);
        request.setNickname("认证接口测试用户");
        request.setStatus(status);
        return sysUserService.createUser(request);
    }

    private String loginBody(String username, String password) {
        return """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);
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
