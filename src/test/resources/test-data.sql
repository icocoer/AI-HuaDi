INSERT INTO sys_user (username, password, real_name, phone, email, status, role) VALUES
('admin', '123456', '管理员', '13800000000', 'admin@test.com', 1, 'admin'),
('doctor1', '123456', '张医生', '13800000001', 'doctor1@test.com', 1, 'doctor'),
('nurse1', '123456', '李护士', '13800000002', 'nurse1@test.com', 1, 'nurse');

INSERT INTO elder_health_record (name, gender, birth_date, phone, risk_level, status) VALUES
('王建国', 'M', '1950-03-15', '13900000001', 'medium', 1),
('李秀英', 'F', '1945-07-20', '13900000002', 'high', 1),
('张明华', 'M', '1955-11-08', '13900000003', 'low', 1);

INSERT INTO cognitive_assessment (elder_id, assessment_type, total_score, risk_level, assessor, assessment_time) VALUES
(1, 'mmse', 24, 'medium', '张医生', '2026-05-20 10:00:00'),
(2, 'mmse', 18, 'high', '张医生', '2026-05-19 14:00:00'),
(3, 'moca', 28, 'low', '张医生', '2026-05-18 09:00:00');

INSERT INTO sys_dict (dict_type, dict_key, dict_value, sort) VALUES
('risk_level', 'low', '低风险', 1),
('risk_level', 'medium', '中风险', 2),
('risk_level', 'high', '高风险', 3);
