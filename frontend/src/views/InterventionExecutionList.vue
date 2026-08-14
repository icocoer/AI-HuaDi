<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>干预执行记录</h3>
      </div>

      <div class="search-bar">
        <el-select v-model="searchElderId" placeholder="选择老人" clearable filterable style="width: 200px" @clear="loadData" @change="loadData">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="planId" label="计划ID" width="180" />
        <el-table-column label="老人" width="100">
          <template #default="{ row }">{{ getDisplayName(elderNameMap, row.elderId) }}</template>
        </el-table-column>
        <el-table-column label="执行类型" width="110">
          <template #default="{ row }">{{ executionTypeLabel(row.executionType) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="执行内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="executionDate" label="执行日期" width="120" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="effectEvaluation" label="效果评价" width="100">
          <template #default="{ row }">
            <el-tag :type="row.effectEvaluation === 'good' ? 'success' : row.effectEvaluation === 'normal' ? 'warning' : 'info'" size="small">
              {{ row.effectEvaluation === 'good' ? '良好' : row.effectEvaluation === 'normal' ? '一般' : '待评价' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evaluator" label="评价人" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="老人" prop="elderId">
              <el-select v-model="form.elderId" placeholder="选择老人" filterable style="width: 100%">
                <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行类型" prop="executionType">
              <el-select v-model="form.executionType" style="width: 100%">
                <el-option label="认知训练" value="cognitive_training" />
                <el-option label="运动康复" value="exercise" />
                <el-option label="生活方式指导" value="lifestyle_guide" />
                <el-option label="随访检查" value="follow_up" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行日期" prop="executionDate"><el-input v-model="form.executionDate" placeholder="yyyy-MM-dd" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="时长(分钟)" prop="duration"><el-input v-model.number="form.duration" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="效果评价" prop="effectEvaluation">
              <el-select v-model="form.effectEvaluation" style="width: 100%">
                <el-option label="良好" value="good" /><el-option label="一般" value="normal" /><el-option label="待评价" value="pending" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="执行内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="评价人" prop="evaluator"><el-input v-model="form.evaluator" /></el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { interventionExecutionApi, elderApi, systemApi } from '../api'
import { exportToExcel } from '../utils/export'
import { buildNameMap, getDisplayName } from '../utils/nameResolver'

const tableData = ref([])
const elderList = ref([])
const elderNameMap = computed(() => buildNameMap(elderList.value))
const executionTypes = ref({})
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const searchPlanId = ref('')
const searchElderId = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  planId: '', elderId: '', executionType: 'cognitive_training', content: '',
  executionDate: '', duration: '', effectEvaluation: 'pending', evaluator: '', remark: ''
})

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  executionType: [{ required: true, message: '请选择执行类型', trigger: 'change' }],
  executionDate: [{ required: true, message: '请输入执行日期', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑执行记录' : '添加执行记录')

const parseNumber = (val) => {
  if (val === '' || val === null || val === undefined) return undefined
  const n = parseInt(val, 10)
  return isNaN(n) ? undefined : n
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    const pId = parseNumber(searchPlanId.value)
    const eId = parseNumber(searchElderId.value)
    if (pId) params.planId = pId
    if (eId) params.elderId = eId
    const res = await interventionExecutionApi.list(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }

const handleExport = () => {
  exportToExcel(tableData.value, [
    { prop: 'id', label: 'ID' },
    { prop: 'planId', label: '计划ID' },
    { prop: 'elderId', label: '老人ID' },
    { prop: 'executionType', label: '执行类型' },
    { prop: 'content', label: '执行内容' },
    { prop: 'executionDate', label: '执行日期' },
    { prop: 'duration', label: '时长(分钟)' },
    { prop: 'effectEvaluation', label: '效果评价' },
    { prop: 'evaluator', label: '评价人' }
  ], '干预执行记录')
}

const openAdd = () => { isEdit.value = false; editId.value = null; dialogVisible.value = true }

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  form.value = { ...row }; dialogVisible.value = true
}

const resetForm = () => {
  form.value = { planId: '', elderId: '', executionType: 'cognitive_training', content: '', executionDate: '', duration: '', effectEvaluation: 'pending', evaluator: '', remark: '' }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await interventionExecutionApi.update({ ...form.value, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await interventionExecutionApi.add(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该执行记录吗？', '警告', { type: 'warning' })
  try { await interventionExecutionApi.delete(row.id); ElMessage.success('删除成功'); loadData() } catch { }
}

const loadElders = async () => {
  try {
    const res = await elderApi.list({ pageNum: 1, pageSize: 1000 })
    elderList.value = res.data.list || []
  } catch { elderList.value = [] }
}

const loadExecutionTypes = async () => {
  try {
    const res = await systemApi.dictByType('execution_type')
    const map = {}
    if (res.data) {
      res.data.forEach(item => { map[item.dictKey] = item.dictValue })
    }
    executionTypes.value = map
  } catch { }
}

const executionTypeLabel = (val) => {
  return executionTypes.value[val] || val
}

onMounted(() => {
  loadData()
  loadElders()
  loadExecutionTypes()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>
