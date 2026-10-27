import { defineConfig } from '@playwright/test';

/**
 * E2E caixa-preta: assume a stack inteira rodando (infra + 5 microservices).
 * Nao usa browser — so o cliente HTTP do Playwright (APIRequestContext).
 */
export default defineConfig({
  testDir: './tests',
  timeout: 180_000,          // o ciclo passa por 5 servicos + Kafka
  expect: { timeout: 60_000 },
  fullyParallel: false,
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],
});
