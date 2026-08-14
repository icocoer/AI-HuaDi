<template>
  <el-dialog
    :model-value="visible"
    :title="mode === 'add' ? '新增影像报告' : '编辑影像报告'"
    width="650px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="老人" prop="elderId">
        <el-select v-model="form.elderId" placeholder="选择老人" filterable style="width: 100%">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="影像类型" prop="imageType">
        <el-select v-model="form.imageType" placeholder="请选择" style="width: 100%">
          <el-option v-for="(label, key) in imageTypes" :key="key" :label="label" :value="key" />
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
import { imageReportApi, elderApi, systemApi } from '../api'

const props = defineProps({
  visible: Boolean,
  mode: { type: String, default: 'add' },
  record: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const elderList = ref([])
const imageTypes = ref({})

const loadImageTypes = async () => {
  try {
    const res = await systemApi.dictByType('image_type')
    const map = {}
    if (res.data) {
      res.data.forEach(item => { map[item.dictKey] = item.dictValue })
    }
    imageTypes.value = map
  } catch { }
}

const getDefaultForm = () => ({
  id: null,
  elderId: null,
  imageType: '',
  reportNo: '',
  institution: '',
  diagnosisDate: '',
  doctorName: '',
  diagnosisResult: '',
  diagnosisDescription: '',
  abnormalIndicators: '',
  remark: ''
})

const form = reactive(getDefaultForm())

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  imageType: [{ required: true, message: '请选择影像类型', trigger: 'change' }]
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
    const data = {
      ...form,
      elderId: Number(form.elderId),
      diagnosisDate: formatDateOnly(form.diagnosisDate)
    }
    if (props.mode === 'add') {
      await imageReportApi.add(data)
    } else {
      await imageReportApi.update(data)
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
  loadImageTypes()
})
</script>
