<script setup lang="ts">
import { Lock, User } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const submitting = ref(false)
const form = reactive({
  username: '',
  password: '',
  rememberMe: false,
})

async function handleLogin() {
  if (!form.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  if (form.password.length < 8) {
    ElMessage.warning('密码长度不能少于8个字符')
    return
  }

  submitting.value = true

  try {
    const session = await login({
      username: form.username.trim(),
      password: form.password,
    })

    authStore.signIn(session, form.rememberMe)

    if (session.mustChangePassword) {
      ElMessage.warning('首次登录需要修改密码')
    } else {
      ElMessage.success('登录成功')
    }

    const redirectPath =
      typeof route.query.redirect === 'string' &&
      route.query.redirect.startsWith('/')
        ? route.query.redirect
        : '/'

    await router.push(redirectPath)
  } catch (error) {
    const message = axios.isAxiosError(error)
      ? error.response?.data?.message
      : undefined
    ElMessage.error(message || '登录失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="login-intro">
      <div class="intro-badge">RBAC LEARNING PROJECT</div>
      <h1>把权限模型<br />真正跑起来</h1>
      <p>从登录会话到数据权限，用一个完整但克制的项目理解 Sa-Token 与 RBAC。</p>
      <div class="learning-points">
        <span>01 · 会话认证</span>
        <span>02 · 角色授权</span>
        <span>03 · 数据范围</span>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <div class="brand compact">
          <div class="brand-mark">S</div>
          <div>
            <strong>Sa-Token RBAC</strong>
            <span>权限学习平台</span>
          </div>
        </div>

        <div class="login-heading">
          <span>WELCOME BACK</span>
          <h2>登录到权限学习平台</h2>
          <p>使用用户名和密码登录，开始权限模型学习。</p>
        </div>

        <el-form :model="form" label-position="top" @submit.prevent="handleLogin">
          <el-form-item label="用户名">
            <el-input
              v-model="form.username"
              size="large"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              placeholder="请输入密码"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>
          <div class="login-options">
            <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
            <span>登录问题请联系管理员</span>
          </div>
          <el-button
            class="login-submit"
            type="primary"
            size="large"
            native-type="submit"
            :loading="submitting"
          >
            进入学习平台
          </el-button>
        </el-form>

        <p class="preview-note">登录状态由 Sa-Token 会话统一管理。</p>
      </div>
    </section>
  </main>
</template>
