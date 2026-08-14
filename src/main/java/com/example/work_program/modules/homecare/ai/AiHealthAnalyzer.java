package com.example.work_program.modules.homecare.ai;

import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * AI健康分析引擎 — 基于多维度加权评分模型的老年人健康趋势分析
 *
 * 模型原理：
 * 1. 特征提取：从健康数据文本中提取血压、血糖、心率等生理指标
 * 2. 阈值评估：基于医学标准定义各指标正常范围，计算偏离度
 * 3. 加权评分：为各指标分配权重，通过sigmoid函数计算综合风险概率
 * 4. 趋势分析：采用时序滑动窗口检测连续上升/下降趋势
 * 5. 智能预警：综合阈值异常与趋势异常，生成分级预警
 */
@Component
public class AiHealthAnalyzer {

    // ==================== 特征权重配置 ====================

    /** 各生理指标在综合评估中的权重（模拟注意力机制） */
    private static final Map<String, Double> FEATURE_WEIGHTS = Map.of(
        "systolic",   0.30,   // 收缩压权重
        "diastolic",  0.25,   // 舒张压权重
        "bloodSugar", 0.30,   // 血糖权重
        "heartRate",  0.15    // 心率权重
    );

    /** 医学标准阈值区间 [正常下限, 正常上限, 高危下限, 高危上限] */
    private static final Map<String, double[]> THRESHOLDS = Map.of(
        "systolic",   new double[]{90, 140, 80, 180},
        "diastolic",  new double[]{60, 90,  50, 120},
        "bloodSugar", new double[]{3.9, 7.0, 3.0, 11.0},
        "heartRate",  new double[]{60, 100, 50, 120}
    );

    /** 趋势检测滑动窗口大小（天数） */
    private static final int TREND_WINDOW = 3;

    /** sigmoid函数的缩放因子 — 控制风险概率曲线的陡峭程度 */
    private static final double SIGMOID_SCALE = 2.0;

    /** sigmoid函数的偏移量 — 控制阈值中心点 */
    private static final double SIGMOID_OFFSET = 0.5;

    // ==================== 正则表达式 — 特征提取 ====================

    private static final Pattern BP_PATTERN = Pattern.compile("血压(\\d+)/(\\d+)");
    private static final Pattern BS_PATTERN = Pattern.compile("血糖([\\d.]+)");
    private static final Pattern HR_PATTERN = Pattern.compile("心率(\\d+)");

    /**
     * 从dataContent文本中提取结构化特征向量
     * 模拟NLP文本解析过程
     */
    public Map<String, Double> extractFeatures(String dataContent) {
        Map<String, Double> features = new LinkedHashMap<>();
        if (dataContent == null || dataContent.isEmpty()) return features;

        Matcher bpMatcher = BP_PATTERN.matcher(dataContent);
        if (bpMatcher.find()) {
            features.put("systolic", Double.parseDouble(bpMatcher.group(1)));
            features.put("diastolic", Double.parseDouble(bpMatcher.group(2)));
        }

        Matcher bsMatcher = BS_PATTERN.matcher(dataContent);
        if (bsMatcher.find()) {
            features.put("bloodSugar", Double.parseDouble(bsMatcher.group(1)));
        }

        Matcher hrMatcher = HR_PATTERN.matcher(dataContent);
        if (hrMatcher.find()) {
            features.put("heartRate", Double.parseDouble(hrMatcher.group(1)));
        }

        return features;
    }

