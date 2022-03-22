package com.wxl.shiro.base.exception;

import lombok.Data;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@Data
public class CamAirException extends RuntimeException {
    private static final long serialVersionUID = -9090643032999658226L;

    public String message;

    public CamAirException(String message) {
        this.message = message;
    }
}
