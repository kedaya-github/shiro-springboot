package com.wxl.shiro.base.configuration.shiro;

import com.wxl.shiro.base.bo.Resource;
import com.wxl.shiro.base.bo.Role;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.mapper.ResourceMapper;
import com.wxl.shiro.base.mapper.RoleMapper;
import com.wxl.shiro.base.mapper.UserMapper;
import com.wxl.shiro.base.sys.UserLoginToken;
import com.wxl.shiro.base.utils.shiro.CustomSimpleByteSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 这个方法只有会在登录时调用一次
    *  认证方法
    */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UserLoginToken userLoginToken = (UserLoginToken) authenticationToken;
        // 1. 查询数据库数据是否匹配
        String username = userLoginToken.getUsername();
        char[] password = userLoginToken.getPassword();
        User user = userMapper.queryByUserName(username);
        if (Objects.isNull(user)) {
            throw new UnknownAccountException("账号不存在！");
        }
        if ("NO".equals(user.getEnableFlag())) {
            throw new UnknownAccountException("账号被禁用！");
        }
        // 2. 返回匹配方法,进行密码的校验
        ByteSource source = new CustomSimpleByteSource(password);
        return new SimpleAuthenticationInfo(user , user.getPassWord() , source , "userRealm");
    }

    /**
     * 这个方法在每次过滤器进行hasRole，HasPerm 等方法时 都会调用。
     *  授权方法 ， 一般只存储角色信息即可
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("realm authorization check start....");
        // 获取登录主体信息
        User user = (User)principalCollection.getPrimaryPrincipal();
        if (Objects.isNull(user)) {
            throw new RuntimeException("数据不存在！");
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 查询角色信息
        List<Role> roleList = roleMapper.queryByUserId(user.getId());
        List<String> roleIdList = roleList.stream().map(Role::getId).collect(Collectors.toList());
        Set<String> LabelList = roleList.stream().map(Role::getLabel).collect(Collectors.toSet());
        simpleAuthorizationInfo.setRoles(LabelList);
        // 查询资源信息
        List<Resource> resourceList = resourceMapper.queryByRoleList(roleIdList);
        Set<String> permList = resourceList.stream().map(Resource::getLabel).collect(Collectors.toSet());
        simpleAuthorizationInfo.setStringPermissions(permList);
        return simpleAuthorizationInfo;
    }
}
