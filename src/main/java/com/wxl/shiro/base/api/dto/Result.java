package com.wxl.shiro.base.api.dto;

import com.wxl.shiro.base.api.enums.SysCode;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 7242221971141381783L;
    private String status;
    private String message;
    private T data;
    private String timestamp;

    public static Result<Void> ok(){
        return init(SysCode.success.getCode(),SysCode.success.getMessage(),null);
    }

    public static <T> Result<T> ok(T data){
        return init(SysCode.success.getCode(),SysCode.success.getMessage(),data);
    }

    public static Result error(String code, String msg){
        return init(code,msg,null);
    }
    public static Result error(SysCode sysCode){
        return init(sysCode.getCode(),sysCode.getMessage(),null);
    }

    public static <T> Result<T> ok(String msg, T data){
        return init(SysCode.success.getCode(), msg, data);
    }

    public static boolean isSuccess(Result result){
        return "200".equals(result.status);
    }

    private static <T> Result<T> init(String code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setStatus(code);
        result.setMessage(msg);
        result.setData(data);
        result.setTimestamp(LocalDateTime.now().toString());
        return result;
    }
    private Result() {
    }
}