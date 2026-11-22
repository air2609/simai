import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('simai_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    // Backend membungkus respons dalam ApiResponse { success, message, data }
    const body = response.data
    if (body && body.success === false) {
      ElMessage.error(body.message || 'Terjadi kesalahan')
      return Promise.reject(new Error(body.message))
    }
    return body?.data ?? body
  },
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || 'Terjadi kesalahan'

    if (status === 401) {
      localStorage.removeItem('simai_token')
      localStorage.removeItem('simai_user')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

export default http
