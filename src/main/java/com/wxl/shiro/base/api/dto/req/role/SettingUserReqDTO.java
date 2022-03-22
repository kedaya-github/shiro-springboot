package com.wxl.shiro.base.api.dto.req.role;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Weixl
 * @date 2021/10/29
 */
@Data
public class SettingUserReqDTO implements Serializable {

    @NotBlank(message = "roleId cannot be blank")
    private String roleId;

    @NotNull(message = "userIds cannot be null")
    private List<String> userIds;
}
