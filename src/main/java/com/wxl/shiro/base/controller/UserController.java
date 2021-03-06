package com.wxl.shiro.base.controller;

import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.api.dto.req.user.UserAddReqDTO;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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

    @GetMapping("/query")
    public Result<List<User>> queryAll() {
        return Result.ok(userService.queryAll());
    }
}
