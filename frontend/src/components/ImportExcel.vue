<template>
  <el-dialog v-model="visible" title="导入Excel" width="600px" @close="reset">
    <el-upload
      ref="uploadRef"
      :auto-upload="false"
      :limit="1"
      accept=".xlsx,.xls"
      :on-change="handleFileChange"
      :on-exceed="() => ElMessage.warning('只能上传一个文件')"
      drag
    >
      <el-icon :size="40"><UploadFilled /></el-icon>
      <div>将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div style="color: #999; font-size: 12px;">仅支持 .xlsx / .xls 格式</div>
      </template>
    </el-upload>

    <div v-if="previewData.length > 0" style="margin-top: 16px;">
      <div style="margin-bottom: 8px; font-weight: 600;">预览（前5条）：</div>
      <el-table :data="previewData.slice(0, 5)" border size="small" max-height="200">
        <el-table-column v-for="col in previewColumns" :key="col" :prop="col" :label="col" />
      </el-table>
      <div style="margin-top: 8px; color: #666;">共 {{ previewData.length }} 条数据</div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :disabled="previewData.length === 0" @click="handleImport">确认导入</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'

const visible = ref(false)
const previewData = ref([])
const previewColumns = ref([])
const uploadRef = ref(null)
let resolvePromise = null

const open = () => {
  visible.value = true
  return new Promise((resolve) => {
    resolvePromise = resolve
  })
}

const handleFileChange = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const wb = XLSX.read(e.target.result, { type: 'array' })
      const ws = wb.Sheets[wb.SheetNames[0]]
      const jsonData = XLSX.utils.sheet_to_json(ws)
      if (jsonData.length === 0) {
        ElMessage.warning('文件为空')
        return
      }
      previewData.value = jsonData
      previewColumns.value = Object.keys(jsonData[0])
    } catch {
      ElMessage.error('文件解析失败')
    }
  }
  reader.readAsArrayBuffer(file.raw)
}

const handleImport = () => {
  if (resolvePromise) {
    resolvePromise(previewData.value)
  }
  visible.value = false
  ElMessage.success(`成功导入 ${previewData.value.length} 条数据`)
}

const reset = () => {
  previewData.value = []
  previewColumns.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

defineExpose({ open })
</script>
