package com.wxl.shiro.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxl.shiro.base.bo.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2021/10/25
 *@author Weixl
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
    *  删除根据用户
    * @param userId 1
    */
    void deleteByUserId(@Param("userId") String userId);

    /**
    *  根据roleId删除
    * @param roleId 1
    */
    void deleteByRoleId(String roleId);

    /**
    *  批量添加绑定用户
    * @param userRoleList 1
    */
    void batchInsert(List<UserRole> userRoleList);
}