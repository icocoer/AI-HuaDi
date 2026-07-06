package com.example.work_program.modules.system.entity;

import lombok.Data;

@Data
public class SysDict {
    private Long id;
    private String dictType;
    private String dictKey;
    private String dictValue;
    private Integer sort;
    private String remark;
}
