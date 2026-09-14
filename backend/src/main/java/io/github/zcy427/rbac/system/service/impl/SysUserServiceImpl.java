package io.github.zcy427.rbac.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.zcy427.rbac.common.exception.BusinessException;
import io.github.zcy427.rbac.common.exception.ErrorCode;
import io.github.zcy427.rbac.common.security.PasswordHasher;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import io.github.zcy427.rbac.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 系统用户业务实现
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper sysUserMapper;
    private final PasswordHasher passwordHasher;

    // 校验用户名唯一性并保存经过 BCrypt 哈希处理的用户数据
    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        String username = request.getUsername().trim();

        Long userCount = sysUserMapper.selectCount(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, username)
        );

        if (userCount > 0) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setNickname(request.getNickname().trim());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender() == null ? 0 : request.getGender());
        user.setDeptId(request.getDeptId());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        user.setMustChangePassword(true);
        user.setLoginFailCount(0);

        int affectedRows = sysUserMapper.insert(user);
        if (affectedRows != 1) {
            throw new IllegalStateException("创建用户失败");
        }

        return user.getId();
    }
}
