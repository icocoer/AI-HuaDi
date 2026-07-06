package com.example.work_program.modules.datacollection.enums;

public enum DataSourceEnum {
    SMART("smart", "智能评估"),
    QUESTIONNAIRE("questionnaire", "健康问询"),
    IMAGE("image", "影像报告");

    private final String code;
    private final String description;

    DataSourceEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DataSourceEnum fromCode(String code) {
        for (DataSourceEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
