package com.wxl.shiro.base.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色资源表
 * @date 2021/10/26
 *@author Weixl
 */
@Data
@TableName(value = "sh_role_resource")
public class RoleResource implements Serializable {
    @TableId(type = IdType.AUTO)
    private String id;

    private String enableFlag;

    private String roleId;

    private String resourceId;
}