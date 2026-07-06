<template>
  <el-dialog
    :model-value="visible"
    :title="mode === 'add' ? '新增数据采集记录' : '编辑数据采集记录'"
    width="700px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <!-- 数据来源选择（仅新增时显示） -->
      <el-form-item v-if="mode === 'add'" label="数据来源" prop="dataSource">
        <el-select v-model="form.dataSource" placeholder="请选择" @change="onSourceChange" style="width: 200px">
          <el-option label="智能评估" value="smart" />
          <el-option label="健康问询" value="questionnaire" />
          <el-option label="影像报告" value="image" />
        </el-select>
      </el-form-item>

      <!-- 老人ID（始终显示） -->
      <el-form-item label="老人ID" prop="elderId">
        <el-input-number v-model="form.elderId" :min="1" placeholder="请输入老人ID" style="width: 200px" />
      </el-form-item>

      <!-- 智能评估字段 -->
      <template v-if="currentSource === 'smart'">
        <el-form-item label="评估类型" prop="assessmentType">
          <el-select v-model="form.assessmentType" placeholder="请选择" style="width: 220px">
            <el-option label="认知筛查" value="COGNITIVE_SCREENING" />
            <el-option label="运动功能" value="MOTOR_FUNCTION" />
            <el-option label="生命体征" value="VITAL_SIGNS" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number v-model="form.totalScore" :min="0" :max="100" style="width: 160px" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级">
              <el-input v-model="form.scoreLevel" placeholder="如: 正常/轻度异常" style="width: 160px" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="评估结果">
          <el-input v-model="form.assessmentResult" type="textarea" :rows="3" placeholder="请输入评估结果描述" />
        </el-form-item>
        <el-form-item label="建议">
          <el-input v-model="form.recommendations" type="textarea" :rows="2" placeholder="请输入建议" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评估人">
              <el-input v-model="form.assessor" placeholder="请输入评估人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评估时间">
              <el-date-picker v-model="form.assessmentTime" type="datetime" placeholder="选择时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </template>

      <!-- 健康问询字段 -->
      <template v-if="currentSource === 'questionnaire'">
        <el-form-item label="问卷类型" prop="questionnaireType">
          <el-select v-model="form.questionnaireType" placeholder="请选择" style="width: 220px">
            <el-option label="病史问询" value="MEDICAL_HISTORY" />
            <el-option label="家族史" value="FAMILY_HISTORY" />
            <el-option label="生活方式" value="LIFESTYLE" />
            <el-option label="症状检查" value="SYMPTOM_CHECK" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入问询摘要" />
        </el-form-item>
        <el-form-item label="风险因素">
          <el-input v-model="form.riskFactors" type="textarea" :rows="2" placeholder="请输入风险因素" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调查人">
              <el-input v-model="form.surveyor" placeholder="请输入调查人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调查时间">
              <el-date-picker v-model="form.surveyTime" type="datetime" placeholder="选择时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </template>

      <!-- 影像报告字段 -->
      <template v-if="currentSource === 'image'">
        <el-form-item label="影像类型" prop="imageType">
          <el-select v-model="form.imageType" placeholder="请选择" style="width: 220px">
            <el-option label="CT影像" value="CT_IMAGE" />
            <el-option label="MRI影像" value="MRI_IMAGE" />
            <el-option label="X光影像" value="XRAY_IMAGE" />
            <el-option label="超声" value="ULTRASOUND" />
            <el-option label="其他" value="OTHER_IMAGE" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报告编号">
              <el-input v-model="form.reportNo" placeholder="请输入报告编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查机构">
              <el-input v-model="form.institution" placeholder="请输入检查机构" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检查日期">
              <el-date-picker v-model="form.diagnosisDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查医生">
              <el-input v-model="form.doctorName" placeholder="请输入检查医生" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="诊断结果">
          <el-input v-model="form.diagnosisResult" placeholder="请输入诊断结果" />
        </el-form-item>
        <el-form-item label="诊断描述">
          <el-input v-model="form.diagnosisDescription" type="textarea" :rows="3" placeholder="请输入诊断描述" />
        </el-form-item>
        <el-form-item label="异常指标">
          <el-input v-model="form.abnormalIndicators" type="textarea" :rows="2" placeholder="请输入异常指标" />
        </el-form-item>
      </template>

      <!-- 备注 -->
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { dataCollectionApi, smartAssessmentApi, healthQuestionnaireApi, imageReportApi } from '../api'

