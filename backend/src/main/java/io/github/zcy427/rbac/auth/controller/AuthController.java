package io.github.zcy427.rbac.auth.controller;

import io.github.zcy427.rbac.auth.dto.LoginRequest;
import io.github.zcy427.rbac.auth.dto.LoginResponse;
import io.github.zcy427.rbac.auth.service.AuthService;
import io.github.zcy427.rbac.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

// 用户认证接口
@Tag(name = "用户认证")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户名密码登录")
    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                Result.success(
                        response,
                        UUID.randomUUID().toString()
                )
        );
    }
}
