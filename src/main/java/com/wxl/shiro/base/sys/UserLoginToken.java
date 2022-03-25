package com.wxl.shiro.base.sys;


import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;


/**
 * @author Weixl
 * @date 2021/10/14
 *  用作两个登录鉴权方式中：
 *  1. 有状态登录 将登录信息缓存在Redis中，shiro使用RedisCacheManager来管理session
 *  2. 无状态登录 将UsernamePasswordToken用作传值 用在JwtRealm中进行校验用户，生成JwtToken返回给前端. 服务端不记录用户的任何信息
 */
@Data
public class UserLoginToken extends UsernamePasswordToken {
    public UserLoginToken(String username, String password) {
        super(username, password);
    }
}
