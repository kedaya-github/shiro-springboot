package com.wxl.shiro.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxl.shiro.base.bo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2021/10/14
 *@author Weixl
 */
public interface UserMapper extends BaseMapper<User> {
    /**
    *  用户名查找
    * @param username 1
    * @return com.wxl.shiro.base.bo.User
    */
    User queryByUserName(@Param("userName") String username);

    /**
    *  id查询
    * @param userId 1
    * @return com.wxl.shiro.base.bo.User
    */
    User queryByUserId(String userId);

    /**
    *  用户禁用
    * @param user 1
    */
    void updateEnable(@Param("user") User user);

    /**
    *  删除用户
    * @param userId 1
    */
    void deleteUser(@Param("userId") String userId);

    /**
    *  角色下用户集合
    * @param roleId 1
    * @return List<User>
    */
    List<User> queryByRoleId(String roleId);

    /**
    *  查询所有
    * @param  1
    * @return List<User>
    */
    List<User> queryAll();
}