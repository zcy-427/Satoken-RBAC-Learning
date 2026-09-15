package io.github.zcy427.rbac.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

// 用户查询响应对象
@Data
public class UserResponse {

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer gender;
    private String deptId;
    private Integer status;
    private Boolean mustChangePassword;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lockedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}