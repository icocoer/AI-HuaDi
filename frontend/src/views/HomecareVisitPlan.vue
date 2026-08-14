<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>随访计划管理</h3>
        <el-button type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon>添加计划
        </el-button>
      </div>

      <div class="search-bar">
        <el-select v-model="searchElderId" placeholder="选择老人" clearable filterable style="width: 200px" @clear="loadData" @change="loadData">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="待执行" value="pending" /><el-option label="已完成" value="completed" /><el-option label="已取消" value="cancelled" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column label="老人" width="100">
          <template #default="{ row }">{{ getDisplayName(elderNameMap, row.elderId) }}</template>
        </el-table-column>
        <el-table-column label="责任医生" width="100">
          <template #default="{ row }">{{ getDisplayName(doctorNameMap, row.doctorId) }}</template>
        </el-table-column>
        <el-table-column prop="visitType" label="随访类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.visitType === 'home' ? 'success' : row.visitType === 'phone' ? 'warning' : 'info'" size="small">
              {{ row.visitType === 'home' ? '上门' : row.visitType === 'phone' ? '电话' : '视频' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plannedDate" label="计划日期" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : row.status === 'cancelled' ? 'danger' : 'info'" size="small">
              {{ row.status === 'completed' ? '已完成' : row.status === 'cancelled' ? '已取消' : '待执行' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 15px; justify-content: flex-end"
        @current-change="loadData"
      />
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
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
            <el-form-item label="责任医生" prop="doctorId">
              <el-select v-model="form.doctorId" placeholder="选择医生" filterable :disabled="user.role !== 'admin'" style="width: 100%">
                <el-option v-for="u in doctorList" :key="u.id" :label="`${u.realName || u.username}（${u.id}）`" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="随访类型" prop="visitType">
              <el-select v-model="form.visitType" style="width: 100%">
                <el-option label="上门" value="home" /><el-option label="电话" value="phone" /><el-option label="视频" value="video" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划日期" prop="plannedDate"><el-date-picker v-model="form.plannedDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="待执行" value="pending" /><el-option label="已完成" value="completed" /><el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
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
import { homecareApi, elderApi, userApi } from '../api'
import { buildNameMap, getDisplayName } from '../utils/nameResolver'

const user = JSON.parse(localStorage.getItem('user') || '{}')

const tableData = ref([])
const elderList = ref([])
const elderNameMap = computed(() => buildNameMap(elderList.value))
const doctorList = ref([])
const doctorNameMap = computed(() => buildNameMap(doctorList.value))
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
  elderId: '', doctorId: '', visitType: 'home', plannedDate: '', status: 'pending', remark: ''
})

const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑随访计划' : '添加随访计划')

const loadData = async () => {
  loading.value = true
  try {
    const params = { elderId: searchElderId.value || undefined, status: searchStatus.value || undefined, pageNum: pageNum.value, pageSize: pageSize.value }
    const res = await homecareApi.visitPlanList(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false; editId.value = null
  resetForm()
  // 医生角色默认责任医生为自己
  if (user.role !== 'admin') {
    form.value.doctorId = user.id
  }
  dialogVisible.value = true
}
const openEdit = (row) => { isEdit.value = true; editId.value = row.id; form.value = { ...row }; dialogVisible.value = true }

const resetForm = () => {
  form.value = { elderId: '', doctorId: '', visitType: 'home', plannedDate: '', status: 'pending', remark: '' }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await homecareApi.updateVisitPlan({ ...form.value, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await homecareApi.addVisitPlan(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false; loadData()
  } catch { } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该随访计划吗？', '警告', { type: 'warning' })
  try { await homecareApi.deleteVisitPlan(row.id); ElMessage.success('删除成功'); loadData() } catch { }
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
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>