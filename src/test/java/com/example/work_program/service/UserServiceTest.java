package com.example.work_program.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.system.entity.User;
import com.example.work_program.modules.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testLoginSuccess() {
        User user = userService.login("admin", "123456");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getRole());
        assertNull(user.getPassword());
    }

    @Test
    void testLoginFailWrongPassword() {
        User user = userService.login("admin", "wrongpass");
        assertNull(user);
    }

    @Test
    void testLoginFailNonExistUser() {
        User user = userService.login("nobody", "123456");
        assertNull(user);
    }

    @Test
    void testFindById() {
        User user = userService.findById(1L);
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testFindAll() {
        PageResult<User> page = userService.findAll(null, null, 1, 100);
        assertFalse(page.getList().isEmpty());
        assertTrue(page.getList().size() >= 3);
    }

    @Test
    void testFindAllByUsername() {
        PageResult<User> page = userService.findAll("admin", null, 1, 100);
        assertEquals(1, page.getList().size());
        assertEquals("admin", page.getList().get(0).getUsername());
    }

    @Test
    void testRegisterSuccess() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("123456");
        newUser.setRealName("新用户");
        newUser.setPhone("13900000000");

        User registered = userService.register(newUser);
        assertNotNull(registered.getId());
        assertEquals("newuser", registered.getUsername());
        assertEquals("nurse", registered.getRole());
        assertEquals(1, registered.getStatus());
        assertNull(registered.getPassword());
    }

    @Test
    void testRegisterDuplicateUsername() {
        User dupUser = new User();
        dupUser.setUsername("admin");
        dupUser.setPassword("123456");

        assertThrows(RuntimeException.class, () -> userService.register(dupUser));
    }
}
