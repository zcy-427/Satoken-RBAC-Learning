<script setup lang="ts">
import { Lock, User } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

// Login page model; API integration will replace the temporary preview behavior.
const router = useRouter()
const submitting = ref(false)
const form = reactive({
  username: '',
  password: '',
  rememberMe: false,
})

async function handleLogin() {
  submitting.value = true
  await router.push('/')
  submitting.value = false
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
          <p>前端骨架已就绪，登录接口将在后端认证模块完成后接入。</p>
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

        <p class="preview-note">当前为前端预览模式，暂不校验账号。</p>
      </div>
    </section>
  </main>
</template>
