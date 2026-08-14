import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

let loadingInstance = null
let loadingCount = 0

const showLoading = () => {
  if (loadingCount === 0) {
    loadingInstance = ElLoading.service({ fullscreen: true, text: '加载中...' })
  }
  loadingCount++
}

const hideLoading = () => {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  }
}

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  if (config.method !== 'get') {
    showLoading()
  }
  return config
})

request.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data
    if (res.code == null && res.status != null) {
      ElMessage.error(res.error || res.message || '服务器错误')
      return Promise.reject(new Error(res.error || '服务器错误'))
    }
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    hideLoading()
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
        return Promise.reject(error)
      }
      const msg = error.response.data?.message || error.response.data?.error || '请求失败'
      ElMessage.error(msg)
    } else {
      ElMessage.error('网络错误，请检查后端服务是否启动')
    }
    return Promise.reject(error)
  }
)

// ========== 主数据采集 API ==========
export const dataCollectionApi = {
  list(params) { return request.get('/data/collection/list', { params }) },
  getById(id) { return request.get(`/data/collection/${id}`) },
  add(data) { return request.post('/data/collection/add', data) },
  update(data) { return request.put('/data/collection/update', data) },
  delete(id) { return request.delete(`/data/collection/delete/${id}`) },
  statistics() { return request.get('/data/collection/statistics') },
  getDataSourceEnums() { return request.get('/data/collection/datasource/enums') },
  getDataTypeEnums() { return request.get('/data/collection/datatype/enums') }
}

// ========== 智能评估 API ==========
export const smartAssessmentApi = {
  list(params) { return request.get('/data/smart/list', { params }) },
  getById(id) { return request.get(`/data/smart/${id}`) },
  add(data) { return request.post('/data/smart/add', data) },
  update(data) { return request.put('/data/smart/update', data) },
  delete(id) { return request.delete(`/data/smart/delete/${id}`) },
  getLatest(elderId) { return request.get(`/data/smart/latest/${elderId}`) }
}

// ========== 健康问询 API ==========
export const healthQuestionnaireApi = {
  list(params) { return request.get('/data/questionnaire/list', { params }) },
  getById(id) { return request.get(`/data/questionnaire/${id}`) },
  add(data) { return request.post('/data/questionnaire/add', data) },
  update(data) { return request.put('/data/questionnaire/update', data) },
  delete(id) { return request.delete(`/data/questionnaire/delete/${id}`) }
}

// ========== 影像报告 API ==========
export const imageReportApi = {
  list(params) { return request.get('/data/image/list', { params }) },
  getById(id) { return request.get(`/data/image/${id}`) },
  add(data) { return request.post('/data/image/add', data) },
  update(data) { return request.put('/data/image/update', data) },
  delete(id) { return request.delete(`/data/image/delete/${id}`) },
  count(elderId) { return request.get(`/data/image/count/${elderId}`) }
}

// ========== 认证 API ==========
export const authApi = {
  login(data) { return request.post('/user/login', data) },
  register(data) { return request.post('/user/register', data) }
}

// ========== 用户管理 API ==========
export const userApi = {
  list(params) { return request.get('/user/list', { params }) },
  getById(id) { return request.get(`/user/${id}`) },
  add(data) { return request.post('/user/add', data) },
  update(data) { return request.put('/user/update', data) },
  delete(id) { return request.delete(`/user/delete/${id}`) }
}

// ========== 老人健康档案 API ==========
export const elderApi = {
  list(params) { return request.get('/elder/list', { params }) },
  getById(id) { return request.get(`/elder/${id}`) },
  add(data) { return request.post('/elder/add', data) },
  update(data) { return request.put('/elder/update', data) },
  delete(id) { return request.delete(`/elder/delete/${id}`) },
  statistics() { return request.get('/elder/statistics') },
  fullProfile(id) { return request.get(`/elder/${id}/full-profile`) }
}

