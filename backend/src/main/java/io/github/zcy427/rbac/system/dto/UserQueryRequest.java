package io.github.zcy427.rbac.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 用户分页查询参数
@Data
public class UserQueryRequest {

    @Min(value = 1, message = "页码不能小于1")
    private int page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private int size = 20;

    @Size(max = 64, message = "用户名长度不能超过64个字符")
    private String username;

    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    @Min(value = 0, message = "用户状态值不正确")
    @Max(value = 1, message = "用户状态值不正确")
    private Integer status;

    @Min(value = 1, message = "部门ID必须大于0")
    private Long deptId;
}