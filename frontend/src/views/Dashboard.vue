<template>
  <div class="dashboard">
    <!-- 核心统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
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
      <el-col :span="6">
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
      <el-col :span="6">
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
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ffebee; color: #d32f2f;">
              <el-icon :size="24"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ stats.unreadWarnings || 0 }}</h4>
              <p>未读预警</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据采集统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f3e5f5; color: #7b1fa2;">
              <el-icon :size="24"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ collectionStats.totalCount || 0 }}</h4>
              <p>总采集记录</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e8f5e9; color: #388e3c;">
              <el-icon :size="24"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ collectionStats.todayCount || 0 }}</h4>
              <p>今日新增</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fff3e0; color: #f57c00;">
              <el-icon :size="24"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ collectionStats.weekCount || 0 }}</h4>
              <p>本周采集</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e3f2fd; color: #1976d2;">
              <el-icon :size="24"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ collectionStats.monthCount || 0 }}</h4>
              <p>本月采集</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 风险分布 & 数据来源 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">风险等级分布</span></template>
          <div class="chart-container">
            <div class="source-item" v-for="item in riskData" :key="item.name">
              <span class="source-label">{{ item.name }}</span>
              <el-progress :percentage="item.percent" :color="item.color" :stroke-width="18" />
              <span class="source-value">{{ item.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">数据来源分布</span></template>
          <div class="chart-container">
            <div class="source-item" v-for="item in sourceData" :key="item.name">
              <span class="source-label">{{ item.name }}</span>
              <el-progress :percentage="item.percent" :color="item.color" :stroke-width="18" />
              <span class="source-value">{{ item.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 干预计划状态 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span class="card-title">干预计划状态分布</span></template>
          <div class="chart-container">
            <div class="source-item" v-for="item in planData" :key="item.name">
              <span class="source-label">{{ item.name }}</span>
              <el-progress :percentage="item.percent" :color="item.color" :stroke-width="18" />
              <span class="source-value">{{ item.value }}</span>
            </div>
          </div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User, Document, Clock, DataAnalysis, List, Bell, TrendCharts,
  Cpu, ChatLineSquare, Picture, EditPen, Warning
} from '@element-plus/icons-vue'
import { analysisApi, dataCollectionApi } from '../api'

const router = useRouter()

const stats = ref({})
const collectionStats = ref({})
const riskData = ref([])
const sourceData = ref([])
const planData = ref([])

const quickActions = [
  { label: '老人档案', desc: '管理老人健康档案', icon: Document, color: '#1976d2', path: '/elder-list' },
  { label: '认知评估', desc: '执行认知功能评估', icon: EditPen, color: '#388e3c', path: '/assessment-list' },
  { label: '干预计划', desc: '制定个性化干预方案', icon: List, color: '#f57c00', path: '/intervention-plan' },
  { label: '风险评估', desc: '查看风险预警信息', icon: Warning, color: '#d32f2f', path: '/risk-warning' },
  { label: '数据采集', desc: '智能评估/问询/影像', icon: DataAnalysis, color: '#7b1fa2', path: '/data-list' },
  { label: '系统管理', desc: '用户与字典管理', icon: User, color: '#1976d2', path: '/system' }
]

const loadComprehensiveStats = async () => {
  try {
    const res = await analysisApi.dashboard()
    if (res.data) {
      stats.value = res.data
      const dist = res.data.riskDistribution || {}
      const low = parseInt(dist.low) || 0
      const medium = parseInt(dist.medium) || 0
      const high = parseInt(dist.high) || 0
      const total = low + medium + high
      riskData.value = [
        { name: '低风险', value: low, percent: total > 0 ? Math.round(low / total * 100) : 0, color: '#388e3c' },
        { name: '中风险', value: medium, percent: total > 0 ? Math.round(medium / total * 100) : 0, color: '#f57c00' },
        { name: '高风险', value: high, percent: total > 0 ? Math.round(high / total * 100) : 0, color: '#d32f2f' }
      ]
      const plan = res.data.planStatusDistribution || {}
      const pending = parseInt(plan.pending) || 0
      const inProgress = parseInt(plan.in_progress) || 0
      const completed = parseInt(plan.completed) || 0
      const planTotal = pending + inProgress + completed
      planData.value = [
        { name: '待执行', value: pending, percent: planTotal > 0 ? Math.round(pending / planTotal * 100) : 0, color: '#909399' },
        { name: '执行中', value: inProgress, percent: planTotal > 0 ? Math.round(inProgress / planTotal * 100) : 0, color: '#f57c00' },
        { name: '已完成', value: completed, percent: planTotal > 0 ? Math.round(completed / planTotal * 100) : 0, color: '#388e3c' }
      ]
    }
  } catch {
    // Fallback mock data
    riskData.value = [
      { name: '低风险', value: 45, percent: 45, color: '#388e3c' },
      { name: '中风险', value: 32, percent: 32, color: '#f57c00' },
      { name: '高风险', value: 23, percent: 23, color: '#d32f2f' }
    ]
    planData.value = [
      { name: '待执行', value: 10, percent: 25, color: '#909399' },
      { name: '执行中', value: 18, percent: 45, color: '#f57c00' },
      { name: '已完成', value: 12, percent: 30, color: '#388e3c' }
    ]
  }
}

const loadCollectionStats = async () => {
  try {
    const res = await dataCollectionApi.statistics()
    if (res.data) {
      collectionStats.value = res.data
      const smartCount = parseInt(res.data.smartCount) || 0
      const questionnaireCount = parseInt(res.data.questionnaireCount) || 0
      const imageCount = parseInt(res.data.imageCount) || 0
      const total = smartCount + questionnaireCount + imageCount
      sourceData.value = [
        { name: '智能评估', value: smartCount, percent: total > 0 ? Math.round(smartCount / total * 100) : 0, color: '#1976d2' },
        { name: '健康问询', value: questionnaireCount, percent: total > 0 ? Math.round(questionnaireCount / total * 100) : 0, color: '#388e3c' },
        { name: '影像报告', value: imageCount, percent: total > 0 ? Math.round(imageCount / total * 100) : 0, color: '#7b1fa2' }
      ]
    }
  } catch {
    sourceData.value = [
      { name: '智能评估', value: 180, percent: 43, color: '#1976d2' },
      { name: '健康问询', value: 142, percent: 33, color: '#388e3c' },
      { name: '影像报告', value: 101, percent: 24, color: '#7b1fa2' }
    ]
  }
}

onMounted(() => { loadComprehensiveStats(); loadCollectionStats() })
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
.chart-container { padding: 10px 0; }
.source-item { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.source-label { width: 70px; font-size: 13px; color: #666; text-align: right; flex-shrink: 0; }
.source-value { width: 40px; font-size: 13px; color: #333; font-weight: 600; text-align: right; flex-shrink: 0; }
.source-item :deep(.el-progress) { flex: 1; }
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
