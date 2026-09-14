package io.github.zcy427.rbac.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.zcy427.rbac.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

// 系统用户数据访问接口
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}