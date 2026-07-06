package com.example.work_program.config;

import com.example.work_program.util.SnowflakeIdGenerator;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * MyBatis 拦截器：INSERT 时自动为实体设置雪花算法 ID
 */
@Component
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class SnowflakeIdInterceptor implements Interceptor {

    private final SnowflakeIdGenerator idGenerator;

    public SnowflakeIdInterceptor(SnowflakeIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        if (ms.getSqlCommandType() == SqlCommandType.INSERT && parameter != null) {
            setSnowflakeId(parameter);
        }

        return invocation.proceed();
    }

    private void setSnowflakeId(Object obj) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            if (idField.get(obj) == null) {
                idField.set(obj, idGenerator.nextId());
            }
        } catch (NoSuchFieldException e) {
            // 无 id 字段，跳过
        } catch (IllegalAccessException e) {
            throw new RuntimeException("设置雪花 ID 失败", e);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
