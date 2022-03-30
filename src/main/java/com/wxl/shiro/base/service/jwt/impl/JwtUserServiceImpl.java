package com.wxl.shiro.base.service.jwt.impl;

import com.wxl.shiro.base.api.dto.req.user.UserAddReqDTO;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.bo.UserRole;
import com.wxl.shiro.base.exception.CamAirException;
import com.wxl.shiro.base.mapper.UserMapper;
import com.wxl.shiro.base.mapper.UserRoleMapper;
import com.wxl.shiro.base.service.jwt.JwtUserService;
import com.wxl.shiro.base.support.JwtShiroFilterSupport;
import com.wxl.shiro.base.utils.SequenceGenerator;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@Service
public class JwtUserServiceImpl implements JwtUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private JwtShiroFilterSupport shiroFilterSupport;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(UserAddReqDTO reqDTO) {
        // 判断用户名是否重复
        User user = userMapper.queryByUserName(reqDTO.getLoginName());
        if (Objects.nonNull(user)) {
            throw new CamAirException("Jwt用户名重复了");
        }
        // 加密密码
        ByteSource salt = ByteSource.Util.bytes(reqDTO.getPassWord());
        SimpleHash hash = new SimpleHash("MD5", reqDTO.getPassWord(), salt);
        // 保存信息
        User saveUser = new User();
        BeanUtils.copyProperties(reqDTO , saveUser);
        saveUser.setPassWord(hash.toString());
        saveUser.setEnableFlag("YES");
        saveUser.setSalt(salt.toString());
        saveUser.setId(SequenceGenerator.generateId());
        // 添加默认角色
        UserRole userRole = new UserRole();
        userRole.setId(SequenceGenerator.generateId());
        userRole.setRoleId("1");
        userRole.setUserId(saveUser.getId());
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
//        if ("NO".equals(user.getEnableFlag())) {
//            throw new CamAirException("用户已禁用");
//        }
        User userUpdate = new User();
        userUpdate.setId(userId);
        userUpdate.setEnableFlag("NO".equals(user.getEnableFlag()) ? "YES" : "NO");
        userMapper.updateEnable(userUpdate);
        // 在redis中记录当前用户的黑名单一小时
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.JWT_USER_FILTER + user.getLoginName() , user.getLoginName() , 3600 , TimeUnit.SECONDS);
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
        // 在redis中记录当前用户的黑名单一小时
        redisTemplate.opsForValue().set(ShiroPathCheckConstant.JWT_USER_FILTER + user.getLoginName() , user.getLoginName() , 3600 , TimeUnit.SECONDS);
    }

    @Override
    public List<User> queryAll() {
        return userMapper.queryAll();
    }
}
