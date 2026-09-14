import { mount } from '@vue/test-utils'
import { createMemoryHistory, createRouter } from 'vue-router'
import { describe, expect, it } from 'vitest'
import App from '../App.vue'

// Root component routing smoke test.
describe('App', () => {
  it('renders the active route', async () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes: [{ path: '/', component: { template: '<h1>权限学习平台</h1>' } }],
    })
    await router.push('/')
    await router.isReady()

    const wrapper = mount(App, { global: { plugins: [router] } })
    expect(wrapper.text()).toContain('权限学习平台')
  })
})
