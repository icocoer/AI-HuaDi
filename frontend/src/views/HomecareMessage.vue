<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>医患沟通</h3>
        <el-button type="primary" @click="openSend">
          <el-icon><Plus /></el-icon>发送消息
        </el-button>
      </div>

      <div class="search-bar">
        <el-input v-model="searchReceiverId" placeholder="接收者ID" clearable style="width: 140px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="searchIsRead" placeholder="状态" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="未读" :value="0" /><el-option label="已读" :value="1" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="180" />
        <el-table-column label="发送者" width="100">
          <template #default="{ row }">{{ getDisplayName(userNameMap, row.senderId) }}</template>
        </el-table-column>
        <el-table-column label="接收者" width="100">
          <template #default="{ row }">{{ getDisplayName(userNameMap, row.receiverId) }}</template>
        </el-table-column>
        <el-table-column prop="messageType" label="消息类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.messageType === 'text' ? 'info' : row.messageType === 'image' ? 'success' : 'warning'" size="small">
              {{ row.messageType === 'text' ? '文字' : row.messageType === 'image' ? '图片' : '系统' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="消息内容" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 0 ? 'danger' : 'success'" size="small">
              {{ row.isRead === 0 ? '未读' : '已读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleMarkRead(row)" :disabled="row.isRead === 1">标记已读</el-button>
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

    <!-- 发送消息弹窗 -->
    <el-dialog title="发送消息" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="接收者" prop="receiverId">
          <el-select v-model="form.receiverId" placeholder="选择接收者" filterable style="width: 100%">
            <el-option v-for="u in userList" :key="u.id" :label="`${u.realName || u.username}（${u.id}）`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息类型" prop="messageType">
          <el-select v-model="form.messageType" style="width: 100%">
            <el-option label="文字" value="text" /><el-option label="图片" value="image" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { homecareApi, userApi } from '../api'
import { buildNameMap, getDisplayName } from '../utils/nameResolver'

const tableData = ref([])
const userList = ref([])
const userNameMap = computed(() => buildNameMap(userList.value))
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const searchReceiverId = ref('')
const searchIsRead = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const user = JSON.parse(localStorage.getItem('user') || '{}')

const form = ref({
  receiverId: '', messageType: 'text', content: ''
})

const rules = {
  receiverId: [{ required: true, message: '请选择接收者', trigger: 'change' }],
  content: [{ required: true, message: '请输入消息内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { receiverId: searchReceiverId.value || undefined, isRead: searchIsRead.value !== '' ? searchIsRead.value : undefined, pageNum: pageNum.value, pageSize: pageSize.value }
    const res = await homecareApi.messageList(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const openSend = () => { dialogVisible.value = true }
const resetForm = () => {
  form.value = { receiverId: '', messageType: 'text', content: '' }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await homecareApi.sendMessage({ ...form.value, senderId: user.id })
    ElMessage.success('消息发送成功')
    dialogVisible.value = false; loadData()
  } catch { } finally { submitting.value = false }
}

const handleMarkRead = async (row) => {
  try {
    await homecareApi.markMessageRead(row.id)
    ElMessage.success('已标记为已读')
    loadData()
  } catch { }
}

const loadUsers = async () => {
  try {
    const res = await userApi.list({ pageNum: 1, pageSize: 1000 })
    userList.value = res.data.list || []
  } catch { userList.value = [] }
}

onMounted(() => {
  loadData()
  loadUsers()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>