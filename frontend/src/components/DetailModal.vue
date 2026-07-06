<template>
  <el-dialog
    :model-value="visible"
    title="数据采集详情"
    width="650px"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-descriptions :column="2" border size="default">
      <el-descriptions-item label="记录ID">{{ record.id }}</el-descriptions-item>
      <el-descriptions-item label="老人ID">{{ record.elderId }}</el-descriptions-item>

      <!-- 智能评估详情 -->
      <template v-if="isSmart">
        <el-descriptions-item label="评估类型">{{ record.assessmentType }}</el-descriptions-item>
        <el-descriptions-item label="总分">
          <el-tag :type="record.scoreLevel === '正常' ? 'success' : 'warning'">{{ record.totalScore }}分</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="等级">{{ record.scoreLevel }}</el-descriptions-item>
        <el-descriptions-item label="评估人">{{ record.assessor }}</el-descriptions-item>
        <el-descriptions-item label="评估时间" :span="2">{{ record.assessmentTime }}</el-descriptions-item>
        <el-descriptions-item label="评估结果" :span="2">{{ record.assessmentResult }}</el-descriptions-item>
        <el-descriptions-item label="建议" :span="2">{{ record.recommendations }}</el-descriptions-item>
      </template>

      <!-- 健康问询详情 -->
      <template v-if="isQuestionnaire">
        <el-descriptions-item label="问卷类型">{{ record.questionnaireType }}</el-descriptions-item>
        <el-descriptions-item label="调查人">{{ record.surveyor }}</el-descriptions-item>
        <el-descriptions-item label="调查时间" :span="2">{{ record.surveyTime }}</el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">{{ record.summary }}</el-descriptions-item>
        <el-descriptions-item label="风险因素" :span="2">{{ record.riskFactors }}</el-descriptions-item>
      </template>

      <!-- 影像报告详情 -->
      <template v-if="isImage">
        <el-descriptions-item label="影像类型">{{ record.imageType }}</el-descriptions-item>
        <el-descriptions-item label="报告编号">{{ record.reportNo }}</el-descriptions-item>
        <el-descriptions-item label="检查机构">{{ record.institution }}</el-descriptions-item>
        <el-descriptions-item label="检查医生">{{ record.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="检查日期">{{ record.diagnosisDate }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果">
          <el-tag type="warning">{{ record.diagnosisResult }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="诊断描述" :span="2">{{ record.diagnosisDescription }}</el-descriptions-item>
        <el-descriptions-item label="异常指标" :span="2">{{ record.abnormalIndicators }}</el-descriptions-item>
      </template>

      <!-- 全部记录详情 -->
      <template v-if="isAll">
        <el-descriptions-item label="数据来源">
          <el-tag :type="sourceTagType(record.dataSource)">{{ record.dataSourceDesc || record.dataSource }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="数据类型">{{ record.dataTypeDesc || record.dataType }}</el-descriptions-item>
        <el-descriptions-item label="采集日期">{{ record.collectionDate }}</el-descriptions-item>
        <el-descriptions-item label="采集人">{{ record.collector }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ record.createTime }}</el-descriptions-item>
      </template>

      <el-descriptions-item label="备注" :span="2">{{ record.remark || '无' }}</el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: Boolean,
  record: { type: Object, default: () => ({}) },
  tab: { type: String, default: 'all' }
})

defineEmits(['update:visible'])

const isSmart = computed(() => props.tab === 'smart' || props.record.assessmentType)
const isQuestionnaire = computed(() => props.tab === 'questionnaire' || props.record.questionnaireType)
const isImage = computed(() => props.tab === 'image' || props.record.imageType)
const isAll = computed(() => !isSmart.value && !isQuestionnaire.value && !isImage.value)

const sourceTagType = (source) => {
  const map = { smart: 'primary', questionnaire: 'success', image: 'warning' }
  return map[source] || 'info'
}
</script>
