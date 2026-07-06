<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>用户管理</h3>
        <el-button type="primary" @click="openAdd" v-if="isAdmin">
          <el-icon><Plus /></el-icon>添加用户
        </el-button>
      </div>

      <div class="search-bar">
        <el-input v-model="searchUsername" placeholder="用户名" clearable style="width: 180px" @clear="loadData" @keyup.enter="loadData" />
        <el-input v-model="searchRealName" placeholder="真实姓名" clearable style="width: 180px; margin-left: 10px" @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : row.role === 'doctor' ? 'warning' : 'info'" size="small">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-if="isAdmin">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" :placeholder="isEdit ? '留空不修改' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="医生" value="doctor" />
            <el-option label="护士" value="nurse" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { userApi } from '../api'

const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)
const searchUsername = ref('')
const searchRealName = ref('')

const user = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}') } catch { return {} }
})
const isAdmin = computed(() => user.value.role === 'admin')

const form = ref({
  username: '', password: '', realName: '', phone: '', email: '', role: 'nurse', status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '添加用户')

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.list({ username: searchUsername.value, realName: searchRealName.value })
    tableData.value = res.data || []
  } catch { tableData.value = [] } finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false; editId.value = null; dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id
  form.value = { ...row, password: '' }; dialogVisible.value = true
}

const resetForm = () => {
  form.value = { username: '', password: '', realName: '', phone: '', email: '', role: 'nurse', status: 1 }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      const data = { ...form.value, id: editId.value }
      if (!data.password) delete data.password
      await userApi.update(data)
      ElMessage.success('更新成功')
    } else {
      await userApi.add(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { } finally { submitting.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该用户吗？', '警告', { type: 'warning' })
  try {
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>
