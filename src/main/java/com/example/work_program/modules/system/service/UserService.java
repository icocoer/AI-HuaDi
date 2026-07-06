package com.example.work_program.modules.system.service;

import com.example.work_program.modules.system.entity.User;

import java.util.List;

public interface UserService {
    User login(String username, String password);
    User findById(Long id);
    List<User> findAll(String username, String realName);
    void add(User user);
    void update(User user);
    void deleteById(Long id);
    User register(User user);
}
