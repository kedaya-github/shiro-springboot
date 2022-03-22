package com.wxl.shiro.base.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @date 2021/10/25
 *@author Weixl
 */
/**
    * 用户角色表
    */
@Data
@TableName(value = "sh_user_role")
public class UserRole {
    @TableId(type = IdType.AUTO)
    private String id;

    private String enableFlag;

    private String userId;

    private String roleId;
}