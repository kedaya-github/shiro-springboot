package com.wxl.shiro.base.service.impl;

import com.wxl.shiro.base.api.dto.req.user.UserAddReqDTO;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.bo.UserRole;
import com.wxl.shiro.base.exception.CamAirException;
import com.wxl.shiro.base.mapper.UserMapper;
import com.wxl.shiro.base.mapper.UserRoleMapper;
import com.wxl.shiro.base.service.UserService;
import com.wxl.shiro.base.support.ShiroFilterSupport;
import com.wxl.shiro.base.utils.SequenceGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ShiroFilterSupport shiroFilterSupport;

    @Override
    public void save(UserAddReqDTO reqDTO) {
        // 判断用户名是否重复
        User user = userMapper.queryByUserName(reqDTO.getLoginName());
        if (Objects.nonNull(user)) {
            throw new CamAirException("用户名重复了");
        }
        // 加密密码
        ByteSource salt = ByteSource.Util.bytes(reqDTO.getPassWord());
        SimpleHash hash = new SimpleHash("MD5", reqDTO.getPassWord(), salt);
        // 保存信息
        User saveUser = new User();
        BeanUtils.copyProperties(reqDTO , saveUser);
        saveUser.setEnableFlag("YES");
        saveUser.setPassWord(hash.toString());
        saveUser.setSalt(salt.toString());
        saveUser.setId(SequenceGenerator.generateId());
        // 添加默认角色
        UserRole userRole = new UserRole();
        userRole.setId(SequenceGenerator.generateId());
        userRole.setUserId(saveUser.getId());
        userRole.setRoleId("1");
        userRole.setEnableFlag("YES");
        userMapper.insert(saveUser);
        userRoleMapper.insert(userRole);
    }

    @Override
    public void enableUser(String userId) {
        User user = userMapper.queryByUserId(userId);
        if (Objects.isNull(user)) {
            throw new CamAirException("用户数据不存在");
        }
        if ("NO".equals(user.getEnableFlag())) {
            throw new CamAirException("用户已禁用");
        }
        User userUpdate = new User();
        userUpdate.setEnableFlag("NO");
        userUpdate.setId(userId);
        userMapper.updateEnable(userUpdate);
        // 清除当前用户的登录连接
        shiroFilterSupport.delLoginSession(user.getLoginName());
    }

    @Override
    public void deleteUser(String userId) {
        User user = userMapper.queryByUserId(userId);
        if (Objects.isNull(user)) {
            throw new CamAirException("用户数据不存在");
        }
        // 删除数据
        userMapper.deleteUser(userId);
        // 删除角色连接表信息
        userRoleMapper.deleteByUserId(userId);
        // 清除当前用户的登录连接
        shiroFilterSupport.delLoginSession(user.getLoginName());
    }

    @Override
    public List<User> queryAll() {
        return userMapper.queryAll();
    }
}
