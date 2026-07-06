package com.example.work_program.modules.elder.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ElderHealthRecord {
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotNull(message = "出生日期不能为空")
    private LocalDate birthDate;

    private String idCard;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String medicalHistory;
    private String familyHistory;
    private String cognitiveBaseline;
    private String riskLevel;
    private Integer status;
    private String createBy;
    private String updateBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
