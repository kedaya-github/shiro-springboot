package com.wxl.shiro.base.controller.jwt;

import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.api.dto.req.user.UserAddReqDTO;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.service.jwt.JwtUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@RestController
@RequestMapping("/jwt/user")
@Slf4j
public class JwtUserController {

    @Autowired
    private JwtUserService userService;

    @PostMapping("/add")
    public Result<Void> save(@RequestBody UserAddReqDTO reqDTO) {
        userService.save(reqDTO);
        return Result.ok();
    }

    @PostMapping("/enableUser/{userId}")
    public Result<Void> enableUser(@PathVariable String userId) {
        userService.enableUser(userId);
        return Result.ok();
    }

    @PostMapping("/delete/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.ok();
    }

    @PostMapping("/query")
    public Result<List<User>> queryAll() {
        log.info("登录信息===>{}" , SecurityUtils.getSubject().getPrincipal());
        return Result.ok(userService.queryAll());
    }
}
