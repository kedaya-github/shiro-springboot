package com.wxl.shiro.base.controller;

import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.api.dto.req.role.SettingResourceReqDTO;
import com.wxl.shiro.base.api.dto.req.role.SettingUserReqDTO;
import com.wxl.shiro.base.api.dto.req.user.RoleAddReqDTO;
import com.wxl.shiro.base.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Weixl
 * @date 2021/10/26
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

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
