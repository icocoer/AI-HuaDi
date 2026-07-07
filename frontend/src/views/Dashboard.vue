<template>
  <div class="dashboard">
    <!-- 核心统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e3f2fd; color: #1976d2;">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.totalElders || 0 }}</h4>
              <p>老人总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e8f5e9; color: #388e3c;">
              <el-icon :size="24"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.totalAssessments || 0 }}</h4>
              <p>评估总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fff3e0; color: #f57c00;">
              <el-icon :size="24"><List /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.totalPlans || 0 }}</h4>
              <p>干预计划数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e8f5e9; color: #388e3c;">
              <el-icon :size="24"><House /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.totalVisitPlans || 0 }}</h4>
              <p>随访计划数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fff3e0; color: #f57c00;">
              <el-icon :size="24"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.unreadAlerts || 0 }}</h4>
              <p>健康预警</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e3f2fd; color: #1976d2;">
              <el-icon :size="24"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.unreadMessages || 0 }}</h4>
              <p>未读消息</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">风险等级分布</span></template>
          <div ref="riskChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">数据来源分布</span></template>
          <div ref="sourceChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">干预计划状态</span></template>
          <div ref="planChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">快捷入口</span></template>
          <div class="quick-links">
            <el-row :gutter="12">
              <el-col :span="12" v-for="item in quickActions" :key="item.label">
                <div class="quick-link-card" @click="$router.push(item.path)">
                  <el-icon :size="24" :color="item.color"><component :is="item.icon" /></el-icon>
                  <div>
                    <h4>{{ item.label }}</h4>
                    <p>{{ item.desc }}</p>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import {
  User, Document, DataAnalysis, List, Bell, EditPen, Warning, House, ChatDotRound
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { analysisApi, dataCollectionApi } from '../api'

const stats = ref({})
const collectionStats = ref({})
const riskChartRef = ref(null)
const sourceChartRef = ref(null)
const planChartRef = ref(null)

let riskChart = null
let sourceChart = null
let planChart = null

const quickActions = [
  { label: '老人档案', desc: '管理老人健康档案', icon: Document, color: '#1976d2', path: '/elder-list' },
  { label: '认知评估', desc: '执行认知功能评估', icon: EditPen, color: '#388e3c', path: '/assessment-list' },
  { label: '干预计划', desc: '制定个性化干预方案', icon: List, color: '#f57c00', path: '/intervention-plan' },
  { label: '风险评估', desc: '查看风险预警信息', icon: Warning, color: '#d32f2f', path: '/risk-warning' },
  { label: '数据采集', desc: '智能评估/问询/影像', icon: DataAnalysis, color: '#7b1fa2', path: '/data-list' },
  { label: '家庭健康助手', desc: '远程随访与健康管理', icon: House, color: '#388e3c', path: '/homecare/visit-plan' },
  { label: '系统管理', desc: '用户与字典管理', icon: User, color: '#1976d2', path: '/system' }
]

const initChart = (el) => {
  if (!el) return null
  const chart = echarts.init(el)
  window.addEventListener('resize', () => chart.resize())
  return chart
}

const renderPieChart = (chart, title, data) => {
  if (!chart) return
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, left: 'center' },
    series: [{
      name: title,
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data
    }]
  })
}

const renderBarChart = (chart, categories, data) => {
  if (!chart) return
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: categories },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data,
      barWidth: '40%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#1976d2' },
          { offset: 1, color: '#4fc3f7' }
        ])
      }
    }]
  })
}

const loadData = async () => {
  try {
    const res = await analysisApi.dashboard()
    if (res.data) {
      stats.value = res.data
      const dist = res.data.riskDistribution || {}
      renderPieChart(riskChart, '风险等级分布', [
        { value: parseInt(dist.low) || 0, name: '低风险', itemStyle: { color: '#388e3c' } },
        { value: parseInt(dist.medium) || 0, name: '中风险', itemStyle: { color: '#f57c00' } },
        { value: parseInt(dist.high) || 0, name: '高风险', itemStyle: { color: '#d32f2f' } }
      ])

      const plan = res.data.planStatusDistribution || {}
      renderBarChart(planChart, ['待执行', '执行中', '已完成'], [
        parseInt(plan.pending) || 0,
        parseInt(plan.in_progress) || 0,
        parseInt(plan.completed) || 0
      ])
    }
  } catch { /* ignore */ }

  try {
    const res = await dataCollectionApi.statistics()
    if (res.data) {
      collectionStats.value = res.data
      renderPieChart(sourceChart, '数据来源分布', [
        { value: parseInt(res.data.smartCount) || 0, name: '智能评估', itemStyle: { color: '#1976d2' } },
        { value: parseInt(res.data.questionnaireCount) || 0, name: '健康问询', itemStyle: { color: '#388e3c' } },
        { value: parseInt(res.data.imageCount) || 0, name: '影像报告', itemStyle: { color: '#7b1fa2' } }
      ])
    }
  } catch { /* ignore */ }
}

onMounted(async () => {
  await nextTick()
  riskChart = initChart(riskChartRef.value)
  sourceChart = initChart(sourceChartRef.value)
  planChart = initChart(planChartRef.value)
  loadData()
})

onBeforeUnmount(() => {
  riskChart?.dispose()
  sourceChart?.dispose()
  planChart?.dispose()
  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.stats-row { margin-bottom: 20px; }
.stat-card { cursor: default; }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon {
  width: 52px; height: 52px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-info h4 { font-size: 26px; font-weight: 700; color: #333; margin: 0; }
.stat-info p { font-size: 13px; color: #999; margin: 2px 0 0 0; }
.chart-row { margin-bottom: 20px; }
.card-title { font-size: 16px; font-weight: 600; }
.quick-links { padding: 0; }
.quick-link-card {
  display: flex; align-items: center; gap: 12px; padding: 14px;
  border: 1px solid #eee; border-radius: 8px; margin-bottom: 12px;
  cursor: pointer; transition: all 0.2s;
}
.quick-link-card:hover { border-color: #1a5c7d; background: #f5f9fc; }
.quick-link-card h4 { font-size: 14px; color: #333; margin: 0 0 4px 0; }
.quick-link-card p { font-size: 12px; color: #999; margin: 0; }
</style>
