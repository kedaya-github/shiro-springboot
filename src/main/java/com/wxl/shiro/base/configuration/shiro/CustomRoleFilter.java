package com.wxl.shiro.base.configuration.shiro;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Weixl
 * @date 2021/10/19
 */
@Slf4j
public class CustomRoleFilter extends AuthorizationFilter {

    /**
    *  校验失败方法
    */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        log.error("role check fail....");
        Subject subject = this.getSubject(request, response);
        // 返回错误码 , 这里可以做国际化处理
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        Result<Void> result = Result.error("500", "没有权限");
        httpServletResponse.setHeader("Content-Type" , "application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
        return false;
    }

    /**
    *  校验方法
    */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        log.info("判断用户权限");
        Subject subject = getSubject(request, response);
        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            //no roles specified, so nothing to check - allow access.
            return true;
        }
        for (String role : rolesArray) {
            // Shiro的默认角色过滤器 这里用的是 hasAllRoles
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
