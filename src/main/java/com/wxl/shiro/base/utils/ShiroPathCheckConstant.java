package com.wxl.shiro.base.utils;

/**
 * @author Weixl
 * @date 2021/10/26
 */
public class ShiroPathCheckConstant {

    /**
    *  更新Shiro过滤权限路由配置
    */
    public static final String PATH_REDIS_PREFIX = "update_path_check";

    /**
     *  用户权限信息缓存key
     */
    public static final String AUTHORIZATION_CACHE_PREFIX = "authorizationCache";

    /**
     *  用户限制登录key
     */
    public static final String ACCOUNT_LIMIT = "account_limit:";

    /**
    *  JWT无状态 判断鉴权方式
    */
    public static final String JWT_ROLE_FILTER = "role:update:";

    /**
     *  JWT无状态 判断某用户是否可用
     */
    public static final String JWT_USER_FILTER = "user:update:";
}
