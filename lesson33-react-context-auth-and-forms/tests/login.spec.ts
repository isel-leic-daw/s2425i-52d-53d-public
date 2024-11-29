import { test, expect } from '@playwright/test';

test('can login', async ({ page }) => {
  // when: navigating to the home page
  await page.goto('http://localhost:8000/');

  // then: the page has a link to the 'User 123 Game xyz' page
  const authorsLink = page.getByRole('link', {name: 'User 123 Game xyz', exact:true})
  await expect(authorsLink).toBeVisible()

  // when: navigating to the '/users/123/games/xyz' page
  await authorsLink.click()

  // then: the login form appears
  const usernameInput = page.getByLabel("username")
  const passwordInput = page.getByLabel("password")
  const loginButton = page.getByRole('button')
  await expect(usernameInput).toBeVisible()
  await expect(passwordInput).toBeVisible()
  await expect(loginButton).toBeVisible()

  // when: providing incorrect credentials
  await usernameInput.fill("alice")
  await passwordInput.fill("123")
  await loginButton.click()

  // then: the button get disabled and then enabled again 
  await expect(loginButton).toBeDisabled()
  await expect(loginButton).toBeEnabled()

  // and: the error message appears
  await expect(page.getByText("Invalid username or password")).toBeVisible()

  // and: inputs are cleared
  await expect(usernameInput).toHaveValue("")
  await expect(passwordInput).toHaveValue("")

  // when: providing correct credentials
  await usernameInput.fill("alice")
  await passwordInput.fill("1234")
  await loginButton.click()

  // then Redirect to /users/123/games/xyz
  await expect(page.getByText('User Game Detail')).toBeVisible()
});