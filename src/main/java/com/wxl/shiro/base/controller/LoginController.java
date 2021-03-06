package com.wxl.shiro.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.wxl.shiro.base.api.dto.Result;
import com.wxl.shiro.base.bo.User;
import com.wxl.shiro.base.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<String> login(@RequestAttribute("userJson") String userJson) {
        String data = loginService.login(JSONObject.parseObject(userJson , User.class));
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
}
