import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'

import {
  AUTH_SESSION_KEY,
  useAuthStore,
  type AuthSession,
} from '@/stores/auth'

// 登录会话状态单元测试
describe('auth store', () => {
  const session: AuthSession = {
    token: 'test-token',
    tokenType: 'Bearer',
    expiresIn: 604800,
    mustChangePassword: true,
  }

  beforeEach(() => {
    localStorage.clear()
    sessionStorage.clear()
    setActivePinia(createPinia())
  })

  it('stores a remembered session in local storage', () => {
    const authStore = useAuthStore()

    authStore.signIn(session, true)

    expect(authStore.isAuthenticated).toBe(true)
    expect(JSON.parse(localStorage.getItem(AUTH_SESSION_KEY)!)).toEqual(session)
    expect(sessionStorage.getItem(AUTH_SESSION_KEY)).toBeNull()
  })

  it('stores a temporary session in session storage', () => {
    const authStore = useAuthStore()

    authStore.signIn(session, false)

    expect(JSON.parse(sessionStorage.getItem(AUTH_SESSION_KEY)!)).toEqual(session)
    expect(localStorage.getItem(AUTH_SESSION_KEY)).toBeNull()
  })

  it('clears the session when signing out', () => {
    const authStore = useAuthStore()
    authStore.signIn(session, true)

    authStore.signOut()

    expect(authStore.isAuthenticated).toBe(false)
    expect(localStorage.getItem(AUTH_SESSION_KEY)).toBeNull()
    expect(sessionStorage.getItem(AUTH_SESSION_KEY)).toBeNull()
  })
})
