package com.example.work_program.modules.system.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.system.entity.User;

public interface UserService {
    User login(String username, String password);
    User findById(Long id);
    PageResult<User> findAll(String username, String realName, int pageNum, int pageSize);
    void add(User user);
    void update(User user);
    void deleteById(Long id);
    User register(User user);
}
