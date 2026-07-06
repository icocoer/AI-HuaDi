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
        <el-input v-model="searchElderId" placeholder="老人ID" clearable style="width: 140px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="待执行" value="pending" /><el-option label="执行中" value="in_progress" /><el-option label="已完成" value="completed" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="elderId" label="老人ID" width="80" />
        <el-table-column prop="planName" label="计划名称" width="160" show-overflow-tooltip />
        <el-table-column prop="planType" label="计划类型" width="100" />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="650px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="老人ID" prop="elderId"><el-input v-model.number="form.elderId" /></el-form-item>
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
            <el-form-item label="责任医生" prop="responsibleDoctor"><el-input v-model="form.responsibleDoctor" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { interventionPlanApi } from '../api'
import { exportToExcel } from '../utils/export'

const tableData = ref([])
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

const form = ref({
  elderId: '', planName: '', planType: 'cognitive', riskLevel: 'low',
  cognitiveTraining: '', lifestyleIntervention: '', rehabilitationPlan: '',
  goals: '', startDate: '', endDate: '', responsibleDoctor: '', status: 'pending'
})

const rules = {
  elderId: [{ required: true, message: '请输入老人ID', trigger: 'blur' }],
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

const openAdd = () => { isEdit.value = false; editId.value = null; dialogVisible.value = true }

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

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>
