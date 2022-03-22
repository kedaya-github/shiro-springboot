package com.wxl.shiro.base.configuration.shiro;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Weixl
 * @date 2021/10/19
 */
@Slf4j
public class CustomUserFilter extends UserFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        log.info("校验用户登录....");
        return super.isAccessAllowed(request, response, mappedValue);
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
