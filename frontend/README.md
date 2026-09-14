# Sa-Token RBAC 前端

该目录是权限学习平台的 Vue 3 前端，当前包含登录页、管理后台布局、学习看板和基础路由。

## 环境要求

- Node.js 22.18+ 或 24.12+
- npm 10+

## 本地启动

```sh
npm install
npm run dev
```

默认访问地址为 `http://127.0.0.1:5173`，登录页地址为 `/login`。

## 质量检查

```sh
npm run lint
npm run type-check
npm run test:unit -- --run
npm run build
```

首次执行端到端测试前安装 Chromium：

```sh
npx playwright install chromium
npm run test:e2e -- --project=chromium
```

## 目录说明

- `src/layouts`：后台页面公共布局。
- `src/views`：路由页面。
- `src/router`：静态路由，后续接入后端动态菜单。
- `src/styles`：全局视觉样式。
- `src/__tests__`：单元测试。
- `e2e`：浏览器端到端测试。

登录接口尚未接入。当前提交仅建立可运行的前端基础骨架。
