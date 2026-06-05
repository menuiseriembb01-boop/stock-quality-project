import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// ============================================================
// Configuration du test de charge - Séga Diallo
// Objectif : 50 utilisateurs simultanés pendant 1 minute
// ============================================================
export const options = {
  stages: [
    { duration: '15s', target: 10 },   // Montée progressive à 10 users
    { duration: '30s', target: 50 },   // Pic à 50 users simultanés
    { duration: '15s', target: 0 },    // Descente progressive
  ],
  thresholds: {
    // Seuils de performance à respecter
    'http_req_duration': ['p(95)<2000'],  // 95% des requêtes < 2s
    'http_req_failed': ['rate<0.05'],      // Taux d'erreur < 5%
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  // Test endpoint 1 : calculateRisk
  const riskRes = http.get(`${BASE_URL}/api/v1/risk-index?lvl=25`);
  check(riskRes, {
    'risk-index status 200': (r) => r.status === 200,
    'risk-index réponse valide': (r) => ['Low-A', 'Low-B', 'Medium', 'High'].includes(r.body),
    'risk-index latence < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(0.5);

  // Test endpoint 2 : processData (version optimisée avec limit=100)
  const dataRes = http.get(`${BASE_URL}/api/v1/process-data?limit=100`);
  check(dataRes, {
    'process-data status 200': (r) => r.status === 200,
    'process-data latence < 1000ms': (r) => r.timings.duration < 1000,
  });

  sleep(0.5);
}

// Génération du rapport HTML à la fin du test
export function handleSummary(data) {
  return {
    'k6/results/summary.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}
