package com.example.work_program.modules.system.service.impl;

import com.example.work_program.common.BusinessException;
import com.example.work_program.common.PageResult;
import com.example.work_program.modules.system.entity.User;
import com.example.work_program.modules.system.mapper.UserMapper;
import com.example.work_program.modules.system.service.UserService;
import com.example.work_program.util.JwtUtil;
import com.example.work_program.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            return null;
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public User findById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public PageResult<User> findAll(String username, String realName, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = userMapper.count(username, realName);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<User> users = userMapper.findAll(username, realName, offset, pageSize);
        users.forEach(u -> u.setPassword(null));
        return new PageResult<>(users, total, pageNum, pageSize);
    }

    @Override
    public void add(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        User existing = userMapper.findById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        userMapper.update(user);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public User register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        user.setRole("nurse");
        user.setStatus(1);
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }
}
