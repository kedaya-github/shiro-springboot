package com.wxl.shiro.base.service.jwt.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxl.shiro.base.api.dto.req.role.SettingResourceReqDTO;
import com.wxl.shiro.base.api.dto.req.role.SettingUserReqDTO;
import com.wxl.shiro.base.api.dto.req.user.RoleAddReqDTO;
import com.wxl.shiro.base.bo.Role;
import com.wxl.shiro.base.bo.RoleResource;
import com.wxl.shiro.base.bo.UserRole;
import com.wxl.shiro.base.exception.CamAirException;
import com.wxl.shiro.base.mapper.RoleMapper;
import com.wxl.shiro.base.mapper.RoleResourceMapper;
import com.wxl.shiro.base.mapper.UserMapper;
import com.wxl.shiro.base.mapper.UserRoleMapper;
import com.wxl.shiro.base.service.jwt.JwtRoleService;
import com.wxl.shiro.base.support.ShiroFilterSupport;
import com.wxl.shiro.base.utils.SequenceGenerator;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * TODO ： Jwt无状态的方式修改角色Role信息
 * @author Weixl
 * @date 2021/10/26
 */
@Service
public class JwtRoleServiceImpl implements JwtRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShiroFilterSupport shiroFilterSupport;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void add(RoleAddReqDTO reqDTO) {
        // 查询是否重复
        List<Role> roleList = roleMapper.selectList(new QueryWrapper<Role>().eq("LABEL", reqDTO.getLabel()));
        if (CollectionUtils.isNotEmpty(roleList)) {
            throw new CamAirException("角色重复啦");
        }
        // 添加角色不影响当前登录的用户设备
        Role role = new Role();
        BeanUtils.copyProperties(reqDTO , role);
        role.setId(SequenceGenerator.generateId());
        roleMapper.insert(role);
    }

    @Override
    public void delete(String roleId) {
        // 判断是否删除的是默认角色
        if ("1".equals(roleId)) {
            throw new CamAirException("不能删除默认角色");
        }
        // 删除角色
        Role role = roleMapper.queryByRoleId(roleId);
        if (Objects.isNull(role)) {
            throw new CamAirException("角色数据不存在");
        }
        roleMapper.deleteById(roleId);
        // 删除角色用户连接表
        roleResourceMapper.deleteByRoleId(roleId);
        userRoleMapper.deleteByRoleId(roleId);
        // 记录一下当前角色的更改到Redis中，后续再UserFilter中会进行过滤判断此角色下用户 都会让其重新登录
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.JWT_ROLE_FILTER + role.getLabel() , role.getLabel() , 3600 , TimeUnit.SECONDS);
        // 设置redis更新权限
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.PATH_REDIS_PREFIX , String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void enableRole(String roleId) {
        Role role = roleMapper.queryByRoleId(roleId);
        if (Objects.isNull(role)) {
            throw new CamAirException("角色数据不存在");
        }
        Role roleUpdate = new Role();
        roleUpdate.setEnableFlag("NO".equals(role.getEnableFlag()) ? "YES" : "NO");
        roleUpdate.setId(roleId);
        roleMapper.updateRole(roleUpdate);
        // 记录一下当前角色的更改到Redis中，后续再UserFilter中会进行过滤判断此角色下用户 都会让其重新登录
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.JWT_ROLE_FILTER + role.getLabel() , role.getLabel() , 3600 , TimeUnit.SECONDS);
        // 设置redis更新权限
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.PATH_REDIS_PREFIX , String.valueOf(System.currentTimeMillis()));
    }

    /**
    *  用户-角色 绑定不需要清除登录连接 和 更新过滤链路
    * @param reqDTO 1
    */
    @Override
    public void settingUser(SettingUserReqDTO reqDTO) {
        // 查询角色是否禁用了
        Role role = roleMapper.queryByRoleId(reqDTO.getRoleId());
        if (Objects.isNull(role) || "NO".equals(role.getEnableFlag())) {
            throw new CamAirException("角色数据不存在或已禁用");
        }
        // 清除之前旧的用户中间表信息
        userRoleMapper.deleteByRoleId(reqDTO.getRoleId());
        // 添加最新绑定用户
        List<UserRole> userRoleList = reqDTO.getUserIds().stream().map(p -> {
            UserRole userRole = new UserRole();
            userRole.setId(SequenceGenerator.generateId());
            userRole.setUserId(p);
            userRole.setEnableFlag("YES");
            userRole.setRoleId(reqDTO.getRoleId());
            return userRole;
        }).collect(Collectors.toList());
        userRoleMapper.batchInsert(userRoleList);
        // 记录一下当前角色的更改到Redis中，后续再UserFilter中会进行过滤判断此角色下用户 都会让其重新登录
        // 如果是新添加的用户，就不能判断是进行拦截登录，只能让用户自己手动退出重登陆
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.JWT_ROLE_FILTER + role.getLabel() , role.getLabel() , 3600 , TimeUnit.SECONDS);
    }

    @Override
    public void settingResource(SettingResourceReqDTO reqDTO) {
        // 查询角色是否禁用了
        Role role = roleMapper.queryByRoleId(reqDTO.getRoleId());
        if (Objects.isNull(role) || "NO".equals(role.getEnableFlag())) {
            throw new CamAirException("角色数据不存在或已禁用");
        }
        // 清除之前旧的中间表信息
        roleResourceMapper.deleteByRoleId(reqDTO.getRoleId());
        // 添加最新绑定权限
        List<RoleResource> roleResourceList = reqDTO.getResourceIds().stream().map(p -> {
            RoleResource roleResource = new RoleResource();
            roleResource.setId(SequenceGenerator.generateId());
            roleResource.setResourceId(p);
            roleResource.setEnableFlag("YES");
            roleResource.setRoleId(reqDTO.getRoleId());
            return roleResource;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleResourceList)) {
            roleResourceMapper.batchInsert(roleResourceList);
        }
        // 记录一下当前角色的更改到Redis中，后续再UserFilter中会进行过滤判断此角色下用户 都会让其重新登录
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.JWT_ROLE_FILTER + role.getLabel() , role.getLabel() , 3600 , TimeUnit.SECONDS);
        // 设置redis更新权限
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.PATH_REDIS_PREFIX , String.valueOf(System.currentTimeMillis()));
    }
}
