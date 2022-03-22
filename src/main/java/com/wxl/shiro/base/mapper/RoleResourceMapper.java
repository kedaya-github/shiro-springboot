package com.wxl.shiro.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxl.shiro.base.bo.RoleResource;

import java.util.List;

/**
 * @date 2021/10/26
 *@author Weixl
 */
public interface RoleResourceMapper extends BaseMapper<RoleResource> {
    /**
    *  根据角色id删除
    * @param roleId 1
    */
    void deleteByRoleId(String roleId);

    /**
    *  批量添加
    * @param roleResourceList 1
    */
    void batchInsert(List<RoleResource> roleResourceList);
}