package com.wxl.shiro.base.controller.jwt;

import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.api.dto.req.role.SettingResourceReqDTO;
import com.wxl.shiro.base.api.dto.req.role.SettingUserReqDTO;
import com.wxl.shiro.base.api.dto.req.user.RoleAddReqDTO;
import com.wxl.shiro.base.service.jwt.JwtRoleService;
import com.wxl.shiro.base.service.shiro.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Weixl
 * @date 2021/10/26
 */
@RestController
@RequestMapping("/jwt/role")
@Slf4j
public class JwtRoleController {

    @Autowired
    private JwtRoleService roleService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Validated RoleAddReqDTO reqDTO) {
        roleService.add(reqDTO);
        return Result.ok();
    }

    @PostMapping("/delete/{roleId}")
    public Result<Void> delete(@PathVariable String roleId) {
        roleService.delete(roleId);
        return Result.ok();
    }

    @PostMapping("/enable/{roleId}")
    public Result<Void> enableRole(@PathVariable String roleId) {
        roleService.enableRole(roleId);
        return Result.ok();
    }

    @PostMapping("/setting/user")
    public Result<Void> settingUser(@RequestBody @Validated SettingUserReqDTO reqDTO) {
        roleService.settingUser(reqDTO);
        return Result.ok();
    }

    @PostMapping("/setting/resource")
    public Result<Void> settingResource(@RequestBody @Validated SettingResourceReqDTO reqDTO) {
        roleService.settingResource(reqDTO);
        return Result.ok();
    }
}
