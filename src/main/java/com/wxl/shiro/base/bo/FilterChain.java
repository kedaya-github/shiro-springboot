package com.wxl.shiro.base.bo;

import lombok.Data;

/**
 * @date 2021/10/14
 *@author Weixl
 */
@Data
public class FilterChain {
    /**
    * 主键
    */
    private String id;

    /**
    * 描述
    */
    private String urlName;

    /**
    * 路径
    */
    private String url;

    /**
    * 拦截器名称
    */
    private String filterName;

    /**
    * 排序
    */
    private Integer sortNo;

    /**
    * 是否有效
    */
    private String enableFlag;

    private String permissions;

    private String roles;
}