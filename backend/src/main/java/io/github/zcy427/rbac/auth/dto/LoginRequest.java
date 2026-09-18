package io.github.zcy427.rbac.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 用户名密码登录请求参数
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 56, message = "密码长度必须为8到56个字符")
    private String password;
}
