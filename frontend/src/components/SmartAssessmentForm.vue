<template>
  <el-dialog
    :model-value="visible"
    :title="mode === 'add' ? '新增智能评估' : '编辑智能评估'"
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
      <el-form-item label="评估类型" prop="assessmentType">
        <el-select v-model="form.assessmentType" placeholder="请选择" style="width: 100%">
          <el-option v-for="(label, key) in smartTypes" :key="key" :label="label" :value="key" />
        </el-select>
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="总分" prop="totalScore">
            <el-input-number v-model="form.totalScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="等级">
            <el-select v-model="form.scoreLevel" placeholder="请选择" style="width: 100%">
              <el-option label="正常" value="正常" />
              <el-option label="轻度异常" value="轻度异常" />
              <el-option label="中度异常" value="中度异常" />
              <el-option label="重度异常" value="重度异常" />
            </el-select>
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
            <el-select v-model="form.assessor" placeholder="选择评估人" filterable :disabled="user.role !== 'admin'" style="width: 100%">
              <el-option v-for="u in doctorList" :key="u.id" :label="`${u.realName || u.username}`" :value="u.realName || u.username" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="评估时间">
            <el-date-picker v-model="form.assessmentTime" type="datetime" placeholder="选择时间" style="width: 100%" />
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
import { smartAssessmentApi, elderApi, userApi, systemApi } from '../api'

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
const smartTypes = ref({})

const loadSmartTypes = async () => {
  try {
    const res = await systemApi.dictByType('smart_type')
    const map = {}
    if (res.data) {
      res.data.forEach(item => { map[item.dictKey] = item.dictValue })
    }
    smartTypes.value = map
  } catch { }
}

const getDefaultForm = () => ({
  id: null,
  elderId: null,
  assessmentType: '',
  totalScore: null,
  scoreLevel: '',
  assessmentResult: '',
  recommendations: '',
  assessor: user.role !== 'admin' ? (user.realName || user.username) : '',
  assessmentTime: '',
  remark: ''
})

const form = reactive(getDefaultForm())

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  assessmentType: [{ required: true, message: '请选择评估类型', trigger: 'change' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }]
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
      assessmentTime: formatDate(form.assessmentTime)
    }
    if (props.mode === 'add') {
      await smartAssessmentApi.add(data)
    } else {
      await smartAssessmentApi.update(data)
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
  loadSmartTypes()
})
</script>
