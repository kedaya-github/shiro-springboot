package com.wxl.shiro.base.configuration.shiro;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.configuration.ApplicationBeanUtils;
import com.wxl.shiro.base.exception.CamAirException;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Weixl
 * @date 2021/10/23
 */
@Slf4j
public class CustomWebSessionManager extends DefaultWebSessionManager {

    private final String X_AUTH_TOKEN = "x-auth-token";

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        log.info("获取SessionId");
        try {
            String token = ((HttpServletRequest) request).getHeader("token");
            if (StringUtils.isEmpty(token)) {
                // 走父类方法，从cookie中获取
//                return super.getSessionId(request, response);
                return null;
            }
            // 父类方法的request传递
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());
            ((HttpServletResponse)response).setHeader(this.X_AUTH_TOKEN, token);
            // 返回获取到的SessionId
            return token;
        }catch (Exception e) {
            log.error("获取SessionId错误" , e);
            return super.getSessionId(request, response);
        }
    }

    @Override
    protected void onChange(Session session) {
        super.onChange(session);
        // 更新用户登录记录的key的缓存时间 , 每次登录用户进行操作请求时就会调用此方法
        String sessionId = (String) session.getId();
        // 获取key
        SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (Objects.isNull(simplePrincipalCollection)) {return;}
        User user = (User) simplePrincipalCollection.getPrimaryPrincipal();
        StringRedisTemplate redisTemplate = ApplicationBeanUtils.getBean(StringRedisTemplate.class);
        String prefix = ShiroPathCheckConstant.ACCOUNT_LIMIT + user.getLoginName() + ":" + sessionId;
        Set<String> keys = redisTemplate.keys(prefix + ":*");
        // 更新时间
        keys.forEach(key -> {
            redisTemplate.opsForValue().set(key , sessionId , 3600 , TimeUnit.SECONDS);
        });
    }
}
