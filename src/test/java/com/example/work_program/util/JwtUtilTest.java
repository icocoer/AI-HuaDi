package com.example.work_program.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-jwt-secret-key-for-unit-testing-12345678");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testParseToken() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("admin", claims.getSubject());
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("admin", claims.get("role", String.class));
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtUtil.generateToken(2L, "doctor1", "doctor");
        assertEquals("doctor1", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtUtil.generateToken(3L, "nurse1", "nurse");
        assertEquals(3L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testGetRoleFromToken() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        assertEquals("admin", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void testTokenNotExpired() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.jwt.token"));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken(null));
    }
}
