package com.wxl.shiro.base.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@RestController
@RequestMapping("/manager")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        String data = loginService.login(user);
        return Result.ok(data);
    }

    @GetMapping("/test")
    public Result<String> test() {
        return Result.ok("success");
    }

    @GetMapping("/redisDel/{id}")
    public Result<String> redisDel(@PathVariable String id) {
        Boolean delete = redisTemplate.delete(id);
        System.out.println(delete);
        return Result.ok("success");
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServerRequest request) {
        String token = request.getHeader("token");
        loginService.logout(token);
        return Result.ok();
    }
}
