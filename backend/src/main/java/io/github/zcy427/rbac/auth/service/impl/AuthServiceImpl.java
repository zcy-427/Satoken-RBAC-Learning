package io.github.zcy427.rbac.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.zcy427.rbac.auth.dto.LoginRequest;
import io.github.zcy427.rbac.auth.dto.LoginResponse;
import io.github.zcy427.rbac.auth.service.AuthService;
import io.github.zcy427.rbac.common.exception.BusinessException;
import io.github.zcy427.rbac.common.exception.ErrorCode;
import io.github.zcy427.rbac.common.security.PasswordHasher;
import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// 认证业务实现
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final long LOCK_MINUTES = 15;

    private final SysUserMapper sysUserMapper;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        LocalDateTime now = LocalDateTime.now();

        SysUser user = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, username)
        );

        if (user == null || user.getPasswordHash() == null) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        if (user.getLockedUntil() != null
                && user.getLockedUntil().isAfter(now)) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        int failCount = user.getLoginFailCount() == null
                ? 0
                : user.getLoginFailCount();

        if (user.getLockedUntil() != null
                && !user.getLockedUntil().isAfter(now)) {
            failCount = 0;
        }

        if (!passwordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            recordLoginFailure(user.getId(), failCount, now);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        sysUserMapper.update(
                null,
                Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getId, user.getId())
                        .set(SysUser::getLoginFailCount, 0)
                        .set(SysUser::getLockedUntil, null)
                        .set(SysUser::getLastLoginAt, now)
        );

        StpUtil.login(user.getId());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenInfo.getTokenTimeout());
        response.setMustChangePassword(user.getMustChangePassword());

        return response;
    }

    // 记录登录失败次数，达到阈值后临时锁定账号
    private void recordLoginFailure(
            Long userId,
            int currentFailCount,
            LocalDateTime now
    ) {
        int failCount = currentFailCount + 1;
        LocalDateTime lockedUntil = failCount >= MAX_LOGIN_FAIL_COUNT
                ? now.plusMinutes(LOCK_MINUTES)
                : null;

        sysUserMapper.update(
                null,
                Wrappers.<SysUser>lambdaUpdate()
                        .eq(SysUser::getId, userId)
                        .set(SysUser::getLoginFailCount, failCount)
                        .set(SysUser::getLockedUntil, lockedUntil)
        );
    }
}
