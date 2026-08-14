<template>
  <div class="page">
    <el-card shadow="never">
      <div class="page-header">
        <h3>健康监测</h3>
      </div>

      <div class="search-bar">
        <el-select v-model="searchElderId" placeholder="选择老人" clearable filterable style="width: 200px" @clear="loadData" @change="loadData">
          <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">查询</el-button>
      </div>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span class="card-title">血压趋势</span></template>
            <div ref="bpChartRef" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span class="card-title">血糖趋势</span></template>
            <div ref="bsChartRef" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="hover" style="margin-top: 20px">
        <template #header><span class="card-title">最近健康数据</span></template>
        <el-table :data="healthData" border stripe v-loading="loading">
          <el-table-column prop="collectionDate" label="采集日期" width="120" />
          <el-table-column label="数据来源" width="100">
            <template #default="{ row }">
              <el-tag :type="row.dataSource === 'smart' ? 'primary' : row.dataSource === 'questionnaire' ? 'success' : 'warning'" size="small">
                {{ row.dataSource === 'smart' ? '智能评估' : row.dataSource === 'questionnaire' ? '健康问询' : '影像报告' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="数据类型" width="120">
            <template #default="{ row }">{{ dataTypeLabel(row.dataSource, row.dataType) }}</template>
          </el-table-column>
          <el-table-column prop="dataContent" label="数据内容" show-overflow-tooltip />
          <el-table-column prop="collector" label="采集人" width="80" />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { dataCollectionApi, elderApi, systemApi } from '../api'

const searchElderId = ref(null)
const loading = ref(false)
const healthData = ref([])
const elderList = ref([])
const bpChartRef = ref(null)
const bsChartRef = ref(null)
let bpChart = null
let bsChart = null

// 字典数据
const dictMap = ref({
  data_source: {},
  smart_type: {},
  questionnaire_type: {},
  image_type: {}
})

const loadDict = async () => {
  try {
    const types = ['data_source', 'smart_type', 'questionnaire_type', 'image_type']
    for (const type of types) {
      const res = await systemApi.dictByType(type)
      const map = {}
      if (res.data) {
        res.data.forEach(item => { map[item.dictKey] = item.dictValue })
      }
      dictMap.value[type] = map
    }
  } catch { }
}

const initChart = (el) => {
  if (!el) return null
  return echarts.init(el)
}

const dataTypeLabel = (source, type) => {
  if (source === 'smart') {
    return dictMap.value.smart_type[type] || type
  }
  if (source === 'questionnaire') {
    return dictMap.value.questionnaire_type[type] || type
  }
  if (source === 'image') {
    return dictMap.value.image_type[type] || type
  }
  return type
}

const loadData = async () => {
  if (!searchElderId.value) return
  loading.value = true
  try {
    const res = await dataCollectionApi.list({ elderId: searchElderId.value, pageNum: 1, pageSize: 20 })
    healthData.value = res.data.list || []
    renderCharts()
  } catch { healthData.value = [] } finally { loading.value = false }
}

// 按日期去重，每天只保留最新一条
const dedupeByDate = (records) => {
  const map = new Map()
  for (const r of records) {
    const date = r.collectionDate
    if (!date) continue
    if (!map.has(date)) {
      map.set(date, r)
    }
  }
  return Array.from(map.values()).sort((a, b) => a.collectionDate.localeCompare(b.collectionDate))
}

const renderCharts = () => {
  const bodyRecords = healthData.value
    .filter(d => d.dataType === 'blood_test' || d.dataType === 'BODY_CHECK')
  const dailyRecords = dedupeByDate(bodyRecords)

  // 血压数据
  const bpDates = dailyRecords.map(d => d.collectionDate)
  const bpData = dailyRecords.map(d => {
    const match = d.dataContent?.match(/血压(\d+)\/(\d+)/)
    return match ? parseInt(match[1]) : null
  })

  // 血糖数据
  const bsDates = dailyRecords.map(d => d.collectionDate)
  const bsData = dailyRecords.map(d => {
    const match = d.dataContent?.match(/血糖([\d.]+)/)
    return match ? parseFloat(match[1]) : null
  })

  if (bpChart) {
    bpChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: bpDates },
      yAxis: { type: 'value', name: 'mmHg' },
      series: [{ name: '收缩压', type: 'line', data: bpData, smooth: true }]
    })
  }

  if (bsChart) {
    bsChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: bsDates },
      yAxis: { type: 'value', name: 'mmol/L' },
      series: [{ name: '空腹血糖', type: 'line', data: bsData, smooth: true }]
    })
  }
}

const loadElders = async () => {
  try {
    const res = await elderApi.list({ pageNum: 1, pageSize: 1000 })
    elderList.value = res.data.list || []
  } catch { elderList.value = [] }
}

onMounted(async () => {
  await nextTick()
  bpChart = initChart(bpChartRef.value)
  bsChart = initChart(bsChartRef.value)
  loadElders()
  loadDict()
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h3 { margin: 0; color: #1a5c7d; }
.search-bar { margin-top: 15px; display: flex; align-items: center; }
.card-title { font-size: 16px; font-weight: 600; }
</style>