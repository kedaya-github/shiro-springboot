package com.wxl.shiro.base.service.shiro.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxl.shiro.base.api.dto.req.role.SettingResourceReqDTO;
import com.wxl.shiro.base.api.dto.req.role.SettingUserReqDTO;
import com.wxl.shiro.base.api.dto.req.user.RoleAddReqDTO;
import com.wxl.shiro.base.bo.Role;
import com.wxl.shiro.base.bo.RoleResource;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.bo.UserRole;
import com.wxl.shiro.base.exception.CamAirException;
import com.wxl.shiro.base.mapper.RoleMapper;
import com.wxl.shiro.base.mapper.RoleResourceMapper;
import com.wxl.shiro.base.mapper.UserMapper;
import com.wxl.shiro.base.mapper.UserRoleMapper;
import com.wxl.shiro.base.service.shiro.RoleService;
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
import java.util.stream.Collectors;

/**
 * @author Weixl
 * @date 2021/10/26
 */
@Service
public class RoleServiceImpl implements RoleService {

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
        // 角色下用户信息
        List<User> userList = userMapper.queryByRoleId(roleId);
        // 删除角色
        Role role = roleMapper.queryByRoleId(roleId);
        if (Objects.isNull(role)) {
            throw new CamAirException("角色数据不存在");
        }
        roleMapper.deleteById(roleId);
        // 删除角色用户连接表
        roleResourceMapper.deleteByRoleId(roleId);
        userRoleMapper.deleteByRoleId(roleId);
        // 清除用户连接
        userList.forEach(p -> {
            shiroFilterSupport.delLoginSession(p.getLoginName());
        });
        // 更新动态过滤器链路 , 异步方法这里可以使用 MQ消息，或记录一个redis值通过shiro的onPreHandle更新权限
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.PATH_REDIS_PREFIX , String.valueOf(System.currentTimeMillis()));
        // 清除授权缓存
        shiroFilterSupport.delAuthorizationCache();
    }

    @Override
    public void enableRole(String roleId) {
        Role role = roleMapper.queryByRoleId(roleId);
        if (Objects.isNull(role)) {
            throw new CamAirException("角色数据不存在");
        }
        Role roleUpdate = new Role();
        roleUpdate.setId(roleId);
        roleUpdate.setEnableFlag("NO");
        roleMapper.updateRole(roleUpdate);
        // 清除用户登录连接
        List<User> userList = userMapper.queryByRoleId(roleId);
        userList.forEach(p -> {
            shiroFilterSupport.delLoginSession(p.getLoginName());
        });
        // 更新动态过滤器链路 , 异步方法这里可以使用 MQ消息，或记录一个redis值通过shiro的onPreHandle更新权限
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.PATH_REDIS_PREFIX , String.valueOf(System.currentTimeMillis()));
        // 清除授权缓存
        shiroFilterSupport.delAuthorizationCache();
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
            userRole.setEnableFlag("YES");
            userRole.setUserId(p);
            userRole.setRoleId(reqDTO.getRoleId());
            return userRole;
        }).collect(Collectors.toList());
        userRoleMapper.batchInsert(userRoleList);
        // 清除授权缓存
        shiroFilterSupport.delAuthorizationCache();
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
            roleResource.setEnableFlag("YES");
            roleResource.setResourceId(p);
            roleResource.setRoleId(reqDTO.getRoleId());
            return roleResource;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleResourceList)) {
            roleResourceMapper.batchInsert(roleResourceList);
        }
        // 设置redis更新权限
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.PATH_REDIS_PREFIX , String.valueOf(System.currentTimeMillis()));
    }
}
