import { test, expect } from '@playwright/test';

test.describe('NavBar', () => {
    const URL = 'http://localhost:8000';

    test.beforeEach(async ({ page }) => {
        await page.goto(URL);
    });

    test('should display current date and time', async ({ page }) => {
        const date = new Date();
        const formattedDate = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
        const formattedTime = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;

        const dateText = await page.textContent('text=Data:');
        const timeText = await page.textContent('text=Hora:');

        expect(dateText).toContain(formattedDate);
        expect(timeText).toContain(formattedTime);
    });

    test('should display the title', async ({ page }) => {
        const title = await page.textContent('text=Planeamento & Controlo GL UAGs');
        expect(title).toBe('Planeamento & Controlo GL UAGs');
    });

    test('should toggle theme', async ({ page }) => {
        const switchSelector = 'input[type="checkbox"]';

        const initialSwitchState = await page.isChecked(switchSelector);

        await page.click(switchSelector);

        const toggledSwitchState = await page.isChecked(switchSelector);

        expect(toggledSwitchState).not.toBe(initialSwitchState);
    });
});