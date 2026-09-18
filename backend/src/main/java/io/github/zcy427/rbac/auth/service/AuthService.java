package io.github.zcy427.rbac.auth.service;

import io.github.zcy427.rbac.auth.dto.LoginRequest;
import io.github.zcy427.rbac.auth.dto.LoginResponse;

// 认证业务接口
public interface AuthService {

    LoginResponse login(LoginRequest request);
}
