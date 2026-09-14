<script setup lang="ts">
import { useRouter } from 'vue-router'

// Fallback page for unknown frontend routes.
const router = useRouter()
</script>

<template>
  <main class="not-found-page">
    <span>404</span>
    <h1>页面不存在</h1>
    <p>该功能可能尚未开放，或者访问地址有误。</p>
    <el-button type="primary" @click="router.push('/')">返回学习概览</el-button>
  </main>
</template>
