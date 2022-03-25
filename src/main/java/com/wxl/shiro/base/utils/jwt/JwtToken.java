package com.wxl.shiro.base.utils.jwt;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Weixl
 * @date 2022/3/23
 * 自定义的shiro接口token，可以通过这个类将string的token转型成AuthenticationToken，可供shiro使用
 *  * 注意：需要重写getPrincipal和getCredentials方法，因为是进行三件套处理的，没有特殊配置
 *  shiro无法通过这两个方法获取到用户名和密码，需要直接返回token，之后交给JwtUtil去解析获取。
 *  （当然了，可以对realm进行配置HashedCredentialsMatcher，这里就不这么处理了）
 *  //////////
 *  通过JwtUtil的getJwtToken就可以生成Jwt规范的tokenA字符串，首先要清楚这个tokenA就是需要发送给客户端进行保存的token。
 *  而在前面的区别我们说到Shiro还需要token可以进行认证，可以采用Shiro自带的token去进行认证，也可以使用我们这个tokenA进行认证（在controller中说明），
 *  这里我们对这个tokenA再利用。
 * ​
 */
@Slf4j
public class JwtToken implements AuthenticationToken {

    private String token;

    private static final long serialVersionUID = -7421603495775881906L;

    public JwtToken(String token) {
        this.token = token;
    }

    private JwtToken() {
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
