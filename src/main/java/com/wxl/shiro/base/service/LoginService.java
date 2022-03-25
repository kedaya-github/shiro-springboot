package com.wxl.shiro.base.service;

import com.wxl.shiro.base.bo.User;

/**
 * @author Weixl
 * @date 2021/10/14
 */
public interface LoginService {
    /**
    *  登录
    * @param user 1
    * @return java.lang.String
    */
    String login(User user);

    /**
    *  登出
    * @param token 1
    */
    void logout(String token);
}
