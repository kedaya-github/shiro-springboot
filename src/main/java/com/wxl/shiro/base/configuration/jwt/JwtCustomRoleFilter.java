package com.wxl.shiro.base.configuration.jwt;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import com.wxl.shiro.base.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Weixl
 * @date 2022/3/25
 */
@Slf4j
public class JwtCustomRoleFilter extends AuthorizationFilter {

    private RedisTemplate redisTemplate;

    public JwtCustomRoleFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
    *  JWT单点登录无状态 校验方法
    * @param request 1
     * @param response 2
     * @param mappedValue 3
    * @return boolean
    */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        log.info("进行用户角色权限的校验...");
        // 由于是JWT的无状态登录，那么在Subject中是没有session对象，通过之前将RoleList信息储存在JwtToken中来获取进行判断
        String token = ((HttpServletRequest) request).getHeader("token");
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        // 不需要鉴权
        String[] rolesArray = (String[]) mappedValue;
        if (rolesArray == null || rolesArray.length == 0) {
            //no roles specified, so nothing to check - allow access.
            return true;
        }
        // 获取Token中的 RoleList信息
        Map<String, Claim> claims = JwtUtils.getClaims(token);
        if (Objects.isNull(claims)) {
            return false;
        }
        // 判断Redis中是否有修改 Role角色权限的动作,
        redisTemplate.opsForValue().get(ShiroPathCheckConstant.JWT_ROLE_FILTER);
        // 如果有就则通过 JwtRealm中的方法 用数据库来查询最后的角色Role进行判断
        String roleLabelString = claims.get("role").asString();
        if (StringUtils.isEmpty(roleLabelString)) {
            return false;
        }
        List<String> roleLabelList = JSONObject.parseArray(roleLabelString, String.class);
        for (String role : rolesArray) {
            // Shiro的默认角色过滤器 这里用的是 hasAllRoles
            if (roleLabelList.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
    *  JWT 权限校验失败处理
    * @param request 1
     * @param response 2
    * @return boolean
    */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        log.error("role check fail....");
        // 返回错误码 , 这里可以做国际化处理
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        Result result = Result.error("500", "没有权限");
        httpServletResponse.setHeader("Content-Type" , "application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
        return false;
    }
}
