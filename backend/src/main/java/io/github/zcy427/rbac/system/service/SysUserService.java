package io.github.zcy427.rbac.system.service;

import io.github.zcy427.rbac.common.result.PageResult;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.dto.UserQueryRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;

// 系统用户业务接口
public interface SysUserService {
    Long createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    PageResult<UserResponse> pageUsers(UserQueryRequest request);
}
