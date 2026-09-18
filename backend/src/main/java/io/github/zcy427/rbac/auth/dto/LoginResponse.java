package io.github.zcy427.rbac.auth.dto;

import lombok.Data;

// 登录成功响应结果
@Data
public class LoginResponse {

    private String token;

    private String tokenType;

    private Long expiresIn;

    private Boolean mustChangePassword;
}
