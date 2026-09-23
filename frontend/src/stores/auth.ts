import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

// 前端登录会话结构
export interface AuthSession {
  token: string
  tokenType: string
  expiresIn: number
  mustChangePassword: boolean
}

export const AUTH_SESSION_KEY = 'satoken_rbac_auth_session'

function parseSession(value: string | null): AuthSession | null {
  if (!value) {
    return null
  }

  try {
    const session = JSON.parse(value) as Partial<AuthSession>
    if (typeof session.token !== 'string' || !session.token) {
      return null
    }

    return {
      token: session.token,
      tokenType: session.tokenType || 'Bearer',
      expiresIn: session.expiresIn || 0,
      mustChangePassword: Boolean(session.mustChangePassword),
    }
  } catch {
    return null
  }
}

// 读取当前浏览器中保存的登录会话
export function getStoredSession(): AuthSession | null {
  return (
    parseSession(localStorage.getItem(AUTH_SESSION_KEY)) ??
    parseSession(sessionStorage.getItem(AUTH_SESSION_KEY))
  )
}

// 清理浏览器中的登录会话
export function clearStoredSession() {
  localStorage.removeItem(AUTH_SESSION_KEY)
  sessionStorage.removeItem(AUTH_SESSION_KEY)
}

export const useAuthStore = defineStore('auth', () => {
  const session = ref<AuthSession | null>(getStoredSession())
  const isAuthenticated = computed(() => Boolean(session.value?.token))

  function signIn(nextSession: AuthSession, rememberMe: boolean) {
    clearStoredSession()

    const storage = rememberMe ? localStorage : sessionStorage
    storage.setItem(AUTH_SESSION_KEY, JSON.stringify(nextSession))
    session.value = nextSession
  }

  function signOut() {
    clearStoredSession()
    session.value = null
  }

  return {
    session,
    isAuthenticated,
    signIn,
    signOut,
  }
})