// ========== 认知评估 API ==========
export const assessmentApi = {
  list(params) { return request.get('/assessment/list', { params }) },
  getById(id) { return request.get(`/assessment/${id}`) },
  add(data) { return request.post('/assessment/add', data) },
  update(data) { return request.put('/assessment/update', data) },
  delete(id) { return request.delete(`/assessment/delete/${id}`) },
  assess(elderId) { return request.get(`/assessment/assess/${elderId}`) }
}

// ========== 干预计划 API ==========
export const interventionPlanApi = {
  list(params) { return request.get('/intervention/plan/list', { params }) },
  getById(id) { return request.get(`/intervention/plan/${id}`) },
  add(data) { return request.post('/intervention/plan/add', data) },
  update(data) { return request.put('/intervention/plan/update', data) },
  delete(id) { return request.delete(`/intervention/plan/delete/${id}`) },
  statistics() { return request.get('/intervention/statistics') },
  elderPlans(elderId) { return request.get(`/intervention/elder/${elderId}/plans`) }
}

// ========== 干预执行 API ==========
export const interventionExecutionApi = {
  list(params) { return request.get('/intervention/execution/list', { params }) },
  getById(id) { return request.get(`/intervention/execution/${id}`) },
  add(data) { return request.post('/intervention/execution/add', data) },
  update(data) { return request.put('/intervention/execution/update', data) },
  delete(id) { return request.delete(`/intervention/execution/delete/${id}`) }
}

// ========== 风险评估预警 API ==========
export const riskApi = {
  assessmentList(params) { return request.get('/risk/assessment/list', { params }) },
  assess(elderId) { return request.get(`/risk/assess/${elderId}`) },
  warnings(unreadOnly) { return request.get('/risk/warnings', { params: { unreadOnly } }) },
  markRead(id) { return request.put(`/risk/warnings/${id}/read`) },
  unreadCount() { return request.get('/risk/warnings/unread-count') },
  statistics() { return request.get('/risk/statistics') }
}

// ========== 效果分析 API ==========
export const analysisApi = {
  dashboard() { return request.get('/analysis/dashboard') },
  riskDistribution() { return request.get('/analysis/risk-distribution') }
}

// ========== 系统管理 API ==========
export const systemApi = {
  dictTypes() { return request.get('/system/dict/types') },
  dictByType(type) { return request.get(`/system/dict/${type}`) },
  dictById(id) { return request.get(`/system/dict/item/${id}`) },
  addDict(data) { return request.post('/system/dict/add', data) },
  updateDict(data) { return request.put('/system/dict/update', data) },
  deleteDict(id) { return request.delete(`/system/dict/delete/${id}`) },
  statistics() { return request.get('/system/statistics') }
}

// ========== 家庭健康助手 API ==========
export const homecareApi = {
  // 随访计划
  visitPlanList(params) { return request.get('/homecare/visit-plan/list', { params }) },
  visitPlanById(id) { return request.get(`/homecare/visit-plan/${id}`) },
  addVisitPlan(data) { return request.post('/homecare/visit-plan/add', data) },
  updateVisitPlan(data) { return request.put('/homecare/visit-plan/update', data) },
  deleteVisitPlan(id) { return request.delete(`/homecare/visit-plan/delete/${id}`) },

  // 随访记录
  visitRecordList(params) { return request.get('/homecare/visit-record/list', { params }) },
  addVisitRecord(data) { return request.post('/homecare/visit-record/add', data) },

  // 健康预警
  healthAlertList(params) { return request.get('/homecare/health-alert/list', { params }) },
  unreadAlertCount() { return request.get('/homecare/health-alert/unread-count') },
  markAlertRead(id) { return request.put(`/homecare/health-alert/${id}/read`) },

  // 站内消息
  messageList(params) { return request.get('/homecare/message/list', { params }) },
  unreadMessageCount(receiverId) { return request.get('/homecare/message/unread-count', { params: { receiverId } }) },
  markMessageRead(id) { return request.put(`/homecare/message/${id}/read`) },
  sendMessage(data) { return request.post('/homecare/message/send', data) },

  // AI健康分析
  runAiAnalysis(elderId) { return request.post('/homecare/ai/analyze', null, { params: { elderId } }) }
}

export default request
