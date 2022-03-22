package com.wxl.shiro.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxl.shiro.base.bo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2021/10/14
 *@author Weixl
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
    *  查询角色信息
    */
    List<Role> queryByUserId(@Param("userId") String id);

    /**
    *  角色查询
    * @param roleId 1
    * @return com.wxl.shiro.base.bo.Role
    */
    Role queryByRoleId(String roleId);

    /**
    *  查询资源权限下的所有角色
    * @param id 1
    * @return java.util.List<com.wxl.shiro.base.bo.Role>
    */
    List<Role> queryByResourceId(@Param("resourceId") String id);

    /**
    *  禁用
    * @param roleUpdate 1
    */
    void updateRole(Role roleUpdate);
}