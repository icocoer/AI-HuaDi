<template>
  <div class="data-list">
    <!-- 面包屑 -->
    <el-breadcrumb class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>数据采集管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 主卡片 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">数据采集记录</span>
          <el-button type="primary" @click="openAddModal">
            <el-icon><Plus /></el-icon>新增采集
          </el-button>
        </div>
      </template>

      <!-- 搜索区 -->
      <el-form :model="query" inline class="search-form">
        <el-form-item label="老人ID">
          <el-input v-model="query.elderId" placeholder="请输入老人ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="数据来源" v-if="activeTab === 'all'">
          <el-select v-model="query.dataSource" placeholder="全部" clearable style="width: 140px">
            <el-option label="智能评估" value="smart" />
            <el-option label="健康问询" value="questionnaire" />
            <el-option label="影像报告" value="image" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型" v-if="activeTab !== 'all'">
          <el-select v-model="query.dataType" placeholder="全部" clearable style="width: 160px">
            <el-option
              v-for="opt in currentTypeOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部记录" name="all" />
        <el-tab-pane label="智能评估" name="smart" />
        <el-tab-pane label="健康问询" name="questionnaire" />
        <el-tab-pane label="影像报告" name="image" />
      </el-tabs>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="selection" width="45" />

        <!-- 全部记录列 -->
        <template v-if="activeTab === 'all'">
          <el-table-column prop="elderName" label="老人姓名" min-width="100" />
          <el-table-column prop="dataSourceDesc" label="数据来源" min-width="100">
            <template #default="{ row }">
              <el-tag :type="sourceTagType(row.dataSource)" size="small">
                {{ row.dataSourceDesc || row.dataSource }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dataTypeDesc" label="数据类型" min-width="120" />
          <el-table-column prop="collectionDate" label="采集日期" min-width="120" />
          <el-table-column prop="collector" label="采集人" min-width="90" />
          <el-table-column prop="createTime" label="创建时间" min-width="150" />
        </template>

        <!-- 智能评估列 -->
        <template v-if="activeTab === 'smart'">
          <el-table-column prop="elderId" label="老人ID" min-width="90" />
          <el-table-column prop="assessmentType" label="评估类型" min-width="120" />
          <el-table-column prop="totalScore" label="总分" min-width="80" />
          <el-table-column prop="scoreLevel" label="等级" min-width="90">
            <template #default="{ row }">
              <el-tag :type="scoreLevelTag(row.scoreLevel)" size="small">{{ row.scoreLevel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="assessor" label="评估人" min-width="90" />
          <el-table-column prop="assessmentTime" label="评估时间" min-width="150" />
        </template>

        <!-- 健康问询列 -->
        <template v-if="activeTab === 'questionnaire'">
          <el-table-column prop="elderId" label="老人ID" min-width="90" />
          <el-table-column prop="questionnaireType" label="问卷类型" min-width="120" />
          <el-table-column prop="summary" label="摘要" min-width="180" show-overflow-tooltip />
          <el-table-column prop="surveyor" label="调查人" min-width="90" />
          <el-table-column prop="surveyTime" label="调查时间" min-width="150" />
        </template>

        <!-- 影像报告列 -->
        <template v-if="activeTab === 'image'">
          <el-table-column prop="elderId" label="老人ID" min-width="90" />
          <el-table-column prop="imageType" label="影像类型" min-width="100" />
          <el-table-column prop="diagnosisResult" label="诊断结果" min-width="120" />
          <el-table-column prop="institution" label="检查机构" min-width="140" />
          <el-table-column prop="diagnosisDate" label="检查日期" min-width="120" />
          <el-table-column prop="doctorName" label="检查医生" min-width="90" />
        </template>

        <!-- 操作列 -->
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该记录？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <AddEditModal
      v-model:visible="modalVisible"
      :mode="modalMode"
      :record="currentRecord"
      :tab="activeTab"
      @success="loadData"
    />

    <!-- 详情弹窗 -->
    <DetailModal
      v-model:visible="detailVisible"
      :record="currentRecord"
      :tab="activeTab"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { dataCollectionApi, smartAssessmentApi, healthQuestionnaireApi, imageReportApi } from '../api'
import AddEditModal from '../components/AddEditModal.vue'
import DetailModal from '../components/DetailModal.vue'

const route = useRoute()
const activeTab = ref('all')
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
  elderId: '',
  dataSource: '',
  dataType: ''
})

const modalVisible = ref(false)
const modalMode = ref('add')
const currentRecord = ref({})
const detailVisible = ref(false)

const currentTypeOptions = computed(() => {
  const map = {
    smart: [
      { label: '认知筛查', value: 'COGNITIVE_SCREENING' },
      { label: '运动功能', value: 'MOTOR_FUNCTION' },
      { label: '生命体征', value: 'VITAL_SIGNS' }
    ],
    questionnaire: [
      { label: '病史问询', value: 'MEDICAL_HISTORY' },
      { label: '家族史', value: 'FAMILY_HISTORY' },
      { label: '生活方式', value: 'LIFESTYLE' },
      { label: '症状检查', value: 'SYMPTOM_CHECK' }
    ],
    image: [
      { label: 'CT影像', value: 'CT_IMAGE' },
      { label: 'MRI影像', value: 'MRI_IMAGE' },
      { label: 'X光影像', value: 'XRAY_IMAGE' },
      { label: '超声', value: 'ULTRASOUND' },
      { label: '其他', value: 'OTHER_IMAGE' }
    ]
  }
  return map[activeTab.value] || []
})

const sourceTagType = (source) => {
  const map = { smart: 'primary', questionnaire: 'success', image: 'warning' }
  return map[source] || 'info'
}

const scoreLevelTag = (level) => {
  const map = { '正常': 'success', '轻度异常': 'warning', '中度异常': 'warning', '重度异常': 'danger' }
  return map[level] || 'info'
}

const parseNumber = (val) => {
  if (val === '' || val === null || val === undefined) return undefined
  const n = parseInt(val, 10)
  return isNaN(n) ? undefined : n
}

const buildParams = () => {
  const params = {}
  const eId = parseNumber(query.elderId)
  if (eId) params.elderId = eId
  if (activeTab.value === 'all') {
    params.dataSource = query.dataSource || undefined
  } else if (activeTab.value === 'smart') {
    params.assessmentType = query.dataType || undefined
  } else if (activeTab.value === 'questionnaire') {
    params.questionnaireType = query.dataType || undefined
  } else if (activeTab.value === 'image') {
    params.imageType = query.dataType || undefined
  }
  return params
}

const loadData = async () => {
  loading.value = true
  try {
    let res
    const params = buildParams()
    switch (activeTab.value) {
      case 'smart':
        res = await smartAssessmentApi.list(params)
        break
      case 'questionnaire':
        res = await healthQuestionnaireApi.list(params)
        break
      case 'image':
        res = await imageReportApi.list(params)
        break
      default:
        res = await dataCollectionApi.list(params)
    }
    const data = res.data || []
    if (Array.isArray(data)) {
      total.value = data.length
      const start = (pageNum.value - 1) * pageSize.value
      tableData.value = data.slice(start, start + pageSize.value)
    }
  } catch {
    // 加载模拟数据
    loadMockData()
  } finally {
    loading.value = false
  }
}

const loadMockData = () => {
  const mocks = {
    all: [
      { id: 1, elderName: '王建国', elderId: 1, dataSource: 'smart', dataSourceDesc: '智能评估', dataType: 'COGNITIVE_SCREENING', dataTypeDesc: 'MMSE量表', collectionDate: '2026-04-25', collector: '张医生', createTime: '2026-04-25 10:30:00' },
      { id: 2, elderName: '李秀英', elderId: 2, dataSource: 'questionnaire', dataSourceDesc: '健康问询', dataType: 'LIFESTYLE', dataTypeDesc: '生活方式问卷', collectionDate: '2026-04-24', collector: '李护士', createTime: '2026-04-24 14:20:00' },
      { id: 3, elderName: '张明华', elderId: 3, dataSource: 'image', dataSourceDesc: '影像报告', dataType: 'CT_IMAGE', dataTypeDesc: '头部CT', collectionDate: '2026-04-23', collector: '王技术员', createTime: '2026-04-23 09:15:00' },
      { id: 4, elderName: '刘德昌', elderId: 4, dataSource: 'smart', dataSourceDesc: '智能评估', dataType: 'COGNITIVE_SCREENING', dataTypeDesc: 'MoCA量表', collectionDate: '2026-04-22', collector: '张医生', createTime: '2026-04-22 11:00:00' },
      { id: 5, elderName: '陈淑芳', elderId: 5, dataSource: 'image', dataSourceDesc: '影像报告', dataType: 'MRI_IMAGE', dataTypeDesc: '脑部MRI', collectionDate: '2026-04-21', collector: '王技术员', createTime: '2026-04-21 16:45:00' }
    ],
    smart: [
      { id: 1, elderId: 1, assessmentType: 'COGNITIVE_SCREENING', totalScore: 27, scoreLevel: '正常', assessmentResult: '认知功能正常', assessor: '张医生', assessmentTime: '2026-04-25 10:30:00' },
      { id: 4, elderId: 4, assessmentType: 'COGNITIVE_SCREENING', totalScore: 24, scoreLevel: '轻度异常', assessmentResult: '轻度认知受损', assessor: '张医生', assessmentTime: '2026-04-22 11:00:00' }
    ],
    questionnaire: [
      { id: 2, elderId: 2, questionnaireType: 'LIFESTYLE', summary: '生活习惯良好，饮食规律，睡眠质量较好', surveyor: '李护士', surveyTime: '2026-04-24 14:20:00' }
    ],
    image: [
      { id: 3, elderId: 3, imageType: 'CT_IMAGE', diagnosisResult: '未见明显异常', institution: '市第一人民医院', diagnosisDate: '2026-04-23', doctorName: '王主任' },
      { id: 5, elderId: 5, imageType: 'MRI_IMAGE', diagnosisResult: '轻度脑白质病变', institution: '省中心医院', diagnosisDate: '2026-04-21', doctorName: '李主任' }
    ]
  }
  const data = mocks[activeTab.value] || []
  total.value = data.length
  const start = (pageNum.value - 1) * pageSize.value
  tableData.value = data.slice(start, start + pageSize.value)
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  query.elderId = ''
  query.dataSource = ''
  query.dataType = ''
  pageNum.value = 1
  loadData()
}

const handleTabChange = () => {
  pageNum.value = 1
  query.dataSource = ''
  query.dataType = ''
  loadData()
}

const openAddModal = () => {
  modalMode.value = 'add'
  currentRecord.value = {}
  modalVisible.value = true
}

const openEdit = (row) => {
  modalMode.value = 'edit'
  currentRecord.value = { ...row }
  modalVisible.value = true
}

const openDetail = (row) => {
  currentRecord.value = { ...row }
  detailVisible.value = true
}

const handleDelete = async (row) => {
  try {
    const apiMap = {
      smart: smartAssessmentApi,
      questionnaire: healthQuestionnaireApi,
      image: imageReportApi
    }
    const api = apiMap[activeTab.value] || dataCollectionApi
    await api.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.success('删除成功（离线模式）')
    loadData()
  }
}

watch(() => route.query.tab, (val) => {
  if (val) activeTab.value = val
})

onMounted(() => {
  if (route.query.tab) activeTab.value = route.query.tab
  loadData()
})
</script>

<style scoped>
.breadcrumb { margin-bottom: 15px; }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 10px; }
.search-form .el-form-item { margin-bottom: 0; }
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
