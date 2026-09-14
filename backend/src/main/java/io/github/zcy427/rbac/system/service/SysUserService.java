package io.github.zcy427.rbac.system.service;

import io.github.zcy427.rbac.system.dto.UserCreateRequest;

// 系统用户业务接口
public interface SysUserService {
    Long createUser(UserCreateRequest request);
}
