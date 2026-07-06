-- ============================================================
-- AI+老年认知衰弱分级干预与智能管理系统 - 完整初始化脚本
-- 适用于全新MySQL数据库
-- 使用方式: mysql -u root -p < src/main/resources/init-complete.sql
-- ============================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS cognitive_health DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE cognitive_health;

-- 2. 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status INT DEFAULT 1 COMMENT '状态 1=启用 0=禁用',
    role VARCHAR(20) DEFAULT 'nurse' COMMENT '角色 admin/doctor/nurse',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 3. 老人健康档案表
CREATE TABLE IF NOT EXISTS elder_health_record (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender CHAR(1) COMMENT '性别 M/F',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '居住地址',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    medical_history TEXT COMMENT '既往病史',
    family_history TEXT COMMENT '家族病史',
    cognitive_baseline TEXT COMMENT '认知基线',
    risk_level VARCHAR(20) DEFAULT 'low' COMMENT '风险等级 low/medium/high',
    status INT DEFAULT 1 COMMENT '状态',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人健康档案表';

-- 4. 认知评估记录表
CREATE TABLE IF NOT EXISTS cognitive_assessment (
    id BIGINT NOT NULL PRIMARY KEY,
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    assessment_type VARCHAR(50) COMMENT '评估类型 mmse/moca',
    total_score INT COMMENT '总分',
    risk_level VARCHAR(20) COMMENT '风险等级 low/medium/high',
    assessment_result TEXT COMMENT '评估结果',
    recommendations TEXT COMMENT '建议',
    assessor VARCHAR(50) COMMENT '评估人',
    assessment_place VARCHAR(100) COMMENT '评估地点',
    assessment_time DATETIME COMMENT '评估时间',
    next_assessment_date DATETIME COMMENT '下次评估日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_elder_id (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认知评估记录表';

-- 5. 干预计划表
CREATE TABLE IF NOT EXISTS intervention_plan (
    id BIGINT NOT NULL PRIMARY KEY,
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    plan_name VARCHAR(100) COMMENT '计划名称',
    plan_type VARCHAR(50) COMMENT '计划类型',
    risk_level VARCHAR(20) COMMENT '风险等级',
    cognitive_training TEXT COMMENT '认知训练内容',
    lifestyle_intervention TEXT COMMENT '生活方式干预',
    rehabilitation_plan TEXT COMMENT '康复计划',
    goals TEXT COMMENT '目标',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    responsible_doctor VARCHAR(50) COMMENT '负责人',
    status INT DEFAULT 1 COMMENT '状态 1=待执行 2=执行中 3=已完成',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_elder_id (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='干预计划表';

-- 6. 干预执行记录表
CREATE TABLE IF NOT EXISTS intervention_execution (
    id BIGINT NOT NULL PRIMARY KEY,
    plan_id BIGINT COMMENT '计划ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    execution_type VARCHAR(50) COMMENT '执行类型',
    content TEXT COMMENT '执行内容',
    execution_date DATE COMMENT '执行日期',
    duration INT COMMENT '时长(分钟)',
    effect_evaluation VARCHAR(50) COMMENT '效果评价',
    evaluator VARCHAR(50) COMMENT '评估人',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_id (plan_id),
    INDEX idx_elder_id (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='干预执行记录表';

-- 7. 健康数据采集表
CREATE TABLE IF NOT EXISTS health_data_collection (
    id BIGINT NOT NULL PRIMARY KEY,
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    data_source VARCHAR(50) COMMENT '数据来源',
    data_type VARCHAR(50) COMMENT '数据类型',
    report_no VARCHAR(50) COMMENT '报告编号',
    data_content TEXT COMMENT '数据内容',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    institution VARCHAR(100) COMMENT '检测机构',
    doctor_name VARCHAR(50) COMMENT '诊断医生',
    diagnosis_date DATE COMMENT '诊断日期',
    examination_items TEXT COMMENT '检查项目',
    image_thumb_url VARCHAR(500) COMMENT '影像缩略图URL',
    report_status VARCHAR(20) DEFAULT 'pending' COMMENT '报告状态',
    collection_date DATE COMMENT '采集日期',
    collector VARCHAR(50) COMMENT '采集人',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_elder_id (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据采集表';

-- 8. 影像报告详情表
CREATE TABLE IF NOT EXISTS image_report (
    id BIGINT NOT NULL PRIMARY KEY,
    collection_id BIGINT COMMENT '采集记录ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    image_type VARCHAR(50) NOT NULL COMMENT '影像类型 ct/mri/xray/ultrasound/other',
    image_url VARCHAR(500) NOT NULL COMMENT '影像URL',
    thumb_url VARCHAR(500) COMMENT '缩略图URL',
    report_no VARCHAR(50) COMMENT '报告编号',
    institution VARCHAR(100) COMMENT '检测机构',
    doctor_name VARCHAR(50) COMMENT '阅片医生',
    diagnosis_date DATE COMMENT '诊断日期',
    diagnosis_result TEXT COMMENT '诊断结果',
    diagnosis_description TEXT COMMENT '诊断描述',
    abnormal_indicators TEXT COMMENT '异常指标',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_elder_id (elder_id),
    INDEX idx_collection_id (collection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影像报告详情表';

-- 9. 智能评估记录表
CREATE TABLE IF NOT EXISTS smart_assessment (
    id BIGINT NOT NULL PRIMARY KEY,
    collection_id BIGINT COMMENT '采集记录ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    assessment_type VARCHAR(50) NOT NULL COMMENT '评估类型 cognitive/motor/vital',
    assessment_items TEXT COMMENT '评估项目(JSON格式)',
    total_score INT COMMENT '总分',
    score_level VARCHAR(20) COMMENT '得分等级 normal/mild/moderate/severe',
    assessment_result TEXT COMMENT '评估结果',
    recommendations TEXT COMMENT '建议',
    assessor VARCHAR(50) COMMENT '评估人',
    assessment_device VARCHAR(100) COMMENT '评估设备',
    assessment_time DATETIME COMMENT '评估时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_elder_id (elder_id),
    INDEX idx_collection_id (collection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能评估记录表';

-- 10. 健康问询记录表
CREATE TABLE IF NOT EXISTS health_questionnaire (
    id BIGINT NOT NULL PRIMARY KEY,
    collection_id BIGINT COMMENT '采集记录ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    questionnaire_type VARCHAR(50) NOT NULL COMMENT '问询类型 medical/family/lifestyle/symptom',
    questions TEXT NOT NULL COMMENT '问题内容(JSON格式)',
    answers TEXT NOT NULL COMMENT '回答内容(JSON格式)',
    risk_factors TEXT COMMENT '风险因素分析',
    summary TEXT COMMENT '问询摘要',
    surveyor VARCHAR(50) COMMENT '调查员',
    survey_time DATETIME COMMENT '问询时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_elder_id (elder_id),
    INDEX idx_collection_id (collection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康问询记录表';

-- 11. 风险预警表
CREATE TABLE IF NOT EXISTS risk_warning (
    id BIGINT NOT NULL PRIMARY KEY,
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    elder_name VARCHAR(50) COMMENT '老人姓名',
    risk_level VARCHAR(20) COMMENT '风险等级 low/medium/high/severe',
    warning_type VARCHAR(50) COMMENT '预警类型 risk_alert/visit_remind/abnormal',
    warning_msg TEXT COMMENT '预警消息',
    is_read INT DEFAULT 0 COMMENT '是否已读 0=未读 1=已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_elder_id (elder_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险预警表';

-- 12. 系统字典表
CREATE TABLE IF NOT EXISTS sys_dict (
    id BIGINT NOT NULL PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL COMMENT '字典类型',
    dict_key VARCHAR(100) NOT NULL COMMENT '字典键',
    dict_value VARCHAR(200) NOT NULL COMMENT '字典值',
    sort INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员账号 (admin/admin123，SHA-256加密)
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, status, role, create_time, update_time)
VALUES (1, 'admin', 'fb59cc6625a35fe603dec180b9a54f161ffea9afb7f8d05eaef860833ba2506b', '系统管理员', '13800000000', 'admin@example.com', 1, 'admin', NOW(), NOW());

-- 测试医生账号 (doctor1/123456，SHA-256加密)
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, status, role, create_time, update_time)
VALUES (2, 'doctor1', 'f9dc5cf60e578df78e91835f4f662e802ef3bc34fb5cd23ddbbc8d37807d5d32', '张医生', '13800000001', 'doctor1@example.com', 1, 'doctor', NOW(), NOW());

INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, status, role, create_time, update_time)
VALUES (3, 'nurse1', 'f9dc5cf60e578df78e91835f4f662e802ef3bc34fb5cd23ddbbc8d37807d5d32', '李护士', '13800000002', 'nurse1@example.com', 1, 'nurse', NOW(), NOW());

-- 系统字典初始数据
INSERT IGNORE INTO sys_dict (id, dict_type, dict_key, dict_value, sort) VALUES
(1, 'risk_level', 'low', '低风险', 1),
(2, 'risk_level', 'medium', '中风险', 2),
(3, 'risk_level', 'high', '高风险', 3),
(4, 'risk_level', 'severe', '极高风险', 4),
(5, 'plan_status', 'pending', '待执行', 1),
(6, 'plan_status', 'in_progress', '执行中', 2),
(7, 'plan_status', 'completed', '已完成', 3),
(8, 'plan_status', 'cancelled', '已取消', 4),
(9, 'assessment_type', 'mmse', 'MMSE量表', 1),
(10, 'assessment_type', 'moca', 'MoCA量表', 2),
(11, 'assessment_type', 'adl', 'ADL日常生活能力量表', 3),
(12, 'assessment_type', 'gds', 'GDS老年抑郁量表', 4);

-- ============================================================
-- 测试数据（可选，如不需要可删除以下内容）
-- ============================================================

-- 老人健康档案
INSERT IGNORE INTO elder_health_record (id, name, gender, birth_date, id_card, phone, address, emergency_contact, emergency_phone, medical_history, family_history, cognitive_baseline, risk_level, status, create_by, update_by, create_time, update_time) VALUES
(317569200069541888, '王建国', 'M', '1950-03-15', '310101195003150011', '13900000001', '上海市静安区南京西路1888号1栋301', '王小明', '13800001001', '高血压病史15年，2型糖尿病10年', '父亲有阿尔茨海默病史', 'MMSE基线得分26，轻度认知下降', 'medium', 1, 'admin', 'admin', NOW(), NOW()),
(317569200073736192, '李秀英', 'F', '1945-07-20', '310101194507200022', '13900000002', '上海市徐汇区漕溪北路500号2栋502', '张伟', '13800001002', '冠心病史8年，2019年行PCI术', '无家族遗传病史', 'MoCA基线得分22，中度认知下降', 'high', 1, 'admin', 'admin', NOW(), NOW()),
(317569200082124800, '张明华', 'M', '1955-11-08', '310101195511080033', '13900000003', '上海市浦东新区世纪大道1200号8栋102', '张丽', '13800001003', '体健，否认慢性病史', '无', 'MMSE基线得分29，认知功能正常', 'low', 1, 'admin', 'admin', NOW(), NOW());

-- 认知评估记录
INSERT IGNORE INTO cognitive_assessment (id, elder_id, assessment_type, total_score, risk_level, assessment_result, recommendations, assessor, assessment_place, assessment_time, next_assessment_date, remark, create_time, update_time) VALUES
(317569200132456448, 317569200069541888, 'mmse', 24, 'medium', '时间定向扣2分，计算力扣2分，回忆能力扣2分', '建议每周3次认知训练', '张医生', '社区日间照护中心', '2026-02-18 09:30:00', '2026-08-18', '第六次评估', NOW(), NOW()),
(317569200136650752, 317569200073736192, 'moca', 18, 'high', '视空间与执行功能扣3分，延迟回忆扣4分', '建议转神经内科进一步评估', '李医生', '社区卫生服务中心', '2026-02-20 14:00:00', '2026-05-20', '第四次评估', NOW(), NOW()),
(317569200145039360, 317569200082124800, 'moca', 28, 'low', '延迟回忆扣1分，语言流畅性扣1分', '保持现有生活方式', '张医生', '社区老年活动中心', '2026-02-25 10:00:00', '2026-08-25', '年度常规评估', NOW(), NOW());

-- 干预计划
INSERT IGNORE INTO intervention_plan (id, elder_id, plan_name, plan_type, risk_level, cognitive_training, lifestyle_intervention, rehabilitation_plan, goals, start_date, end_date, responsible_doctor, status, remark, create_time, update_time) VALUES
(317569200195371008, 317569200069541888, '轻度认知障碍综合干预', 'cognitive', 'medium', '数字广度训练30min/天', '每周社区太极拳3次', '每周物理治疗1次', '6个月内MMSE提升至26分以上', '2026-03-01', '2026-09-01', '张医生', 2, '高血压合并轻度认知下降', NOW(), NOW()),
(317569200203759616, 317569200073736192, '重度认知障碍干预方案', 'cognitive', 'high', '怀旧疗法每周3次', '低盐低脂冠心病饮食', '心功能康复训练每周2次', '延缓认知衰退速度', '2026-02-01', '2026-08-01', '李医生', 2, '冠心病合并认知下降', NOW(), NOW());

-- 风险预警
INSERT IGNORE INTO risk_warning (id, elder_id, elder_name, risk_level, warning_type, warning_msg, is_read, create_time) VALUES
(317569200337977344, 317569200073736192, '李秀英', 'high', 'risk_alert', '李秀英最近一次MoCA评分降至18分，认知下降加速', 0, NOW()),
(317569200358948864, 317569200069541888, '王建国', 'medium', 'risk_alert', '王建国认知评分从26分降至24分，血压持续偏高', 0, NOW());

-- 完成提示
SELECT '数据库初始化完成！' AS message;
