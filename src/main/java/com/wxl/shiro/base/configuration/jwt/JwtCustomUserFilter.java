package com.wxl.shiro.base.configuration.jwt;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.utils.RsaUtils;
import com.wxl.shiro.base.utils.jwt.JwtUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author Weixl
 * @date 2021/10/19
 *
 */
@Slf4j
public class JwtCustomUserFilter extends UserFilter {

    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        log.info("Jwt校验用户登录....");
        try {
            String token = ((HttpServletRequest) request).getHeader("token");
            // 校验Token是否有效 , 解析出Token中存储的用户信息内容
            Map<String, Claim> claims = JwtUtils.getClaims(token);
            if (Objects.isNull(claims) || claims.size() < 1) {
                return false;
            }
            String username = claims.get("username").asString();
            String password = claims.get("password").asString();
            // 使用 username和password ， 使用公钥将密码进行解密 成 MD5哈希数据库中存储的密码
            String decryptPassword = RsaUtils.decryptByPublicKey(password);
            // 使用password进行
            return JwtUtils.verifyToken(token, username, password, decryptPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
    *  没有登录过滤器响应报错
    */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.error("user not login check....");
        Subject subject = this.getSubject(request, response);
        // 返回错误码 , 这里可以做国际化处理
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        Result<Void> result = Result.error("500", "没有登录");
        httpServletResponse.setHeader("Content-Type" , "application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
        return false;
    }
}
