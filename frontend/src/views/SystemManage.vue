<template>
  <div class="page">
    <!-- 系统统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e3f2fd; color: #1976d2;">
              <el-icon :size="24"><Notebook /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ sysStats.dictTypes || 0 }}</h4>
              <p>字典类型数</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据字典管理 -->
    <el-card shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span class="card-title">数据字典管理</span>
          <el-button type="primary" size="small" @click="openAddDict" v-if="currentDictType">
            <el-icon><Plus /></el-icon>添加字典项
          </el-button>
        </div>
      </template>

      <!-- 字典类型Tabs -->
      <el-tabs v-model="currentDictType" @tab-change="loadDictItems">
        <el-tab-pane v-for="type in dictTypes" :key="type" :label="getDictTypeLabel(type)" :name="type" />
      </el-tabs>

      <el-table :data="dictItems" border stripe v-loading="loading" v-if="currentDictType">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dictKey" label="字典键" width="180" />
        <el-table-column prop="dictValue" label="字典值" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDict(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteDict(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!currentDictType" description="请选择字典类型" />
    </el-card>

    <!-- 字典项编辑弹窗 -->
    <el-dialog :title="dictDialogTitle" v-model="dictDialogVisible" width="500px" @close="resetDictForm">
      <el-form :model="dictForm" ref="dictFormRef" label-width="80px">
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="dictForm.dictType" :disabled="isEditDict" />
        </el-form-item>
        <el-form-item label="字典键" prop="dictKey">
          <el-input v-model="dictForm.dictKey" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="dictForm.dictValue" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dictForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dictForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDict" :loading="saving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Notebook } from '@element-plus/icons-vue'
import { systemApi } from '../api'

const dictTypes = ref([])
const currentDictType = ref('')
const dictItems = ref([])
const loading = ref(false)
const saving = ref(false)
const dictDialogVisible = ref(false)
const isEditDict = ref(false)
const editDictId = ref(null)
const dictFormRef = ref(null)
const sysStats = ref({})

const dictForm = ref({ dictType: '', dictKey: '', dictValue: '', sort: 0, remark: '' })

const dictDialogTitle = computed(() => isEditDict.value ? '编辑字典项' : '添加字典项')

const dictTypeLabelMap = {
  risk_level: '风险等级', plan_status: '计划状态', assessment_type: '评估类型'
}

const getDictTypeLabel = (type) => dictTypeLabelMap[type] || type

const loadSysStats = async () => {
  try { const res = await systemApi.statistics(); sysStats.value = res.data || {} } catch { }
}

const loadDictTypes = async () => {
  try {
    const res = await systemApi.dictTypes()
    dictTypes.value = res.data || []
    if (res.data && res.data.length > 0) {
      currentDictType.value = res.data[0]
      loadDictItems(currentDictType.value)
    }
  } catch { }
}

const loadDictItems = async (type) => {
  if (!type) return
  loading.value = true
  try {
    const res = await systemApi.dictByType(type)
    dictItems.value = res.data || []
  } catch { dictItems.value = [] } finally { loading.value = false }
}

const openAddDict = () => {
  isEditDict.value = false; editDictId.value = null
  dictForm.value = { dictType: currentDictType.value, dictKey: '', dictValue: '', sort: 0, remark: '' }
  dictDialogVisible.value = true
}

const openEditDict = (row) => {
  isEditDict.value = true; editDictId.value = row.id
  dictForm.value = { ...row }
  dictDialogVisible.value = true
}

const resetDictForm = () => {
  dictForm.value = { dictType: '', dictKey: '', dictValue: '', sort: 0, remark: '' }
}

const saveDict = async () => {
  saving.value = true
  try {
    if (isEditDict.value) {
      await systemApi.updateDict({ ...dictForm.value, id: editDictId.value })
      ElMessage.success('更新成功')
    } else {
      await systemApi.addDict(dictForm.value)
      ElMessage.success('添加成功')
    }
    dictDialogVisible.value = false
    loadDictItems(currentDictType.value)
    loadSysStats()
  } catch { } finally { saving.value = false }
}

const deleteDict = async (row) => {
  await ElMessageBox.confirm('确定删除该字典项吗？', '警告', { type: 'warning' })
  try { await systemApi.deleteDict(row.id); ElMessage.success('删除成功'); loadDictItems(currentDictType.value); loadSysStats() } catch { }
}

onMounted(() => { loadSysStats(); loadDictTypes() })
</script>

<style scoped>
.stats-row { margin-bottom: 20px; }
.stat-card { cursor: default; }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon {
  width: 52px; height: 52px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-info h4 { font-size: 26px; font-weight: 700; color: #333; }
.stat-info p { font-size: 13px; color: #999; margin-top: 2px; }
.card-title { font-size: 16px; font-weight: 600; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
