package com.wxl.shiro.base.api.dto.req.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Weixl
 * @date 2021/10/26
 */
@Data
public class RoleAddReqDTO implements Serializable {

    /**
     * 角色名称
     */
    @NotBlank(message = "roleName cannot be blank")
    private String roleName;

    /**
     * 角色标识
     */
    @NotBlank(message = "label cannot be blank")
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
