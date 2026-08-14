<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>认知评估管理</h3>
        <el-button type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon>添加评估
        </el-button>
      </div>

      <div class="search-bar">
        <el-select v-model="searchElderId" placeholder="选择老人" clearable filterable style="width: 200px" @clear="loadData" @change="loadData">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
        <el-select v-model="searchRiskLevel" placeholder="风险等级" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="低风险" value="low" /><el-option label="中风险" value="medium" /><el-option label="高风险" value="high" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column label="老人" width="100">
          <template #default="{ row }">{{ getDisplayName(elderNameMap, row.elderId) }}</template>
        </el-table-column>
        <el-table-column label="评估类型" width="120">
          <template #default="{ row }">{{ assessmentTypeLabel(row.assessmentType) }}</template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === 'high' ? 'danger' : row.riskLevel === 'medium' ? 'warning' : 'success'" size="small">
              {{ row.riskLevel === 'high' ? '高风险' : row.riskLevel === 'medium' ? '中风险' : '低风险' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assessor" label="评估人" width="100" />
        <el-table-column prop="assessmentTime" label="评估时间" width="160" />
        <el-table-column prop="nextAssessmentDate" label="下次评估" width="120" />
        <el-table-column prop="assessmentResult" label="评估结果" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="warning" @click="assessRisk(row.elderId)">风险评估</el-button>
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
            <el-form-item label="评估类型" prop="assessmentType">
              <el-select v-model="form.assessmentType" style="width: 100%">
                <el-option label="MMSE量表" value="mmse" /><el-option label="MoCA量表" value="moca" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore"><el-input v-model.number="form.totalScore" /></el-form-item>
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
            <el-form-item label="评估人" prop="assessor">
              <el-select v-model="form.assessor" placeholder="选择评估人" filterable :disabled="user.role !== 'admin'" style="width: 100%">
                <el-option v-for="u in doctorList" :key="u.id" :label="`${u.realName || u.username}（${u.role}）`" :value="u.realName || u.username" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次评估" prop="nextAssessmentDate"><el-input v-model="form.nextAssessmentDate" placeholder="yyyy-MM-dd" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="评估结果" prop="assessmentResult">
          <el-input v-model="form.assessmentResult" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="建议" prop="recommendations">
          <el-input v-model="form.recommendations" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 风险评估结果弹窗 -->
    <el-dialog title="风险评估结果" v-model="riskVisible" width="550px">
      <el-descriptions :column="2" border v-if="riskResult.currentLevel">
        <el-descriptions-item label="当前风险等级">
          <el-tag :type="riskResult.currentLevel === 'high' ? 'danger' : riskResult.currentLevel === 'medium' ? 'warning' : 'success'">
            {{ riskResult.currentLevel }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总分">{{ riskResult.totalScore }}</el-descriptions-item>
        <el-descriptions-item label="评估次数">{{ riskResult.assessmentCount }}</el-descriptions-item>
        <el-descriptions-item label="趋势">{{ riskResult.trendMsg }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无评估数据" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { assessmentApi, riskApi, elderApi, userApi, systemApi } from '../api'
import { exportToExcel } from '../utils/export'
import { buildNameMap, getDisplayName } from '../utils/nameResolver'

const user = JSON.parse(localStorage.getItem('user') || '{}')

const tableData = ref([])
const elderList = ref([])
const elderNameMap = computed(() => buildNameMap(elderList.value))
const doctorList = ref([])
const assessmentTypes = ref({})
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const riskVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const searchElderId = ref('')
const searchRiskLevel = ref('')
const riskResult = ref({})
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  elderId: '', assessmentType: 'mmse', totalScore: '', riskLevel: 'low',
  assessmentResult: '', recommendations: '', assessor: '', assessmentTime: '', nextAssessmentDate: ''
})

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  assessmentType: [{ required: true, message: '请选择评估类型', trigger: 'change' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑评估' : '添加评估')

const parseNumber = (val) => {
  if (val === '' || val === null || val === undefined) return undefined
  const n = parseInt(val, 10)
  return isNaN(n) ? undefined : n
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { riskLevel: searchRiskLevel.value || undefined, pageNum: pageNum.value, pageSize: pageSize.value }
    const eId = parseNumber(searchElderId.value)
    if (eId) params.elderId = eId
    const res = await assessmentApi.list(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }

const handleExport = () => {
  exportToExcel(tableData.value, [
    { prop: 'id', label: 'ID' },
    { prop: 'elderId', label: '老人ID' },
    { prop: 'assessmentType', label: '评估类型' },
    { prop: 'totalScore', label: '总分' },
    { prop: 'riskLevel', label: '风险等级' },
    { prop: 'assessor', label: '评估人' },
    { prop: 'assessmentTime', label: '评估时间' },
    { prop: 'nextAssessmentDate', label: '下次评估' },
    { prop: 'assessmentResult', label: '评估结果' }
  ], '认知评估列表')
}

const openAdd = () => {
  isEdit.value = false; editId.value = null
  resetForm()
  // 医生角色默认评估人为自己
  if (user.role !== 'admin') {
    form.value.assessor = user.realName || user.username
  }
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  form.value = { ...row }; dialogVisible.value = true
}

const resetForm = () => {
  form.value = { elderId: '', assessmentType: 'mmse', totalScore: '', riskLevel: 'low', assessmentResult: '', recommendations: '', assessor: '', assessmentTime: '', nextAssessmentDate: '' }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await assessmentApi.update({ ...form.value, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await assessmentApi.add({ ...form.value, assessmentTime: new Date().toISOString().slice(0, 19).replace('T', ' ') })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该评估记录吗？', '警告', { type: 'warning' })
  try { await assessmentApi.delete(row.id); ElMessage.success('删除成功'); loadData() } catch { }
}

const assessRisk = async (elderId) => {
  try {
    const res = await riskApi.assess(elderId)
    riskResult.value = res.data || {}
    riskVisible.value = true
  } catch { }
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

const loadAssessmentTypes = async () => {
  try {
    const res = await systemApi.dictByType('assessment_type')
    const map = {}
    if (res.data) {
      res.data.forEach(item => { map[item.dictKey] = item.dictValue })
    }
    assessmentTypes.value = map
  } catch { }
}

const assessmentTypeLabel = (val) => {
  return assessmentTypes.value[val] || val
}

onMounted(() => {
  loadData()
  loadElders()
  loadDoctors()
  loadAssessmentTypes()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>
