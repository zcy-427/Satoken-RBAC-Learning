<script setup lang="ts">
import { ArrowRight } from '@element-plus/icons-vue'

// Learning dashboard content shown before real API data is connected.
const modules = [
  { step: '01', title: '认证与会话', detail: '登录、注销、多设备会话与踢人下线', tone: 'blue' },
  { step: '02', title: 'RBAC 权限', detail: '用户、角色、菜单、按钮与接口权限', tone: 'amber' },
  { step: '03', title: '数据权限', detail: '本人、部门、部门树与自定义范围', tone: 'green' },
  { step: '04', title: '项目任务', detail: '在真实协作场景中验证权限边界', tone: 'violet' },
]
</script>

<template>
  <div class="dashboard">
    <section class="hero-card">
      <div>
        <span class="eyebrow">PROJECT FOUNDATION</span>
        <h2>先理解边界，再编写权限代码</h2>
        <p>当前前端骨架已经建立。下一步从 Spring Boot 项目和第一个健康检查接口开始。</p>
      </div>
      <div class="progress-ring">
        <strong>1</strong>
        <span>/ 6 阶段</span>
      </div>
    </section>

    <section class="module-grid">
      <article
        v-for="module in modules"
        :key="module.step"
        class="module-card"
        :class="module.tone"
      >
        <div class="module-index">{{ module.step }}</div>
        <h3>{{ module.title }}</h3>
        <p>{{ module.detail }}</p>
        <div class="module-action">
          <span>查看学习目标</span>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </article>
    </section>

    <section class="status-grid">
      <article class="status-card primary">
        <span class="eyebrow">CURRENT GOAL</span>
        <h3>建立前后端项目骨架</h3>
        <p>统一目录、开发环境和最小可运行入口，为后续权限功能提供稳定基础。</p>
        <div class="task-checklist">
          <span class="done">✓ Vue 3 + TypeScript</span>
          <span class="done">✓ Element Plus 与路由</span>
          <span>○ Spring Boot + Maven Wrapper</span>
          <span>○ 健康检查接口</span>
        </div>
      </article>

      <article class="status-card">
        <span class="eyebrow">WHY THIS ORDER</span>
        <h3>每一步都能独立验证</h3>
        <p>功能拆小、完成即测试、通过即提交。Git 历史将成为整个学习过程的清晰记录。</p>
        <div class="commit-example">
          <span>git commit</span>
          <code>feat: scaffold project foundation</code>
        </div>
      </article>
    </section>
  </div>
</template>
