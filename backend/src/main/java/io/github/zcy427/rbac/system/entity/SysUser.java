package io.github.zcy427.rbac.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.zcy427.rbac.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 系统用户实体
@Getter
@Setter
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    @TableField("password_hash")
    private String passwordHash;

    private String nickname;

    private String avatar;

    private String email;

    private String phone;

    private Integer gender;

    @TableField("dept_id")
    private Long deptId;

    private Integer status;

    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;
}
