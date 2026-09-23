import { http } from './http'

// 后端统一响应结构
export interface ApiResult<T> {
  code: string
  message: string
  data: T
  traceId: string
  timestamp: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  tokenType: string
  expiresIn: number
  mustChangePassword: boolean
}

// 调用用户名密码登录接口
export async function login(
  request: LoginRequest,
): Promise<LoginResponse> {
  const response = await http.post<ApiResult<LoginResponse>>(
    '/auth/login',
    request,
  )

  return response.data.data
}
