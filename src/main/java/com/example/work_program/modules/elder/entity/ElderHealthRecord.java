package com.example.work_program.modules.elder.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ElderHealthRecord {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
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
