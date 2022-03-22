package com.wxl.shiro.base.configuration.shiro;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Weixl
 * @date 2021/10/24
 */
@Slf4j
public class CustomLoginNumFilter extends UserFilter {

    private StringRedisTemplate redisTemplate;

    private RedisSessionDAO redisSessionDAO;

    private SessionManager sessionManager;

    public CustomLoginNumFilter(StringRedisTemplate redisTemplate, RedisSessionDAO redisSessionDAO, SessionManager sessionManager) {
        this.redisTemplate = redisTemplate;
        this.redisSessionDAO = redisSessionDAO;
        this.sessionManager = sessionManager;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 直接返回没有登录，走onAccessDenied方法
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.info("校验用户登录设备数量...");
        // 校验登录数 , 通过Request获取登录信息
        String jsonBody = ((HttpServletRequest) request).getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        // 将bodyJson传递下去
        request.setAttribute("userJson" , jsonBody);
        System.out.println(jsonBody);
        User user = JSONObject.parseObject(jsonBody , User.class);
        // Redis缓存key
        String key = ShiroPathCheckConstant.ACCOUNT_LIMIT + user.getLoginName();
        Set<String> keys = redisTemplate.keys(key + ":*");
        if (Objects.nonNull(keys) && keys.size() >= 1) {
            // 获取第一个设备的sessionId
            // 冒泡排序得出 记录时间最早的
            String lastKey = sortLastTimeKey(keys);
            if (StringUtils.isEmpty(lastKey)) {
                return true;
            }
            String lastSessionId = redisTemplate.opsForValue().get(lastKey);
            log.info("之前登录的SessionId===>{}" , lastSessionId);
            // 限制不让再次登录方法
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            Result<Void> result = Result.error("500", "已经有设备登录了账号，请检查后重试.");
            httpServletResponse.setHeader("Content-Type" , "application/json;charset=utf-8");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
            return false;

            // 将之前的登录剔除
//            Session lastSession = sessionManager.getSession(new DefaultSessionKey(lastSessionId));
//            if (Objects.nonNull(lastSession)) {
//                redisSessionDAO.delete(lastSession);
//            }
//            // 清除登录次数队列
//            Boolean flag = redisTemplate.delete(lastKey);
//            if (Boolean.FALSE.equals(flag)) {
//                log.error("删除用户登录次数限制redis失败,key===>{}" , lastKey);
//            }
        }
        return true;
    }

    /**
    *  冒泡排序得出最早时间记录的key
    * @param keys 1
    * @return java.lang.String
    */
    private static String sortLastTimeKey(Set<String> keys) {
        if (Objects.isNull(keys)) {
            return null;
        }
        Optional<String> first = keys.stream().sorted().findFirst();
        return first.isPresent() ? first.get() : null;
    }
}