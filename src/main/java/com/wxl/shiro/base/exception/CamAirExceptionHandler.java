package com.wxl.shiro.base.exception;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@ControllerAdvice
@Slf4j
public class CamAirExceptionHandler{

    @ExceptionHandler(value = Exception.class)
    public void exceptionHandler(HttpServletRequest request, Exception e, HttpServletResponse response) {
        e.printStackTrace();
        response.setContentType("application/json;charset=utf-8");
        try {
            Result result;
            if (e instanceof CamAirException) {
                result = Result.error("500" , e.getMessage());
            } else {
                result = Result.error("500" , "服务器开小差了~");
            }
            PrintWriter writer = response.getWriter();
            writer.write(JSONObject.toJSONString(result));
            writer.flush();
            writer.close();
        }catch (Exception e2) {
            log.error("异常返回出错" , e2);
        }
    }
}
