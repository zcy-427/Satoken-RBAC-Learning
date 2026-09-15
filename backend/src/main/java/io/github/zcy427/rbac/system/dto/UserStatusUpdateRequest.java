package io.github.zcy427.rbac.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 修改用户状态请求参数
@Data
public class UserStatusUpdateRequest {

    @NotNull(message = "用户状态不能为空")
    @Min(value = 0, message = "用户状态值不正确")
    @Max(value = 1, message = "用户状态值不正确")
    private Integer status;
}
