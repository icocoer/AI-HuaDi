<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>干预计划管理</h3>
        <el-button type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon>添加计划
        </el-button>
      </div>

      <div class="search-bar">
        <el-select v-model="searchElderId" placeholder="选择老人" clearable filterable style="width: 200px" @clear="loadData" @change="loadData">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="待执行" value="pending" /><el-option label="执行中" value="in_progress" /><el-option label="已完成" value="completed" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px" @row-click="openDetail">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column label="老人" width="100">
          <template #default="{ row }">{{ getDisplayName(elderNameMap, row.elderId) }}</template>
        </el-table-column>
        <el-table-column prop="planName" label="计划名称" width="160" show-overflow-tooltip />
        <el-table-column label="计划类型" width="100">
          <template #default="{ row }">{{ planTypeLabel(row.planType) }}</template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === 'high' ? 'danger' : row.riskLevel === 'medium' ? 'warning' : 'success'" size="small">
              {{ row.riskLevel === 'high' ? '高' : row.riskLevel === 'medium' ? '中' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="responsibleDoctor" label="责任医生" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : row.status === 'in_progress' ? 'warning' : 'info'" size="small">
              {{ row.status === 'completed' ? '已完成' : row.status === 'in_progress' ? '执行中' : '待执行' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click.stop="openExecute(row)" :disabled="row.status === 'completed'">执行</el-button>
            <el-button size="small" type="primary" @click.stop="handleComplete(row)" :disabled="row.status === 'completed'">完成</el-button>
            <el-button size="small" @click.stop="openEdit(row)" :disabled="row.status === 'completed'">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="650px" @close="resetForm">
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
            <el-form-item label="计划名称" prop="planName"><el-input v-model="form.planName" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计划类型" prop="planType">
              <el-select v-model="form.planType" style="width: 100%">
                <el-option label="认知训练" value="cognitive" /><el-option label="生活方式" value="lifestyle" /><el-option label="康复计划" value="rehabilitation" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-select v-model="form.riskLevel" style="width: 100%">
                <el-option label="低风险" value="low" /><el-option label="中风险" value="medium" /><el-option label="高风险" value="high" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate"><el-input v-model="form.startDate" placeholder="yyyy-MM-dd" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate"><el-input v-model="form.endDate" placeholder="yyyy-MM-dd" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="责任医生" prop="responsibleDoctor">
              <el-select v-model="form.responsibleDoctor" placeholder="选择医生" filterable :disabled="user.role !== 'admin'" style="width: 100%">
                <el-option v-for="u in doctorList" :key="u.id" :label="`${u.realName || u.username}（${u.role}）`" :value="u.realName || u.username" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status" v-if="isEdit">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="待执行" value="pending" /><el-option label="执行中" value="in_progress" /><el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="认知训练" prop="cognitiveTraining">
          <el-input v-model="form.cognitiveTraining" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="生活方式干预" prop="lifestyleIntervention">
          <el-input v-model="form.lifestyleIntervention" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="康复计划" prop="rehabilitationPlan">
          <el-input v-model="form.rehabilitationPlan" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="目标" prop="goals">
          <el-input v-model="form.goals" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 执行记录弹窗 -->
    <el-dialog title="添加执行记录" v-model="execDialogVisible" width="600px" @close="resetExecForm">
      <el-form :model="execForm" :rules="execRules" ref="execFormRef" label-width="100px">
        <el-form-item label="老人">
          <el-input :value="getDisplayName(elderNameMap, execForm.elderId)" disabled />
        </el-form-item>
        <el-form-item label="关联计划">
          <el-input :value="execForm.planName" disabled />
        </el-form-item>
        <el-form-item label="执行类型" prop="executionType">
          <el-select v-model="execForm.executionType" style="width: 100%">
            <el-option label="认知训练" value="cognitive_training" />
            <el-option label="运动康复" value="exercise" />
            <el-option label="生活方式" value="lifestyle" />
            <el-option label="功能康复" value="rehabilitation" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行内容" prop="content">
          <el-input v-model="execForm.content" type="textarea" :rows="3" placeholder="请输入执行内容" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="执行日期" prop="executionDate">
              <el-date-picker v-model="execForm.executionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(分钟)">
              <el-input-number v-model="execForm.duration" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="效果评价">
              <el-select v-model="execForm.effectEvaluation" style="width: 100%">
                <el-option label="良好" value="good" /><el-option label="一般" value="moderate" /><el-option label="待评价" value="pending" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评价人">
              <el-select v-model="execForm.evaluator" placeholder="选择评价人" filterable :disabled="user.role !== 'admin'" style="width: 100%">
                <el-option v-for="u in doctorList" :key="u.id" :label="`${u.realName || u.username}`" :value="u.realName || u.username" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="execForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="execDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExecSubmit" :loading="execSubmitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="计划详情" v-model="detailVisible" width="650px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="计划ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="老人">{{ getDisplayName(elderNameMap, detailData.elderId) }}</el-descriptions-item>
        <el-descriptions-item label="计划名称" :span="2">{{ detailData.planName }}</el-descriptions-item>
        <el-descriptions-item label="计划类型">{{ planTypeLabel(detailData.planType) }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">
          <el-tag :type="detailData.riskLevel === 'high' ? 'danger' : detailData.riskLevel === 'medium' ? 'warning' : 'success'" size="small">
            {{ detailData.riskLevel === 'high' ? '高' : detailData.riskLevel === 'medium' ? '中' : '低' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detailData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detailData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="责任医生">{{ detailData.responsibleDoctor }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 'completed' ? 'success' : detailData.status === 'in_progress' ? 'warning' : 'info'" size="small">
            {{ detailData.status === 'completed' ? '已完成' : detailData.status === 'in_progress' ? '执行中' : '待执行' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="认知训练" :span="2">{{ detailData.cognitiveTraining || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生活方式干预" :span="2">{{ detailData.lifestyleIntervention || '-' }}</el-descriptions-item>
        <el-descriptions-item label="康复计划" :span="2">{{ detailData.rehabilitationPlan || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标" :span="2">{{ detailData.goals || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { interventionPlanApi, interventionExecutionApi, elderApi, userApi, systemApi } from '../api'
import { exportToExcel } from '../utils/export'
import { buildNameMap, getDisplayName } from '../utils/nameResolver'

const user = JSON.parse(localStorage.getItem('user') || '{}')

const planTypes = ref({})

const loadPlanTypes = async () => {
  try {
    const res = await systemApi.dictByType('plan_type')
    const map = {}
    if (res.data) {
      res.data.forEach(item => { map[item.dictKey] = item.dictValue })
    }
    planTypes.value = map
  } catch { }
}

const planTypeLabel = (val) => {
  return planTypes.value[val] || val
}

const tableData = ref([])
const elderList = ref([])
const elderNameMap = computed(() => buildNameMap(elderList.value))
const doctorList = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const searchElderId = ref('')
const searchStatus = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 执行记录相关
const execDialogVisible = ref(false)
const execSubmitting = ref(false)
const execFormRef = ref(null)
const execForm = ref({
  planId: null, elderId: null, planName: '', executionType: 'cognitive_training',
  content: '', executionDate: '', duration: null, effectEvaluation: 'pending',
  evaluator: user.role !== 'admin' ? (user.realName || user.username) : '', remark: ''
})
const execRules = {
  executionType: [{ required: true, message: '请选择执行类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入执行内容', trigger: 'blur' }],
  executionDate: [{ required: true, message: '请选择执行日期', trigger: 'change' }]
}

// 详情相关
const detailVisible = ref(false)
const detailData = ref(null)

const form = ref({
  elderId: '', planName: '', planType: 'cognitive', riskLevel: 'low',
  cognitiveTraining: '', lifestyleIntervention: '', rehabilitationPlan: '',
  goals: '', startDate: '', endDate: '', responsibleDoctor: '', status: 'pending'
})

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑计划' : '添加计划')

const parseNumber = (val) => {
  if (val === '' || val === null || val === undefined) return undefined
  const n = parseInt(val, 10)
  return isNaN(n) ? undefined : n
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { status: searchStatus.value || undefined, pageNum: pageNum.value, pageSize: pageSize.value }
    const eId = parseNumber(searchElderId.value)
    if (eId) params.elderId = eId
    const res = await interventionPlanApi.list(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }

const handleExport = () => {
  exportToExcel(tableData.value, [
    { prop: 'id', label: 'ID' },
    { prop: 'elderId', label: '老人ID' },
    { prop: 'planName', label: '计划名称' },
    { prop: 'planType', label: '计划类型' },
    { prop: 'riskLevel', label: '风险等级' },
    { prop: 'startDate', label: '开始日期' },
    { prop: 'endDate', label: '结束日期' },
    { prop: 'responsibleDoctor', label: '责任医生' },
    { prop: 'status', label: '状态' }
  ], '干预计划列表')
}

const openAdd = () => {
  isEdit.value = false; editId.value = null
  resetForm()
  // 添加时默认状态为待执行
  form.value.status = 'pending'
  // 医生角色默认责任医生为自己
  if (user.role !== 'admin') {
    form.value.responsibleDoctor = user.realName || user.username
  }
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  form.value = { ...row }; dialogVisible.value = true
}

const resetForm = () => {
  form.value = { elderId: '', planName: '', planType: 'cognitive', riskLevel: 'low', cognitiveTraining: '', lifestyleIntervention: '', rehabilitationPlan: '', goals: '', startDate: '', endDate: '', responsibleDoctor: '', status: 'pending' }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await interventionPlanApi.update({ ...form.value, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await interventionPlanApi.add(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该干预计划吗？', '警告', { type: 'warning' })
  try { await interventionPlanApi.delete(row.id); ElMessage.success('删除成功'); loadData() } catch { }
}

const handleComplete = async (row) => {
  await ElMessageBox.confirm('确定将该计划标记为已完成吗？', '确认完成', { type: 'info' })
  try {
    await interventionPlanApi.update({ ...row, status: 'completed' })
    ElMessage.success('已标记为完成')
    loadData()
  } catch { }
}

const openDetail = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const openExecute = (row) => {
  execForm.value = {
    planId: row.id, elderId: row.elderId, planName: row.planName,
    executionType: 'cognitive_training', content: '', executionDate: '',
    duration: null, effectEvaluation: 'pending',
    evaluator: user.role !== 'admin' ? (user.realName || user.username) : '', remark: ''
  }
  execDialogVisible.value = true
}

const resetExecForm = () => {
  execForm.value = {
    planId: null, elderId: null, planName: '', executionType: 'cognitive_training',
    content: '', executionDate: '', duration: null, effectEvaluation: 'pending',
    evaluator: user.role !== 'admin' ? (user.realName || user.username) : '', remark: ''
  }
  execFormRef.value?.resetFields()
}

const handleExecSubmit = async () => {
  const valid = await execFormRef.value.validate().catch(() => false)
  if (!valid) return
  execSubmitting.value = true
  try {
    const data = {
      planId: Number(execForm.value.planId),
      elderId: Number(execForm.value.elderId),
      executionType: execForm.value.executionType,
      content: execForm.value.content,
      executionDate: execForm.value.executionDate,
      duration: execForm.value.duration,
      effectEvaluation: execForm.value.effectEvaluation,
      evaluator: execForm.value.evaluator,
      remark: execForm.value.remark
    }
    await interventionExecutionApi.add(data)

    // 如果计划是待执行状态，自动更新为执行中
    const plan = tableData.value.find(p => p.id === execForm.value.planId)
    if (plan && plan.status === 'pending') {
      await interventionPlanApi.update({ ...plan, status: 'in_progress' })
    }

    ElMessage.success('执行记录添加成功')
    execDialogVisible.value = false
    loadData()
  } catch { } finally { execSubmitting.value = false }
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

onMounted(() => {
  loadData()
  loadElders()
  loadDoctors()
  loadPlanTypes()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
:deep(.el-table__row) { cursor: pointer; }
</style>
