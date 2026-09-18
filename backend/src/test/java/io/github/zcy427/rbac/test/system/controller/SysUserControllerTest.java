package io.github.zcy427.rbac.test.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;
import io.github.zcy427.rbac.system.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 系统用户接口集成测试
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class SysUserControllerTest {

    private final MockMvc mockMvc;
    private final SysUserService sysUserService;

    @Autowired
    SysUserControllerTest(
            MockMvc mockMvc,
            SysUserService sysUserService
    ) {
        this.mockMvc = mockMvc;
        this.sysUserService = sysUserService;
    }

    @Test
    void createUserShouldReturnCreated() throws Exception {
        String username = uniqueUsername();
        String requestBody = """
                {
                  "username": "%s",
                  "password": "Test123456",
                  "nickname": "接口测试用户",
                  "status": 1
                }
                """.formatted(username);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isString())
                .andExpect(jsonPath("$.traceId").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void createUserWithDuplicateUsernameShouldReturnConflict() throws Exception {
        String username = uniqueUsername();
        String requestBody = """
                {
                  "username": "%s",
                  "password": "Test123456",
                  "nickname": "重复用户名测试"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value("USERNAME_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("用户名已存在"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void createUserWithInvalidRequestShouldReturnBadRequest() throws Exception {
        String requestBody = """
                {
                  "username": "",
                  "password": "123",
                  "nickname": ""
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        String username = uniqueUsername();
        Long userId = createUser(username, "详情测试用户", 1);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id")
                        .value(userId.toString()))
                .andExpect(jsonPath("$.data.id").isString())
                .andExpect(jsonPath("$.data.username")
                        .value(username))
                .andExpect(jsonPath("$.data.nickname")
                        .value("详情测试用户"))
                .andExpect(jsonPath("$.data.passwordHash")
                        .doesNotExist())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void getMissingUserShouldReturnNotFound() throws Exception {
        mockMvc.perform(get(
                        "/api/v1/users/{id}",
                        Long.MAX_VALUE
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("用户不存在"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void pageUsersShouldReturnFilteredPage() throws Exception {
        String prefix = uniqueUsername();

        createUser(prefix + "_enabled", "启用用户", 1);
        createUser(prefix + "_disabled", "禁用用户", 0);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "1")
                        .param("size", "10")
                        .param("username", prefix)
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.pages").value(1))
                .andExpect(jsonPath("$.data.list.length()")
                        .value(1))
                .andExpect(jsonPath("$.data.list[0].username")
                        .value(prefix + "_enabled"))
                .andExpect(jsonPath("$.data.list[0].id")
                        .isString())
                .andExpect(jsonPath(
                        "$.data.list[0].passwordHash"
                ).doesNotExist());
    }

    @Test
    void pageUsersWithInvalidSizeShouldReturnBadRequest()
            throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .param("page", "1")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void updateUserShouldReturnSuccessAndPersistChanges()
            throws Exception {
        Long userId = createUser(
                uniqueUsername(),
                "修改前昵称",
                1
        );
        String requestBody = """
                {
                  "nickname": "修改后昵称",
                  "avatar": "https://example.com/avatar.png",
                  "email": "updated@example.com",
                  "phone": "13800138000",
                  "gender": 2,
                  "deptId": 100
                }
                """;

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.traceId").isNotEmpty());

        UserResponse updatedUser =
                sysUserService.getUserById(userId);
        assertThat(updatedUser.getNickname()).isEqualTo("修改后昵称");
        assertThat(updatedUser.getAvatar())
                .isEqualTo("https://example.com/avatar.png");
        assertThat(updatedUser.getEmail())
                .isEqualTo("updated@example.com");
        assertThat(updatedUser.getPhone()).isEqualTo("13800138000");
        assertThat(updatedUser.getGender()).isEqualTo(2);
        assertThat(updatedUser.getDeptId()).isEqualTo("100");
    }

    @Test
    void updateUserWithInvalidRequestShouldReturnBadRequest()
            throws Exception {
        Long userId = createUser(
                uniqueUsername(),
                "原昵称",
                1
        );
        String requestBody = """
                {
                  "nickname": "",
                  "email": "invalid-email"
                }
                """;

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void updateUserStatusShouldDisableUserAndLogoutSessions()
            throws Exception {
        Long userId = createUser(
                uniqueUsername(),
                "待禁用用户",
                1
        );
        String requestBody = """
                {
                  "status": 0
                }
                """;

        try (MockedStatic<StpUtil> stpUtil =
                     Mockito.mockStatic(StpUtil.class)) {
            mockMvc.perform(patch(
                            "/api/v1/users/{id}/status",
                            userId
                    )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.traceId").isNotEmpty());

            stpUtil.verify(() -> StpUtil.logout(userId));
        }

        UserResponse disabledUser =
                sysUserService.getUserById(userId);
        assertThat(disabledUser.getStatus()).isZero();
    }

    @Test
    void updateUserStatusWithInvalidValueShouldReturnBadRequest()
            throws Exception {
        Long userId = createUser(
                uniqueUsername(),
                "状态校验用户",
                1
        );
        String requestBody = """
                {
                  "status": 2
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{id}/status", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void deleteUserShouldReturnSuccessAndHideDeletedUser()
            throws Exception {
        Long userId = createUser(
                uniqueUsername(),
                "待删除用户",
                1
        );

        try (MockedStatic<StpUtil> stpUtil =
                     Mockito.mockStatic(StpUtil.class)) {
            mockMvc.perform(delete("/api/v1/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.traceId").isNotEmpty());

            stpUtil.verify(() -> StpUtil.logout(userId));
        }

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void deleteMissingUserShouldReturnNotFound() throws Exception {
        try (MockedStatic<StpUtil> stpUtil =
                     Mockito.mockStatic(StpUtil.class)) {
            mockMvc.perform(delete(
                            "/api/v1/users/{id}",
                            Long.MAX_VALUE
                    ))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code")
                            .value("USER_NOT_FOUND"))
                    .andExpect(jsonPath("$.message")
                            .value("用户不存在"));

            stpUtil.verifyNoInteractions();
        }
    }

    private String uniqueUsername() {
        return "api_" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }

    private Long createUser(
            String username,
            String nickname,
            int status
    ) {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setPassword("Test123456");
        request.setNickname(nickname);
        request.setStatus(status);
        return sysUserService.createUser(request);
    }
}
