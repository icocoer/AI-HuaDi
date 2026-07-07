<template>
  <div class="page">
    <!-- 风险统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e8f5e9; color: #388e3c;">
              <el-icon :size="24"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ riskStats.lowCount || 0 }}</h4>
              <p>低风险</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fff3e0; color: #f57c00;">
              <el-icon :size="24"><WarningFilled /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ riskStats.mediumCount || 0 }}</h4>
              <p>中风险</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ffebee; color: #d32f2f;">
              <el-icon :size="24"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ riskStats.highCount || 0 }}</h4>
              <p>高风险</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f3e5f5; color: #7b1fa2;">
              <el-icon :size="24"><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <h4>{{ riskStats.unreadWarnings || 0 }}</h4>
              <p>未读预警</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速评估 & 预警列表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header><span class="card-title">快速风险评估</span></template>
          <div class="quick-assess">
            <el-select v-model="assessElderId" placeholder="选择老人" filterable style="width: 200px">
              <el-option v-for="e in elderList" :key="e.id" :label="`${e.name}（${e.id}）`" :value="e.id" />
            </el-select>
            <el-button type="primary" @click="doQuickAssess" :loading="assessing" style="margin-left: 10px">
              开始评估
            </el-button>
          </div>
          <div v-if="assessResult.currentLevel" style="margin-top: 20px">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="风险等级">
                <el-tag :type="assessResult.currentLevel === 'high' ? 'danger' : assessResult.currentLevel === 'medium' ? 'warning' : 'success'">
                  {{ assessResult.currentLevel }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="总分">{{ assessResult.totalScore || '-' }}</el-descriptions-item>
              <el-descriptions-item label="评估次数">{{ assessResult.assessmentCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="趋势">
                <el-tag :type="assessResult.trend === 'declining' ? 'danger' : assessResult.trend === 'improving' ? 'success' : 'info'" size="small">
                  {{ assessResult.trendMsg }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">预警消息</span>
              <el-switch v-model="unreadOnly" active-text="仅未读" @change="loadWarnings" />
            </div>
          </template>
          <div class="warning-list">
            <div v-if="warnings.length === 0" style="text-align: center; padding: 40px; color: #999;">
              <el-icon :size="40"><Bell /></el-icon>
              <p>暂无预警消息</p>
            </div>
            <div v-for="item in warnings" :key="item.id" class="warning-item" :class="{ unread: item.isRead === 0 }">
              <div class="warning-header">
                <el-tag :type="item.riskLevel === 'high' ? 'danger' : 'warning'" size="small">
                  {{ item.riskLevel === 'high' ? '高风险' : '中风险' }}
                </el-tag>
                <span class="warning-elder">{{ item.elderName || '老人#' + item.elderId }}</span>
                <span class="warning-time">{{ item.createTime }}</span>
              </div>
              <p class="warning-msg">{{ item.warningMsg }}</p>
              <el-button v-if="item.isRead === 0" size="small" text type="primary" @click="markRead(item.id)">标记已读</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, WarningFilled, CircleClose, Bell } from '@element-plus/icons-vue'
import { riskApi, elderApi } from '../api'

const riskStats = ref({})
const warnings = ref([])
const elderList = ref([])
const unreadOnly = ref(false)
const assessElderId = ref(null)
const assessing = ref(false)
const assessResult = ref({})

const loadStats = async () => {
  try {
    const res = await riskApi.statistics()
    riskStats.value = res.data || {}
  } catch { }
}

const loadWarnings = async () => {
  try {
    const res = await riskApi.warnings(unreadOnly.value)
    warnings.value = res.data || []
  } catch { warnings.value = [] }
}

const doQuickAssess = async () => {
  if (!assessElderId.value) { ElMessage.warning('请选择老人'); return }
  assessing.value = true
  try {
    const res = await riskApi.assess(assessElderId.value)
    assessResult.value = res.data || {}
    loadStats()
  } catch { } finally { assessing.value = false }
}

const loadElders = async () => {
  try {
    const res = await elderApi.list({ pageNum: 1, pageSize: 1000 })
    elderList.value = res.data.list || []
  } catch { elderList.value = [] }
}

onMounted(() => { loadStats(); loadWarnings(); loadElders() })
</script>

<style scoped>
.stats-row { margin-bottom: 0; }
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
.quick-assess { display: flex; align-items: center; }
.warning-list { max-height: 400px; overflow-y: auto; }
.warning-item {
  padding: 12px; border-bottom: 1px solid #eee;
  border-left: 3px solid transparent;
}
.warning-item.unread {
  background: #fff8e1;
  border-left-color: #f57c00;
}
.warning-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.warning-elder { font-weight: 600; color: #333; }
.warning-time { font-size: 12px; color: #999; margin-left: auto; }
.warning-msg { font-size: 13px; color: #666; margin: 0; line-height: 1.5; }
</style>
