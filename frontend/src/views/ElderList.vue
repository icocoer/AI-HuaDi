<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>老人健康档案</h3>
        <el-button type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon>添加档案
        </el-button>
      </div>

      <div class="search-bar">
        <el-input v-model="searchName" placeholder="姓名" clearable style="width: 160px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="searchRiskLevel" placeholder="风险等级" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="低风险" value="low" />
          <el-option label="中风险" value="medium" />
          <el-option label="高风险" value="high" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 'M' ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === 'high' ? 'danger' : row.riskLevel === 'medium' ? 'warning' : 'success'" size="small">
              {{ row.riskLevel === 'high' ? '高' : row.riskLevel === 'medium' ? '中' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="emergencyContact" label="紧急联系人" width="100" />
        <el-table-column prop="emergencyPhone" label="紧急电话" width="130" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="viewProfile(row)">详情</el-button>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="openCreateAccount(row)" :disabled="row.hasAccount">创建账户</el-button>
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
            <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" style="width: 100%">
                <el-option label="男" value="M" /><el-option label="女" value="F" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate"><el-input v-model="form.birthDate" placeholder="yyyy-MM-dd" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="电话" prop="phone"><el-input v-model="form.phone" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址" prop="address"><el-input v-model="form.address" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact"><el-input v-model="form.emergencyContact" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急电话" prop="emergencyPhone"><el-input v-model="form.emergencyPhone" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="既往病史" prop="medicalHistory">
          <el-input v-model="form.medicalHistory" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="form.riskLevel" style="width: 200px">
            <el-option label="低风险" value="low" /><el-option label="中风险" value="medium" /><el-option label="高风险" value="high" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="老人完整档案" v-model="profileVisible" width="700px">
      <el-descriptions :column="2" border v-if="profile.record">
        <el-descriptions-item label="姓名">{{ profile.record.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ profile.record.gender === 'M' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ profile.record.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ profile.record.phone }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ profile.record.address }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">
          <el-tag :type="profile.record.riskLevel === 'high' ? 'danger' : profile.record.riskLevel === 'medium' ? 'warning' : 'success'" size="small">
            {{ profile.record.riskLevel }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="既往病史">{{ profile.record.medicalHistory || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="profile.assessments && profile.assessments.length" style="margin-top: 20px">
        <h4>评估记录</h4>
        <el-table :data="profile.assessments" border size="small" style="margin-top: 10px">
          <el-table-column prop="assessmentType" label="评估类型" width="100" />
          <el-table-column prop="totalScore" label="总分" width="80" />
          <el-table-column prop="riskLevel" label="风险等级" width="100" />
          <el-table-column prop="assessmentTime" label="评估时间" width="120" />
        </el-table>
      </div>
    </el-dialog>

    <!-- 创建账户弹窗 -->
    <el-dialog title="创建患者账户" v-model="accountDialogVisible" width="450px" @close="resetAccountForm">
      <el-form :model="accountForm" :rules="accountRules" ref="accountFormRef" label-width="100px">
        <el-form-item label="关联老人">
          <el-input :value="accountForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="accountForm.username" placeholder="用于登录" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="accountForm.password" type="password" placeholder="初始密码" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="accountForm.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="accountDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateAccount" :loading="accountSubmitting">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { elderApi, userApi } from '../api'
import { exportToExcel } from '../utils/export'

const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const profileVisible = ref(false)
const accountDialogVisible = ref(false)
const accountSubmitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const accountFormRef = ref(null)
const searchName = ref('')
const searchRiskLevel = ref('')
const profile = ref({})
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  name: '', gender: 'M', birthDate: '', idCard: '', phone: '', address: '',
  emergencyContact: '', emergencyPhone: '', medicalHistory: '', riskLevel: 'low', status: 1
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请输入出生日期', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑档案' : '添加档案')

const loadData = async () => {
  loading.value = true
  try {
    const res = await elderApi.list({ name: searchName.value, riskLevel: searchRiskLevel.value, pageNum: pageNum.value, pageSize: pageSize.value })
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }

const handleExport = () => {
  exportToExcel(tableData.value, [
    { prop: 'id', label: 'ID' },
    { prop: 'name', label: '姓名' },
    { prop: 'gender', label: '性别' },
    { prop: 'birthDate', label: '出生日期' },
    { prop: 'phone', label: '电话' },
    { prop: 'riskLevel', label: '风险等级' },
    { prop: 'address', label: '地址' },
    { prop: 'emergencyContact', label: '紧急联系人' },
    { prop: 'emergencyPhone', label: '紧急电话' }
  ], '老人健康档案')
}

const openAdd = () => { isEdit.value = false; editId.value = null; dialogVisible.value = true }

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  form.value = { ...row }; dialogVisible.value = true
}

const resetForm = () => {
  form.value = { name: '', gender: 'M', birthDate: '', idCard: '', phone: '', address: '', emergencyContact: '', emergencyPhone: '', medicalHistory: '', riskLevel: 'low', status: 1 }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await elderApi.update({ ...form.value, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await elderApi.add(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该档案吗？', '警告', { type: 'warning' })
  try { await elderApi.delete(row.id); ElMessage.success('删除成功'); loadData() } catch { }
}

const viewProfile = async (row) => {
  try {
    const res = await elderApi.fullProfile(row.id)
    profile.value = res.data || {}
    profileVisible.value = true
  } catch { }
}

// 创建账户相关
const accountForm = ref({
  elderId: null,
  elderName: '',
  username: '',
  password: '',
  phone: ''
})

const accountRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '长度2-50位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '长度6-100位', trigger: 'blur' }
  ]
}

const openCreateAccount = (row) => {
  accountForm.value = {
    elderId: row.id,
    elderName: row.name,
    username: '',
    password: '',
    phone: row.phone || ''
  }
  accountDialogVisible.value = true
}

const resetAccountForm = () => {
  accountForm.value = { elderId: null, elderName: '', username: '', password: '', phone: '' }
  accountFormRef.value?.resetFields()
}

const handleCreateAccount = async () => {
  const valid = await accountFormRef.value.validate().catch(() => false)
  if (!valid) return
  accountSubmitting.value = true
  try {
    await userApi.add({
      username: accountForm.value.username,
      password: accountForm.value.password,
      realName: accountForm.value.elderName,
      phone: accountForm.value.phone,
      role: 'patient',
      elderId: accountForm.value.elderId,
      status: 1
    })
    ElMessage.success('账户创建成功')
    accountDialogVisible.value = false
    loadData()
  } catch { } finally { accountSubmitting.value = false }
}

// 检查老人是否已有账户
const checkAccountStatus = async () => {
  try {
    const res = await userApi.list({ pageNum: 1, pageSize: 1000 })
    const users = res.data.list || []
    const patientElderIds = new Set(users.filter(u => u.role === 'patient' && u.elderId).map(u => u.elderId))
    tableData.value.forEach(row => {
      row.hasAccount = patientElderIds.has(row.id)
    })
  } catch { }
}

onMounted(async () => {
  await loadData()
  checkAccountStatus()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
h4 { margin: 0; color: #1a5c7d; }
</style>
