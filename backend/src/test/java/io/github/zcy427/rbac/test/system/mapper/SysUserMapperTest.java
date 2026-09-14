package io.github.zcy427.rbac.test.system.mapper;

import io.github.zcy427.rbac.system.entity.SysUser;
import io.github.zcy427.rbac.system.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// 系统用户 Mapper 集成测试
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class SysUserMapperTest {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    void shouldInsertQueryAndLogicDeleteUser() {
        SysUser user = new SysUser();
        user.setUsername("test_" + UUID.randomUUID());
        user.setPasswordHash("test-password-hash");
        user.setNickname("测试用户");
        user.setGender(0);
        user.setStatus(1);

        int insertRows = sysUserMapper.insert(user);

        assertThat(insertRows).isEqualTo(1);
        assertThat(user.getId()).isNotNull();

        SysUser savedUser = sysUserMapper.selectById(user.getId());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());

        int deleteRows = sysUserMapper.deleteById(user.getId());

        assertThat(deleteRows).isEqualTo(1);
        assertThat(sysUserMapper.selectById(user.getId())).isNull();
    }
}
