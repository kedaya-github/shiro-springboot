package com.wxl.shiro.base.bo;

import java.util.Date;
import lombok.Data;

/**
 * @date 2021/10/14
 *@author Weixl
 */
@Data
public class Company {
    private String id;

    /**
    * 是否有效
    */
    private String enableFlag;

    /**
    * 企业名称
    */
    private String companyName;

    /**
    * 公司地址
    */
    private String address;

    /**
    * 企业编码
    */
    private String companyNo;

    /**
    * 法人
    */
    private String boss;

    /**
    * 注册资金
    */
    private String registeredFund;

    /**
    * 注册时间
    */
    private Date registeredTime;

    /**
    * 在保人数
    */
    private Integer insuranceNumber;

    /**
    * 状态 0：正常 1：拉黑
    */
    private String state;
}