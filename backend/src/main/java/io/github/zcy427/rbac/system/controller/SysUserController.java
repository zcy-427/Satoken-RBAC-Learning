package io.github.zcy427.rbac.system.controller;

import io.github.zcy427.rbac.common.result.PageResult;
import io.github.zcy427.rbac.common.result.Result;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.dto.UserQueryRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;
import io.github.zcy427.rbac.system.dto.UserStatusUpdateRequest;
import io.github.zcy427.rbac.system.dto.UserUpdateRequest;
import org.springframework.web.bind.annotation.*;
import io.github.zcy427.rbac.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @Operation(summary = "修改用户基础信息")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Void>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        sysUserService.updateUser(id, request);

        return ResponseEntity.ok(
                Result.<Void>success(
                        null,
                        UUID.randomUUID().toString()
                )
        );
    }

    @Operation(summary = "修改用户状态")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Result<Void>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        sysUserService.updateUserStatus(id, request);

        return ResponseEntity.ok(
                Result.<Void>success(
                        null,
                        UUID.randomUUID().toString()
                )
        );
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(
            @PathVariable Long id
    ) {
        sysUserService.deleteUser(id);

        return ResponseEntity.ok(
                Result.<Void>success(
                        null,
                        UUID.randomUUID().toString()
                )
        );
    }
}
