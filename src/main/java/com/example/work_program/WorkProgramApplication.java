package com.example.work_program;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.work_program.modules.*.mapper")
public class WorkProgramApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkProgramApplication.class, args);
    }

}
