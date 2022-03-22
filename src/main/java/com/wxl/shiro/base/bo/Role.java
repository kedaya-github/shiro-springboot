package com.wxl.shiro.base.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @date 2021/10/14
 *@author Weixl
 */
@Data
@TableName(value = "sh_role")
public class Role {
    /**
    * 主键
    */
    private String id;

    /**
    * 角色名称
    */
    private String roleName;

    /**
    * 角色标识
    */
    private String label;

    /**
    * 角色描述
    */
    private String description;

    /**
    * 排序
    */
    private Integer sortNo;

    /**
    * 是否有效
    */
    private String enableFlag;
}