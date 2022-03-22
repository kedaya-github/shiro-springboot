package com.wxl.shiro.base.service;

import com.wxl.shiro.base.api.dto.req.role.SettingResourceReqDTO;
import com.wxl.shiro.base.api.dto.req.role.SettingUserReqDTO;
import com.wxl.shiro.base.api.dto.req.user.RoleAddReqDTO;

/**
 * @author Weixl
 * @date 2021/10/26
 */
public interface RoleService {
    /**
    *  添加
    * @param reqDTO 1
    */
    void add(RoleAddReqDTO reqDTO);

    /**
    *  删除
    * @param roleId 1
    */
    void delete(String roleId);

    /**
    *  禁用
    * @param roleId 1
    */
    void enableRole(String roleId);

    /**
    *  添加角色用户
    * @param reqDTO 1
    */
    void settingUser(SettingUserReqDTO reqDTO);

    /**
    *  添加角色权限
    * @param reqDTO 1
    */
    void settingResource(SettingResourceReqDTO reqDTO);
}
