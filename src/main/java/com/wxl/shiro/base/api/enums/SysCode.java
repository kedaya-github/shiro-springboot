package com.wxl.shiro.base.api.enums;

/**
 * @author Weixl
 * @date 2021/10/14
 */
public enum SysCode implements ReturnCode {
    success("200","success"),

    SYSTEM_ERROE("9999","system error"),
    ENUM_ERROR("9998","enum typeHandler error"),
    DATABASE_DELETE_ERROR("9997","database delete result no 1"),
    DATABASE_INSERT_ERROR("9996","database insert result no 1"),
    DATABASE_UPDATE_ERROR("9995","database update result no 1"),
    REDIS_EXPIRED_TIME_ERROR("9994","redis expired time error"),
    TRANSACTION_EXCEPTION("9993","TransactionException"),
    HTTP_CLINT_ERROR("9992","http clint error"),
    SYS_TOKEN_EXPIRE("9991","Invalid token"),// token失效
    SYS_URL_UNAUTH("9990","unauthorized"),// 权限不足

    PARAMETER_ERROR("9000","Parameter validation error"),
    SYS_10000("10000","Upcoming. Please contact 0968525555"),
    ;


    private String code;

    private String message;

    SysCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
