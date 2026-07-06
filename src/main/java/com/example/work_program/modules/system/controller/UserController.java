package com.example.work_program.modules.system.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.system.entity.User;
import com.example.work_program.modules.system.service.UserService;
import com.example.work_program.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        log.info("用户登录尝试: username={}", user.getUsername());
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            String token = jwtUtil.generateToken(loginUser.getId(), loginUser.getUsername(), loginUser.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", loginUser);
            log.info("登录成功: userId={}, username={}, role={}", loginUser.getId(), loginUser.getUsername(), loginUser.getRole());
            return Result.success("登录成功", data);
        }
        log.warn("登录失败: username={}, 用户名或密码错误", user.getUsername());
        return Result.error(401, "用户名或密码错误");
    }

    @GetMapping("/list")
    @LoginRequired
    public Result<List<User>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        return Result.success(userService.findAll(username, realName));
    }

    @GetMapping("/{id}")
    @LoginRequired
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin"})
    public Result<Void> add(@RequestBody User user) {
        userService.add(user);
        return Result.success("添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> update(@RequestBody User user) {
        userService.update(user);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin"})
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        log.info("用户注册: username={}", user.getUsername());
        try {
            User registered = userService.register(user);
            log.info("注册成功: userId={}, username={}", registered.getId(), registered.getUsername());
            return Result.success("注册成功", registered);
        } catch (RuntimeException e) {
            log.warn("注册失败: username={}, reason={}", user.getUsername(), e.getMessage());
            return Result.error(400, e.getMessage());
        }
    }
}
