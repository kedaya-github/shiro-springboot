package com.wxl.shiro.base.bo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @date 2021/10/14
 *@author Weixl
 */
@Data
@TableName(value = "sh_user")
public class User implements Serializable {
    private static final long serialVersionUID = -8422749356997914484L;

    public User(String loginName, String passWord) {
        this.loginName = loginName;
        this.passWord = passWord;
    }

    public User() {
    }

    /**
    * 主键
    */
    @TableId
    private String id;

    /**
    * 登录名称
    */
    private String loginName;

    /**
    * 真实姓名
    */
    private String realName;

    /**
    * 昵称
    */
    private String nickName;

    /**
    * 密码
    */
    private String passWord;

    /**
    * 加密因子
    */
    private String salt;

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

    /**
    * 职务
    */
    private String duties;

    /**
    * 排序
    */
    private Integer sortNo;

    /**
    * 是否有效
    */
    private String enableFlag;
}