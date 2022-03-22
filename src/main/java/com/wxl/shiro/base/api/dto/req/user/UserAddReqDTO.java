package com.wxl.shiro.base.api.dto.req.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@Data
public class UserAddReqDTO implements Serializable {
    private static final long serialVersionUID = -1186837838483506136L;
    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 邮箱
     */
    private String zipcode;

    /**
     * 地址
     */
    private String address;

    /**
     * 固定电话
     */
    private String tel;

    /**
     * 电话
     */
    private String mobil;

    /**
     * 邮箱
     */
    private String email;
}