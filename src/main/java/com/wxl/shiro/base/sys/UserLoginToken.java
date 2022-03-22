package com.wxl.shiro.base.sys;


import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;


/**
 * @author Weixl
 * @date 2021/10/14
 */
@Data
public class UserLoginToken extends UsernamePasswordToken {
    public UserLoginToken(String username, String password) {
        super(username, password);
    }
}