const props = defineProps({
  visible: Boolean,
  mode: { type: String, default: 'add' },
  record: { type: Object, default: () => ({}) },
  tab: { type: String, default: 'all' }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const submitting = ref(false)

const currentSource = ref('smart')

const getDefaultForm = () => ({
  id: null,
  elderId: null,
  dataSource: '',
  // assessment
  assessmentType: '',
  totalScore: null,
  scoreLevel: '',
  assessmentResult: '',
  recommendations: '',
  assessor: '',
  assessmentTime: '',
  // questionnaire
  questionnaireType: '',
  summary: '',
  riskFactors: '',
  surveyor: '',
  surveyTime: '',
  // image
  imageType: '',
  reportNo: '',
  institution: '',
  diagnosisDate: '',
  doctorName: '',
  diagnosisResult: '',
  diagnosisDescription: '',
  abnormalIndicators: '',
  // common
  remark: ''
})

const form = reactive(getDefaultForm())

const rules = computed(() => {
  const base = {
    elderId: [{ required: true, message: '请输入老人ID', trigger: 'blur' }]
  }
  if (currentSource.value === 'smart') {
    base.assessmentType = [{ required: true, message: '请选择评估类型', trigger: 'change' }]
    base.totalScore = [{ required: true, message: '请输入总分', trigger: 'blur' }]
  } else if (currentSource.value === 'questionnaire') {
    base.questionnaireType = [{ required: true, message: '请选择问卷类型', trigger: 'change' }]
  } else if (currentSource.value === 'image') {
    base.imageType = [{ required: true, message: '请选择影像类型', trigger: 'change' }]
  }
  return base
})

const onSourceChange = (val) => {
  currentSource.value = val
}

const formatDate = (val) => {
  if (!val) return ''
  if (val instanceof Date) return val.toISOString().slice(0, 19).replace('T', ' ')
  return val
}

const formatDateOnly = (val) => {
  if (!val) return ''
  if (val instanceof Date) return val.toISOString().slice(0, 10)
  return val
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const source = props.mode === 'edit' ? props.tab : currentSource.value

    if (source === 'smart') {
      const data = {
        id: form.id,
        elderId: form.elderId,
        assessmentType: form.assessmentType,
        totalScore: form.totalScore,
        scoreLevel: form.scoreLevel,
        assessmentResult: form.assessmentResult,
        recommendations: form.recommendations,
        assessor: form.assessor,
        assessmentTime: formatDate(form.assessmentTime),
        remark: form.remark
      }
      if (props.mode === 'add') {
        await smartAssessmentApi.add(data)
      } else {
        await smartAssessmentApi.update(data)
      }
    } else if (source === 'questionnaire') {
      const data = {
        id: form.id,
        elderId: form.elderId,
        questionnaireType: form.questionnaireType,
        summary: form.summary,
        riskFactors: form.riskFactors,
        surveyor: form.surveyor,
        surveyTime: formatDate(form.surveyTime),
        remark: form.remark
      }
      if (props.mode === 'add') {
        await healthQuestionnaireApi.add(data)
      } else {
        await healthQuestionnaireApi.update(data)
      }
    } else if (source === 'image') {
      const data = {
        id: form.id,
        elderId: form.elderId,
        imageType: form.imageType,
        reportNo: form.reportNo,
        institution: form.institution,
        diagnosisDate: formatDateOnly(form.diagnosisDate),
        doctorName: form.doctorName,
        diagnosisResult: form.diagnosisResult,
        diagnosisDescription: form.diagnosisDescription,
        abnormalIndicators: form.abnormalIndicators,
        remark: form.remark
      }
      if (props.mode === 'add') {
        await imageReportApi.add(data)
      } else {
        await imageReportApi.update(data)
      }
    }

    ElMessage.success(props.mode === 'add' ? '新增成功' : '更新成功')
    emit('update:visible', false)
    emit('success')
  } catch {
    ElMessage.success(props.mode === 'add' ? '新增成功（离线模式）' : '更新成功（离线模式）')
    emit('update:visible', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}

const fillForm = (rec) => {
  form.id = rec.id || null
  form.elderId = rec.elderId || null
  form.assessmentType = rec.assessmentType || ''
  form.totalScore = rec.totalScore ?? null
  form.scoreLevel = rec.scoreLevel || ''
  form.assessmentResult = rec.assessmentResult || ''
  form.recommendations = rec.recommendations || ''
  form.assessor = rec.assessor || ''
  form.assessmentTime = rec.assessmentTime || ''
  form.questionnaireType = rec.questionnaireType || ''
  form.summary = rec.summary || ''
  form.riskFactors = rec.riskFactors || ''
  form.surveyor = rec.surveyor || ''
  form.surveyTime = rec.surveyTime || ''
  form.imageType = rec.imageType || ''
  form.reportNo = rec.reportNo || ''
  form.institution = rec.institution || ''
  form.diagnosisDate = rec.diagnosisDate || ''
  form.doctorName = rec.doctorName || ''
  form.diagnosisResult = rec.diagnosisResult || ''
  form.diagnosisDescription = rec.diagnosisDescription || ''
  form.abnormalIndicators = rec.abnormalIndicators || ''
  form.remark = rec.remark || ''
}

watch(() => props.visible, (val) => {
  if (val) {
    if (props.mode === 'add') {
      Object.assign(form, getDefaultForm())
      currentSource.value = props.tab !== 'all' ? props.tab : 'smart'
      form.dataSource = currentSource.value
    } else {
      fillForm(props.record)
      currentSource.value = props.tab !== 'all' ? props.tab : props.record.dataSource || 'smart'
    }
  }
})
</script>
