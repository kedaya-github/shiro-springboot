package com.wxl.shiro.base.service;

import com.wxl.shiro.base.api.dto.req.user.UserAddReqDTO;
import com.wxl.shiro.base.bo.User;

import java.util.List;

/**
 * @author Weixl
 * @date 2021/10/25
 */
public interface UserService {
    /**
    *  添加
    * @param reqDTO 1
    */
    void save(UserAddReqDTO reqDTO);

    /**
    *  禁用用户
    * @param userId 1
    */
    void enableUser(String userId);

    /**
    *  删除用户
    * @param userId 1
    */
    void deleteUser(String userId);

    /**
    *  查询
    * @param  1
    * @return java.lang.Object
    */
    List<User> queryAll();
}
