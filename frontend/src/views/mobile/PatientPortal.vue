<template>
  <div class="patient-portal">
    <!-- 顶部导航 -->
    <div class="mobile-header">
      <div class="header-title">健康助手</div>
      <div class="header-user" @click="showUserMenu = !showUserMenu">
        <span>{{ user.realName || user.username }}</span>
        <el-icon><ArrowDown /></el-icon>
      </div>
      <!-- 用户菜单 -->
      <div class="user-menu" v-if="showUserMenu">
        <div class="menu-item" @click="handleLogout">退出登录</div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="mobile-content">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-number">{{ unreadCount }}</div>
          <div class="stat-label">未读消息</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ healthDataCount }}</div>
          <div class="stat-label">健康记录</div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="menu-grid">
        <div class="menu-item" @click="currentTab = 'notifications'">
          <el-icon :size="32" color="#409EFF"><Bell /></el-icon>
          <span>医生通知</span>
          <el-badge :value="unreadCount" :max="99" v-if="unreadCount > 0" />
        </div>
        <div class="menu-item" @click="currentTab = 'upload'">
          <el-icon :size="32" color="#67C23A"><Upload /></el-icon>
          <span>上传数据</span>
        </div>
        <div class="menu-item" @click="currentTab = 'history'">
          <el-icon :size="32" color="#E6A23C"><List /></el-icon>
          <span>历史记录</span>
        </div>
        <div class="menu-item" @click="currentTab = 'profile'">
          <el-icon :size="32" color="#909399"><User /></el-icon>
          <span>个人信息</span>
        </div>
      </div>

      <!-- 通知列表 -->
      <div v-if="currentTab === 'notifications'" class="section">
        <div class="section-header">
          <h3>医生通知</h3>
          <el-button size="small" @click="markAllRead">全部已读</el-button>
        </div>
        <div class="notification-list">
          <div v-if="notifications.length === 0" class="empty-state">
            <el-icon :size="48" color="#C0C4CC"><Bell /></el-icon>
            <p>暂无通知</p>
          </div>
          <div v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: !item.isRead }" @click="readNotification(item)">
            <div class="notification-header">
              <span class="notification-sender">{{ item.senderName || '系统' }}</span>
              <span class="notification-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="notification-content">{{ item.content }}</div>
          </div>
        </div>
      </div>

      <!-- 上传健康数据 -->
      <div v-if="currentTab === 'upload'" class="section">
        <div class="section-header">
          <h3>上传健康数据</h3>
        </div>
        <el-form :model="uploadForm" label-position="top" class="upload-form">
          <el-form-item label="收缩压(mmHg)">
            <el-input-number v-model="uploadForm.systolic" :min="60" :max="250" style="width: 100%" />
          </el-form-item>
          <el-form-item label="舒张压(mmHg)">
            <el-input-number v-model="uploadForm.diastolic" :min="40" :max="150" style="width: 100%" />
          </el-form-item>
          <el-form-item label="空腹血糖(mmol/L)">
            <el-input-number v-model="uploadForm.bloodSugar" :min="2" :max="30" :precision="1" :step="0.1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="测量时间">
            <el-select v-model="uploadForm.measureTime" placeholder="请选择" style="width: 100%">
              <el-option label="空腹" value="fasting" />
              <el-option label="餐后2小时" value="after_meal" />
              <el-option label="随机" value="random" />
            </el-select>
          </el-form-item>
          <el-form-item label="心率(bpm)">
            <el-input-number v-model="uploadForm.heartRate" :min="40" :max="200" style="width: 100%" />
          </el-form-item>
          <el-form-item label="体重(kg)">
            <el-input-number v-model="uploadForm.weight" :min="20" :max="200" :precision="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="症状描述">
            <el-input v-model="uploadForm.symptomDesc" type="textarea" :rows="3" placeholder="请描述您的症状（选填）" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="uploadForm.remark" type="textarea" :rows="2" placeholder="补充说明（选填）" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitHealthData" :loading="submitting" style="width: 100%">提交数据</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 历史记录 -->
      <div v-if="currentTab === 'history'" class="section">
        <div class="section-header">
          <h3>健康数据历史</h3>
        </div>
        <div class="history-list">
          <div v-if="healthHistory.length === 0" class="empty-state">
            <el-icon :size="48" color="#C0C4CC"><List /></el-icon>
            <p>暂无记录</p>
          </div>
          <div v-for="item in healthHistory" :key="item.id" class="history-item">
            <div class="history-header">
              <span class="history-type">{{ getTypeLabel(item.dataType) }}</span>
              <span class="history-time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="history-content">{{ item.dataContent }}</div>
            <div class="history-remark" v-if="item.remark">备注：{{ item.remark }}</div>
          </div>
        </div>
      </div>

      <!-- 个人信息 -->
      <div v-if="currentTab === 'profile'" class="section">
        <div class="section-header">
          <h3>个人信息</h3>
        </div>
        <div class="profile-info">
          <div class="info-item">
            <span class="info-label">姓名</span>
            <span class="info-value">{{ elderInfo.name || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">性别</span>
            <span class="info-value">{{ elderInfo.gender === 'M' ? '男' : '女' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">年龄</span>
            <span class="info-value">{{ calculateAge(elderInfo.birthDate) }}岁</span>
          </div>
          <div class="info-item">
            <span class="info-label">电话</span>
            <span class="info-value">{{ elderInfo.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">风险等级</span>
            <span class="info-value">
              <el-tag :type="elderInfo.riskLevel === 'high' ? 'danger' : elderInfo.riskLevel === 'medium' ? 'warning' : 'success'" size="small">
                {{ elderInfo.riskLevel === 'high' ? '高风险' : elderInfo.riskLevel === 'medium' ? '中风险' : '低风险' }}
              </el-tag>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">地址</span>
            <span class="info-value">{{ elderInfo.address || '-' }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Upload, List, User, ArrowDown } from '@element-plus/icons-vue'
import { homecareApi, elderApi, healthQuestionnaireApi, dataCollectionApi } from '../../api'

const router = useRouter()
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const showUserMenu = ref(false)
const currentTab = ref('notifications')
const submitting = ref(false)

// 通知相关
const notifications = ref([])
const unreadCount = ref(0)

// 健康数据相关
const healthDataCount = ref(0)
const healthHistory = ref([])

// 老人信息
const elderInfo = ref({})

// 上传表单
const uploadForm = ref({
  systolic: null,
  diastolic: null,
  bloodSugar: null,
  measureTime: '',
  heartRate: null,
  weight: null,
  symptomDesc: '',
  remark: ''
})

// 加载通知
const loadNotifications = async () => {
  try {
    const res = await homecareApi.messageList({ receiverId: user.value.id, pageNum: 1, pageSize: 50 })
    notifications.value = res.data.list || []
    unreadCount.value = notifications.value.filter(m => !m.isRead).length
  } catch { }
}

// 标记所有已读
const markAllRead = async () => {
  try {
    const unreadItems = notifications.value.filter(m => !m.isRead)
    for (const item of unreadItems) {
      await homecareApi.markMessageRead(item.id)
    }
    await loadNotifications()
    ElMessage.success('已全部标记为已读')
  } catch { }
}

// 阅读通知
const readNotification = async (item) => {
  if (!item.isRead) {
    try {
      await homecareApi.markMessageRead(item.id)
      item.isRead = 1
      unreadCount.value = notifications.value.filter(m => !m.isRead).length
    } catch { }
  }
}

// 提交健康数据
const submitHealthData = async () => {
  const parts = []
  if (uploadForm.value.systolic && uploadForm.value.diastolic) {
    parts.push(`血压${uploadForm.value.systolic}/${uploadForm.value.diastolic}mmHg`)
  }
  if (uploadForm.value.bloodSugar) {
    const timeLabel = uploadForm.value.measureTime === 'fasting' ? '空腹' : uploadForm.value.measureTime === 'after_meal' ? '餐后2小时' : ''
    parts.push(`${timeLabel}血糖${uploadForm.value.bloodSugar}mmol/L`)
  }
  if (uploadForm.value.heartRate) {
    parts.push(`心率${uploadForm.value.heartRate}bpm`)
  }
  if (uploadForm.value.weight) {
    parts.push(`体重${uploadForm.value.weight}kg`)
  }
  if (uploadForm.value.symptomDesc) {
    parts.push(uploadForm.value.symptomDesc)
  }

  if (parts.length === 0) {
    ElMessage.warning('请至少填写一项健康数据')
    return
  }

  const dataContent = parts.join('，')

  submitting.value = true
  try {
    await healthQuestionnaireApi.add({
      elderId: user.value.elderId || user.value.id,
      questionnaireType: 'BODY_CHECK',
      summary: dataContent,
      surveyor: user.value.realName || user.value.username,
      surveyTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      remark: uploadForm.value.remark
    })
    ElMessage.success('数据上传成功')
    resetUploadForm()
    await loadHealthHistory()
  } catch {
    ElMessage.error('上传失败')
  } finally {
    submitting.value = false
  }
}

// 重置上传表单
const resetUploadForm = () => {
  uploadForm.value = {
    systolic: null,
    diastolic: null,
    bloodSugar: null,
    measureTime: '',
    heartRate: null,
    weight: null,
    symptomDesc: '',
    remark: ''
  }
}

// 加载健康历史
const loadHealthHistory = async () => {
  try {
    const res = await dataCollectionApi.list({ elderId: user.value.elderId || user.value.id, pageNum: 1, pageSize: 20 })
    healthHistory.value = res.data.list || []
    healthDataCount.value = res.data.total || 0
  } catch { }
}

// 加载老人信息
const loadElderInfo = async () => {
  try {
    const elderId = user.value.elderId || user.value.id
    if (elderId) {
      const res = await elderApi.getById(elderId)
      elderInfo.value = res.data || {}
    }
  } catch { }
}

// 获取类型标签
const getTypeLabel = (type) => {
  const map = {
    'blood_pressure': '血压测量',
    'blood_sugar': '血糖测量',
    'heart_rate': '心率记录',
    'weight': '体重记录',
    'symptom': '症状描述',
    'other': '其他',
    'BODY_CHECK': '体检数据',
    'MEDICAL_HISTORY': '病史问询',
    'LIFESTYLE': '生活方式',
    'SYMPTOM_CHECK': '症状检查',
    'FAMILY_HISTORY': '家族史'
  }
  return map[type] || type
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`

  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 计算年龄
const calculateAge = (birthDate) => {
  if (!birthDate) return '-'
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  return age
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  location.replace('/login')
}

onMounted(() => {
  loadNotifications()
  loadHealthHistory()
  loadElderInfo()
})
</script>

<style scoped>
.patient-portal {
  max-width: 480px;
  margin: 0 auto;
  min-height: 100vh;
  background: #f5f7fa;
}

.mobile-header {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  font-size: 14px;
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.user-menu .menu-item {
  padding: 12px 20px;
  cursor: pointer;
  color: #333;
}

.user-menu .menu-item:hover {
  background: #f5f7fa;
}

.mobile-content {
  padding: 15px;
}

.stats-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.menu-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 20px;
}

.menu-grid .menu-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
  position: relative;
}

.menu-grid .menu-item:hover {
  transform: translateY(-2px);
}

.menu-grid .menu-item span {
  font-size: 14px;
  color: #333;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.notification-list, .history-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item, .history-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
}

.notification-item:hover, .history-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #ecf5ff;
  border-left: 3px solid #409EFF;
}

.notification-header, .history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-sender, .history-type {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.notification-time, .history-time {
  font-size: 12px;
  color: #909399;
}

.notification-content, .history-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.history-remark {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.upload-form {
  padding: 10px 0;
}

.profile-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-label {
  font-size: 14px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state p {
  margin-top: 10px;
  font-size: 14px;
}
</style>
