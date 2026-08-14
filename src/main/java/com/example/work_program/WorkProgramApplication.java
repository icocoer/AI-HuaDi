package com.example.work_program;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.work_program.modules.*.mapper")
@EnableScheduling
public class WorkProgramApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkProgramApplication.class, args);
    }

}
