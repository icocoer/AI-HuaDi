package com.example.work_program.modules.datacollection.enums;

public enum DataTypeEnum {
    // 智能评估相关
    COGNITIVE_SCREENING("cognitive_screening", "认知筛查"),
    MOTOR_FUNCTION("motor_function", "运动功能"),
    VITAL_SIGNS("vital_signs", "生命体征"),

    // 健康问询相关
    MEDICAL_HISTORY("medical_history", "既往病史"),
    FAMILY_HISTORY("family_history", "家族病史"),
    LIFESTYLE("lifestyle", "生活方式"),
    SYMPTOM_CHECK("symptom_check", "症状自查"),

    // 影像报告相关
    CT_IMAGE("ct_image", "CT影像"),
    MRI_IMAGE("mri_image", "MRI影像"),
    XRAY_IMAGE("xray_image", "X光片"),
    ULTRASOUND("ultrasound", "超声检查"),
    OTHER_IMAGE("other_image", "其他影像");

    private final String code;
    private final String description;

    DataTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DataTypeEnum fromCode(String code) {
        for (DataTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
