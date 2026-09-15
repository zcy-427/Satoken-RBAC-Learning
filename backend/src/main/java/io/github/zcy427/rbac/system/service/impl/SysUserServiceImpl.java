package io.github.zcy427.rbac.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zcy427.rbac.common.exception.BusinessException;
import io.github.zcy427.rbac.common.exception.ErrorCode;
import io.github.zcy427.rbac.common.result.PageResult;
import io.github.zcy427.rbac.common.security.PasswordHasher;
import io.github.zcy427.rbac.system.dto.UserCreateRequest;
import io.github.zcy427.rbac.system.dto.UserQueryRequest;
import io.github.zcy427.rbac.system.dto.UserResponse;
import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import io.github.zcy427.rbac.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;


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

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserResponse> pageUsers(UserQueryRequest request) {
        String username = normalizeKeyword(request.getUsername());
        String nickname = normalizeKeyword(request.getNickname());

        LambdaQueryWrapper<SysUser> queryWrapper =
                Wrappers.<SysUser>lambdaQuery()
                        .like(username != null, SysUser::getUsername, username)
                        .like(nickname != null, SysUser::getNickname, nickname)
                        .eq(request.getStatus() != null,
                                SysUser::getStatus,
                                request.getStatus())
                        .eq(request.getDeptId() != null,
                                SysUser::getDeptId,
                                request.getDeptId())
                        .orderByDesc(SysUser::getCreatedAt);

        Page<SysUser> userPage = sysUserMapper.selectPage(
                new Page<>(request.getPage(), request.getSize()),
                queryWrapper
        );

        List<UserResponse> users = userPage.getRecords()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResult<>(
                users,
                userPage.getCurrent(),
                userPage.getSize(),
                userPage.getTotal(),
                userPage.getPages()
        );
    }

    private String normalizeKeyword(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    // 将用户实体转换为安全的接口响应，不返回密码等内部字段
    private UserResponse toResponse(SysUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        response.setDeptId(
                user.getDeptId() == null
                        ? null
                        : user.getDeptId().toString()
        );
        response.setStatus(user.getStatus());
        response.setMustChangePassword(user.getMustChangePassword());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setLockedUntil(user.getLockedUntil());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
