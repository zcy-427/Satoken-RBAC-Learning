import { test, expect } from '@playwright/test'

// Login page end-to-end smoke test.
test('shows the login page', async ({ page }) => {
  await page.goto('/login')
  await expect(page.getByRole('heading', { name: '登录到权限学习平台' })).toBeVisible()
})
