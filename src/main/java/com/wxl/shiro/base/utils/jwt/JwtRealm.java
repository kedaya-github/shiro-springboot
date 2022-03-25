package com.wxl.shiro.base.utils.jwt;

import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.mapper.ResourceMapper;
import com.wxl.shiro.base.mapper.RoleMapper;
import com.wxl.shiro.base.mapper.UserMapper;
import com.wxl.shiro.base.sys.UserLoginToken;
import com.wxl.shiro.base.utils.RsaUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @author Weixl
 * @date 2022/3/23
 * 1.  realm可以说是shiro两大功能：认证和授权的入口。可以自定义realm，对认证的方式进行自定义处理。
 * 重心先放在认证方法上，只要调用了subject.login(token)方法，就会进入到realm的doGetAuthenticationInfo内。
 *
 * 2. shiro使用的token和客户端保存的token都是jwt生成的。所以下面两种情况都会调用到subject.login方法进入到realm中：
 *  在认证时（登录controller中调用）
 *  认证通过后每次校验token正确性时（在JwtFilter中调用） 由于是无状态登录,没有在服务端Redis中存储登录信息
 *
 *  3. 问题：
 *      登录认证时，在realm中的认证方法肯定需要去查数据库。由于共用了同个realm，认证通过后每次校验token也都进入了同一个方法，这就导致每次都需要去数据库查，不太合理。
 *      但是由于Shiro不能识别身为字符串的tokenA，所以需要对其进行一下封装，也就是实现下Shiro能够识别的token接口。
 *      采用了无服务状态的方式，所以服务端是不会保存和用户有关的信息的。
 *      将登录成功后返回的token，同时保存一份到redis中，之后在JwtFilter中对token进行校验的时候，
 *      就从redis获取后判断是否相等即刻，就不用在进入realm了。（已经脱离了无状态的情况，还不如直接使用Redis存储 字符串值来对应对象）
 */
@Slf4j
public class JwtRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
    *   单点无状态 验证登录
    */
    @SneakyThrows
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        UserLoginToken userLoginToken = (UserLoginToken) auth;
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
        // 2. 进行密码的校验 , 可以使用 SimpleAuthenticationInfo 自动进行密码校验
        ByteSource salt = ByteSource.Util.bytes(password);
        String verifyPassword = new SimpleHash("MD5", password, salt).toString();
//        if (!verifyPassword.equals(user.getPassWord())) {
//            throw new UnknownAccountException("密码不正确！");
//        }
        // 3. 将用户的哈希密码进行 RSA对称加密存储到token中
        String rsaUsername = RsaUtils.encryptByPrivateKey(user.getLoginName());
        String rsaPassword = RsaUtils.encryptByPrivateKey(user.getPassWord());
        // 4. 生成JwtToken , 使用用户 哈希密码 RSA加密后的 密钥作为secret
        String jwtToken = JwtUtils.getJwtToken(rsaUsername , rsaPassword , user.getPassWord());
        return new SimpleAuthenticationInfo(jwtToken , user.getPassWord() , salt , "jwtRealm");
    }

    /**
    *  单点登录无状态授权授权 ， 由于不做Redis缓存授权信息 ,只能每次查看授权就通过查询数据库
    */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 授权方法

        return null;
    }
}
