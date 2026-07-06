package com.example.work_program.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void testSuccess() {
        Result<String> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testSuccessWithData() {
        Result<String> result = Result.success("test data");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    void testSuccessWithMessageAndData() {
        Result<Integer> result = Result.success("自定义消息", 100);
        assertEquals(200, result.getCode());
        assertEquals("自定义消息", result.getMessage());
        assertEquals(100, result.getData());
    }

    @Test
    void testError() {
        Result<Void> result = Result.error("错误信息");
        assertEquals(500, result.getCode());
        assertEquals("错误信息", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testErrorWithCode() {
        Result<Void> result = Result.error(401, "未登录");
        assertEquals(401, result.getCode());
        assertEquals("未登录", result.getMessage());
    }
}
