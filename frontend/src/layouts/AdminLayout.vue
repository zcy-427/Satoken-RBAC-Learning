<script setup lang="ts">
import {
  Collection,
  FolderOpened,
  House,
  Lock,
  OfficeBuilding,
  Tickets,
  User,
} from '@element-plus/icons-vue'
import { RouterView, useRoute } from 'vue-router'

// Shared application shell for authenticated administration pages.
const route = useRoute()

const navigationItems = [
  { label: '学习概览', icon: House, active: true },
  { label: '用户管理', icon: User },
  { label: '角色权限', icon: Lock },
  { label: '部门岗位', icon: OfficeBuilding },
  { label: '项目管理', icon: FolderOpened },
  { label: '任务管理', icon: Tickets },
  { label: '学习笔记', icon: Collection },
]
</script>

<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">S</div>
        <div>
          <strong>Sa-Token RBAC</strong>
          <span>权限学习平台</span>
        </div>
      </div>

      <nav class="side-nav" aria-label="主导航">
        <button
          v-for="item in navigationItems"
          :key="item.label"
          class="nav-item"
          :class="{ active: item.active }"
          type="button"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
          <span v-if="!item.active" class="coming-soon">待开发</span>
        </button>
      </nav>

      <div class="sidebar-note">
        <span class="status-dot"></span>
        <div>
          <strong>当前阶段</strong>
          <span>项目基础骨架</span>
        </div>
      </div>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div>
          <span class="eyebrow">LEARNING WORKSPACE</span>
          <h1>{{ route.meta.title }}</h1>
        </div>
        <div class="topbar-actions">
          <el-tag effect="plain" round>开发环境</el-tag>
          <div class="user-chip">
            <el-avatar :size="34">A</el-avatar>
            <div>
              <strong>Admin</strong>
              <span>超级管理员</span>
            </div>
          </div>
        </div>
      </header>

      <section class="page-content">
        <RouterView />
      </section>
    </main>
  </div>
</template>
