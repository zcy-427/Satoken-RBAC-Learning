package io.github.zcy427.rbac.system.service;

import io.github.zcy427.rbac.common.result.PageResult;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.dto.UserQueryRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;
import io.github.zcy427.rbac.system.dto.UserStatusUpdateRequest;
import io.github.zcy427.rbac.system.dto.UserUpdateRequest;

// 系统用户业务接口
public interface SysUserService {
    Long createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    PageResult<UserResponse> pageUsers(UserQueryRequest request);

    void updateUser(Long id, UserUpdateRequest request);

    void updateUserStatus(Long id, UserStatusUpdateRequest request);

    void deleteUser(Long id);
}