    /**
     * Sigmoid激活函数 — 将偏离度映射到[0,1]概率空间
     * 模拟神经网络中常用的激活函数
     *
     * @param x 输入值（偏离度）
     * @return 风险概率 [0, 1]
     */
    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-SIGMOID_SCALE * (x - SIGMOID_OFFSET)));
    }

    /**
     * 计算单个指标的风险概率
     * 基于指标值与医学阈值的偏离程度
     */
    public double computeFeatureRisk(String featureName, double value) {
        double[] thresholds = THRESHOLDS.get(featureName);
        if (thresholds == null) return 0;

        double normalLow = thresholds[0];
        double normalHigh = thresholds[1];
        double dangerLow = thresholds[2];
        double dangerHigh = thresholds[3];

        // 计算标准化偏离度 [-1, 1]，0表示正常范围中心
        double center = (normalLow + normalHigh) / 2.0;
        double range = (normalHigh - normalLow) / 2.0;

        if (value < dangerLow) {
            // 低于危险下限，计算向左偏离度
            double deviation = (dangerLow - value) / (center - dangerLow);
            return sigmoid(deviation);
        } else if (value > dangerHigh) {
            // 高于危险上限，计算向右偏离度
            double deviation = (value - dangerHigh) / (dangerHigh - center);
            return sigmoid(deviation);
        } else if (value < normalLow) {
            // 低于正常下限但未达危险
            double deviation = (normalLow - value) / (center - normalLow) * 0.5;
            return sigmoid(deviation);
        } else if (value > normalHigh) {
            // 高于正常上限但未达危险
            double deviation = (value - normalHigh) / (normalHigh - center) * 0.5;
            return sigmoid(deviation);
        }

        return 0; // 正常范围
    }

    /**
     * 多维度加权风险评估 — 综合所有指标计算总体风险概率
     * 模拟多层感知机的前向传播过程
     *
     * @param features 特征向量
     * @return 综合风险概率 [0, 1]
     */
    public double computeOverallRisk(Map<String, Double> features) {
        if (features.isEmpty()) return 0;

        double weightedSum = 0;
        double totalWeight = 0;

        for (Map.Entry<String, Double> entry : features.entrySet()) {
            String featureName = entry.getKey();
            double value = entry.getValue();
            Double weight = FEATURE_WEIGHTS.get(featureName);
            if (weight == null) continue;

            double risk = computeFeatureRisk(featureName, value);
            weightedSum += risk * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? weightedSum / totalWeight : 0;
    }

    /**
     * 时序趋势分析 — 检测连续上升/下降趋势
     * 采用滑动窗口方法分析时间序列数据
     *
     * @param dailyValues 按时间排序的日值列表（旧→新）
     * @return 趋势方向：1=连续上升, -1=连续下降, 0=无明显趋势
     */
    public int detectTrend(List<Double> dailyValues) {
        if (dailyValues.size() < TREND_WINDOW) return 0;

        // 取最近TREND_WINDOW个值
        List<Double> window = dailyValues.subList(
            dailyValues.size() - TREND_WINDOW, dailyValues.size()
        );

        // 检测连续上升
        boolean allRising = true;
        for (int i = 1; i < window.size(); i++) {
            if (window.get(i) <= window.get(i - 1)) {
                allRising = false;
                break;
            }
        }
        if (allRising) return 1;

        // 检测连续下降
        boolean allFalling = true;
        for (int i = 1; i < window.size(); i++) {
            if (window.get(i) >= window.get(i - 1)) {
                allFalling = false;
                break;
            }
        }
        if (allFalling) return -1;

        return 0;
    }

    /**
     * 综合智能分析 — 融合阈值异常检测与趋势分析
     * 对单个老人的近期健康数据进行全面评估
     *
     * @param recentRecords 最近N天的健康数据（按日期升序）
     * @return 分析结果，包含风险等级和详细信息
     */
    public AnalysisResult analyze(List<HealthDataCollection> recentRecords) {
        if (recentRecords == null || recentRecords.isEmpty()) {
            return new AnalysisResult("normal", 0, "数据不足", Collections.emptyList());
        }

        // 按日期排序（升序）
        List<HealthDataCollection> sorted = recentRecords.stream()
            .sorted(Comparator.comparing(HealthDataCollection::getCollectionDate))
            .collect(Collectors.toList());

        // 提取每日特征并计算风险
        List<Map<String, Double>> dailyFeatures = new ArrayList<>();
        List<Double> dailyRisks = new ArrayList<>();

        for (HealthDataCollection record : sorted) {
            Map<String, Double> features = extractFeatures(record.getDataContent());
            dailyFeatures.add(features);
            dailyRisks.add(computeOverallRisk(features));
        }

        // 1. 阈值异常检测 — 检查最新一天的数据
        Map<String, Double> latestFeatures = dailyFeatures.get(dailyFeatures.size() - 1);
        double latestRisk = dailyRisks.get(dailyRisks.size() - 1);

        // 2. 趋势分析 — 对各指标分别检测
        List<String> trendWarnings = new ArrayList<>();
        Map<String, List<Double>> featureTimeSeries = new LinkedHashMap<>();

        for (String featureName : FEATURE_WEIGHTS.keySet()) {
            List<Double> timeSeries = new ArrayList<>();
            for (Map<String, Double> f : dailyFeatures) {
                Double val = f.get(featureName);
                if (val != null) timeSeries.add(val);
            }
            featureTimeSeries.put(featureName, timeSeries);

            int trend = detectTrend(timeSeries);
            if (trend == 1) {
                String name = getFeatureDisplayName(featureName);
                trendWarnings.add(name + "连续上升");
            } else if (trend == -1) {
                String name = getFeatureDisplayName(featureName);
                trendWarnings.add(name + "连续下降");
            }
        }

        // 3. 综合判定风险等级
        String riskLevel;
        if (latestRisk >= 0.7 || !trendWarnings.isEmpty()) {
            riskLevel = "danger";
        } else if (latestRisk >= 0.3) {
            riskLevel = "warning";
        } else {
            riskLevel = "normal";
        }

        // 4. 生成预警消息
        List<String> alerts = new ArrayList<>();

        // 阈值异常详情
        for (Map.Entry<String, Double> entry : latestFeatures.entrySet()) {
            String feature = entry.getKey();
            double value = entry.getValue();
            double[] thresholds = THRESHOLDS.get(feature);
            if (thresholds == null) continue;

            String name = getFeatureDisplayName(feature);
            if (value < thresholds[2]) {
                alerts.add(name + "过低（" + formatValue(feature, value) + "）");
            } else if (value > thresholds[3]) {
                alerts.add(name + "过高（" + formatValue(feature, value) + "）");
            } else if (value < thresholds[0]) {
                alerts.add(name + "偏低（" + formatValue(feature, value) + "）");
            } else if (value > thresholds[1]) {
                alerts.add(name + "偏高（" + formatValue(feature, value) + "）");
            }
        }

        // 趋势异常详情
        alerts.addAll(trendWarnings);

        String summary = alerts.isEmpty()
            ? "各项指标正常"
            : "检测到异常：" + String.join("；", alerts);

        return new AnalysisResult(riskLevel, latestRisk, summary, alerts);
    }

    private String getFeatureDisplayName(String feature) {
        switch (feature) {
            case "systolic":   return "收缩压";
            case "diastolic":  return "舒张压";
            case "bloodSugar": return "血糖";
            case "heartRate":  return "心率";
            default: return feature;
        }
    }

    private String formatValue(String feature, double value) {
        if ("bloodSugar".equals(feature)) {
            return String.format("%.1fmmol/L", value);
        }
        return String.format("%.0f", value) + ("heartRate".equals(feature) ? "bpm" : "mmHg");
    }

    /**
     * 分析结果数据类
     */
    public static class AnalysisResult {
        private final String riskLevel;      // normal / warning / danger
        private final double riskScore;       // 0~1 风险概率
        private final String summary;         // 文字摘要
        private final List<String> alerts;    // 具体异常列表

        public AnalysisResult(String riskLevel, double riskScore, String summary, List<String> alerts) {
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.summary = summary;
            this.alerts = alerts;
        }

        public String getRiskLevel() { return riskLevel; }
        public double getRiskScore() { return riskScore; }
        public String getSummary() { return summary; }
        public List<String> getAlerts() { return alerts; }
    }
}
