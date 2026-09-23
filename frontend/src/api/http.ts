import axios from 'axios'

import { clearStoredSession, getStoredSession } from '@/stores/auth'

// 统一 HTTP 请求客户端
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const session = getStoredSession()

  if (session?.token) {
    config.headers.Authorization = `${session.tokenType} ${session.token}`
  }

  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearStoredSession()
    }

    return Promise.reject(error)
  },
)
