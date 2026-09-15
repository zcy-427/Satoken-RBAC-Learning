package io.github.zcy427.rbac.system.controller;

import io.github.zcy427.rbac.common.result.PageResult;
import io.github.zcy427.rbac.common.result.Result;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.dto.UserQueryRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;
import io.github.zcy427.rbac.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// 系统用户管理接口
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "创建用户")
    @PostMapping
    public ResponseEntity<Result<String>> createUser(
            @Valid @RequestBody UserCreateRequest request
    ) {
        Long userId = sysUserService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(
                        userId.toString(),
                        UUID.randomUUID().toString()
                ));
    }

    @Operation(summary = "查询用户详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserResponse>> getUserById(
            @PathVariable Long id
    ) {
        UserResponse user = sysUserService.getUserById(id);

        return ResponseEntity.ok(
                Result.success(
                        user,
                        UUID.randomUUID().toString()
                )
        );
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public ResponseEntity<Result<PageResult<UserResponse>>> pageUsers(
            @Valid @ModelAttribute UserQueryRequest request
    ) {
        PageResult<UserResponse> page =
                sysUserService.pageUsers(request);

        return ResponseEntity.ok(
                Result.success(
                        page,
                        UUID.randomUUID().toString()
                )
        );
    }
}
