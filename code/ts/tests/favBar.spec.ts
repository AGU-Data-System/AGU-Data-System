import { test, expect } from '@playwright/test';

test.describe('FavBar', () => {
    const URL = 'http://localhost:8000';

    test.beforeEach(async ({ page }) => {
        await page.goto(URL);
    });

    test('should display loading state', async ({ page }) => {
        await expect(page.getByText('UAGs Favoritas')).toBeVisible();
        await expect(page.getByRole('heading', { name: 'Loading...' }).first()).toBeVisible();
        await expect(page.getByRole('img').first()).toBeVisible();
    });

    /*test('should display error state', async ({ page }) => {
        // Simulate error state by mocking the API call to return an error.
        await page.route('**!/api/agus/favourites', async (route) => {
            route.fulfill({
                status: 500,
                body: 'Internal Server Error',
            });
        });

        await page.goto(URL);

        await page.waitForSelector('text=Error fetching!');

        await expect(page.locator('text=Error fetching!')).toBeVisible();
        await expect(page.locator('text=UAGs Favoritas')).toBeVisible();
    });*/

    test('should display success state with no items', async ({ page }) => {
        // Simulate success state with no favorite UAGs.
        await page.route('**/api/agus/favourites', async (route) => {
            route.fulfill({
                status: 200,
                body: JSON.stringify([]),
            });
        });

        await page.goto(URL);
        await expect(page.locator('text=UAGs Favoritas').first()).toBeVisible();
        await expect(page.locator('text=Sem UAGs favoritas!')).toBeVisible();
    });

    test('should display success state with items', async ({ page }) => {
        // Simulate success state with favorite UAGs.
        await page.route('**/api/agus/favourites', async (route) => {
            route.fulfill({
                status: 200,
                body: JSON.stringify([
                    { cui: '1', name: 'UAG 1', dno: { name: 'ORD 1' } },
                    { cui: '2', name: 'UAG 2', dno: { name: 'ORD 2' } }
                ]),
            });
        });

        await page.goto(URL);

        await expect(page.locator('text=Nome: UAG 1')).toBeVisible();
        await expect(page.locator('text=Nome: UAG 2')).toBeVisible();
        await expect(page.locator('text=ORD: ORD 1')).toBeVisible();
        await expect(page.locator('text=ORD: ORD 2')).toBeVisible();
        await expect(page.locator('text=UAGs Favoritas')).toBeVisible();
    });

    test('should navigate to UAG details on click', async ({ page }) => {
        // Simulate success state with favorite UAGs.
        await page.route('**/api/agus/favourites', async (route) => {
            route.fulfill({
                status: 200,
                body: JSON.stringify([
                    { cui: '1', name: 'UAG 1', dno: { name: 'ORD 1' } }
                ]),
            });
        });

        await page.goto(URL);

        await page.click('text=Nome: UAG 1');

        await expect(page).toHaveURL(URL + '/uag/1');
    });
});