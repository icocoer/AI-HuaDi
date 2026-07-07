<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>健康预警</h3>
      </div>

      <div class="search-bar">
        <el-select v-model="searchElderId" placeholder="选择老人" clearable filterable style="width: 200px" @clear="loadData" @change="loadData">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
        <el-select v-model="searchIsRead" placeholder="状态" clearable style="width: 140px; margin-left: 10px" @clear="loadData" @change="loadData">
          <el-option label="未读" :value="0" /><el-option label="已读" :value="1" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 15px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="elderId" label="老人ID" width="80" />
        <el-table-column prop="alertType" label="预警类型" width="120">
          <template #default="{ row }">
            {{ row.alertType === 'blood_pressure' ? '血压' : row.alertType === 'blood_sugar' ? '血糖' : row.alertType === 'heart_rate' ? '心率' : row.alertType }}
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="预警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="row.alertLevel === 'danger' ? 'danger' : 'warning'" size="small">
              {{ row.alertLevel === 'danger' ? '危险' : '警告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertMessage" label="预警信息" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 0 ? 'danger' : 'success'" size="small">
              {{ row.isRead === 0 ? '未读' : '已读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { homecareApi, elderApi } from '../api'

const tableData = ref([])
const elderList = ref([])
const loading = ref(false)
const searchElderId = ref(null)
const searchIsRead = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const params = { elderId: searchElderId.value || undefined, isRead: searchIsRead.value !== '' ? searchIsRead.value : undefined, pageNum: pageNum.value, pageSize: pageSize.value }
    const res = await homecareApi.healthAlertList(params)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleMarkRead = async (row) => {
  try {
    await homecareApi.markAlertRead(row.id)
    ElMessage.success('已标记为已读')
    loadData()
  } catch { }
}

const loadElders = async () => {
  try {
    const res = await elderApi.list({ pageNum: 1, pageSize: 1000 })
    elderList.value = res.data.list || []
  } catch { elderList.value = [] }
}

onMounted(() => {
  loadData()
  loadElders()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
</style>