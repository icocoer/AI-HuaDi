<template>
  <el-dialog
    :model-value="visible"
    :title="mode === 'add' ? '新增健康问询' : '编辑健康问询'"
    width="600px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="老人" prop="elderId">
        <el-select v-model="form.elderId" placeholder="选择老人" filterable style="width: 100%">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="问卷类型" prop="questionnaireType">
        <el-select v-model="form.questionnaireType" placeholder="请选择" style="width: 100%">
          <el-option v-for="(label, key) in questionnaireTypes" :key="key" :label="label" :value="key" />
        </el-select>
      </el-form-item>
      <!-- 体检数据字段 -->
      <template v-if="form.questionnaireType === 'BODY_CHECK'">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压(mmHg)">
              <el-input-number v-model="form.systolicPressure" :min="60" :max="250" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压(mmHg)">
              <el-input-number v-model="form.diastolicPressure" :min="40" :max="150" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="空腹血糖(mmol/L)">
              <el-input-number v-model="form.bloodSugar" :min="2" :max="30" :precision="1" :step="0.1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="心率(bpm)">
              <el-input-number v-model="form.heartRate" :min="40" :max="200" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </template>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入问询摘要" />
      </el-form-item>
      <el-form-item label="风险因素">
        <el-input v-model="form.riskFactors" type="textarea" :rows="2" placeholder="请输入风险因素" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="调查人">
            <el-select v-model="form.surveyor" placeholder="选择调查人" filterable :disabled="user.role !== 'admin'" style="width: 100%">
              <el-option v-for="u in doctorList" :key="u.id" :label="`${u.realName || u.username}`" :value="u.realName || u.username" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="调查时间">
            <el-date-picker v-model="form.surveyTime" type="datetime" placeholder="选择时间" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
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
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { healthQuestionnaireApi, elderApi, userApi, systemApi } from '../api'

const props = defineProps({
  visible: Boolean,
  mode: { type: String, default: 'add' },
  record: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:visible', 'success'])

const user = JSON.parse(localStorage.getItem('user') || '{}')
const formRef = ref(null)
const submitting = ref(false)
const elderList = ref([])
const doctorList = ref([])
const questionnaireTypes = ref({})

const loadQuestionnaireTypes = async () => {
  try {
    const res = await systemApi.dictByType('questionnaire_type')
    const map = {}
    if (res.data) {
      res.data.forEach(item => { map[item.dictKey] = item.dictValue })
    }
    questionnaireTypes.value = map
  } catch { }
}

const getDefaultForm = () => ({
  id: null,
  elderId: null,
  questionnaireType: '',
  summary: '',
  riskFactors: '',
  surveyor: user.role !== 'admin' ? (user.realName || user.username) : '',
  surveyTime: '',
  remark: '',
  systolicPressure: null,
  diastolicPressure: null,
  bloodSugar: null,
  heartRate: null
})

const form = reactive(getDefaultForm())

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  questionnaireType: [{ required: true, message: '请选择问卷类型', trigger: 'change' }]
}

const formatDate = (val) => {
  if (!val) return ''
  if (val instanceof Date) return val.toISOString().slice(0, 19).replace('T', ' ')
  return val
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = {
      ...form,
      elderId: Number(form.elderId),
      surveyTime: formatDate(form.surveyTime)
    }

    // 如果是体检数据类型，将血压血糖信息拼接到摘要中
    if (form.questionnaireType === 'BODY_CHECK') {
      const parts = []
      if (form.systolicPressure && form.diastolicPressure) {
        parts.push(`血压${form.systolicPressure}/${form.diastolicPressure}mmHg`)
      }
      if (form.bloodSugar) {
        parts.push(`空腹血糖${form.bloodSugar}mmol/L`)
      }
      if (form.heartRate) {
        parts.push(`心率${form.heartRate}bpm`)
      }
      if (parts.length > 0) {
        data.summary = parts.join('，') + (data.summary ? '，' + data.summary : '')
      }
    }

    if (props.mode === 'add') {
      await healthQuestionnaireApi.add(data)
    } else {
      await healthQuestionnaireApi.update(data)
    }
    ElMessage.success(props.mode === 'add' ? '新增成功' : '更新成功')
    emit('update:visible', false)
    emit('success')
  } catch { } finally { submitting.value = false }
}

const loadElders = async () => {
  try {
    const res = await elderApi.list({ pageNum: 1, pageSize: 1000 })
    elderList.value = res.data.list || []
  } catch { elderList.value = [] }
}

const loadDoctors = async () => {
  try {
    const res = await userApi.list({ pageNum: 1, pageSize: 1000 })
    doctorList.value = (res.data.list || []).filter(u => u.role === 'admin' || u.role === 'doctor')
  } catch { doctorList.value = [] }
}

watch(() => props.visible, (val) => {
  if (val) {
    if (props.mode === 'edit' && props.record) {
      Object.assign(form, props.record)
    } else {
      Object.assign(form, getDefaultForm())
    }
  }
})

onMounted(() => {
  loadElders()
  loadDoctors()
  loadQuestionnaireTypes()
})
</script>
