package com.wxl.shiro.base.configuration.jwt;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Weixl
 * @date 2022/3/25
 */
@Slf4j
public class JwtCustomRoleFilter extends AuthorizationFilter {

    /**
    *  JWT单点登录无状态 校验方法
    * @param request 1
     * @param response 2
     * @param mappedValue 3
    * @return boolean
    */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
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
        Result<Void> result = Result.error("500", "没有权限");
        httpServletResponse.setHeader("Content-Type" , "application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
        return false;
    }
}
