package com.example.work_program.modules.system.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.system.entity.User;
import com.example.work_program.modules.system.service.UserService;
import com.example.work_program.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @LogOperation("用户登录")
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
        log.warn("登录失败: username={}", user.getUsername());
        return Result.error(401, "用户名或密码错误");
    }

    @GetMapping("/list")
    @LoginRequired
    public Result<PageResult<User>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(userService.findAll(username, realName, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @LoginRequired
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin"})
    @LogOperation("添加用户")
    public Result<Void> add(@Valid @RequestBody User user) {
        userService.add(user);
        return Result.success("添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("更新用户")
    public Result<Void> update(@Valid @RequestBody User user) {
        userService.update(user);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin"})
    @LogOperation("删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/register")
    @LogOperation("用户注册")
    public Result<User> register(@Valid @RequestBody User user) {
        log.info("用户注册: username={}", user.getUsername());
        User registered = userService.register(user);
        log.info("注册成功: userId={}, username={}", registered.getId(), registered.getUsername());
        return Result.success("注册成功", registered);
    }
}
