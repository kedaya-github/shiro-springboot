package com.wxl.shiro.base.service.impl;

import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.service.LoginService;
import com.wxl.shiro.base.sys.UserLoginToken;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String login(User user) {
        UserLoginToken loginToken = new UserLoginToken(user.getLoginName(), user.getPassWord());
        try {
            // 设置记住我功能 - 不开启
            loginToken.setRememberMe(false);
            SecurityUtils.getSubject().login(loginToken);
            // 获取SessionId
            String sessionId = (String) SecurityUtils.getSubject().getSession().getId();
            // 记录登录次数到Redis中
            log.info("登录完成,SessionId===>{}" , sessionId);
            redisTemplate.opsForValue().set(ShiroPathCheckConstant.ACCOUNT_LIMIT + user.getLoginName() +":"+sessionId+":"+System.currentTimeMillis() , sessionId , 3600 , TimeUnit.SECONDS);
            return (String) sessionId;
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            return "passWord Error";
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return "loginError";
        }
    }
}
