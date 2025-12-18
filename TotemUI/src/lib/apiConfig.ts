// ==============================================
// API Configuration - Multi-Backend URLs
// ==============================================
// LunaTotem usa 3 backends diferentes:
// 1. LunaCore - Autenticação centralizada
// 2. LunaTotem API - Dados do totem (pacientes, agendas, etc)
// 3. LunaPay - Processamento de pagamentos

function normalizeBaseUrl(input?: string): string {
  let url = (input || '').trim();
  if (!url) return '';
  // Add protocol if missing
  if (!/^https?:\/\//i.test(url)) {
    url = 'https://' + url;
  }
  // Remove trailing slash
  url = url.replace(/\/$/, '');
  return url;
}

// 🔐 LunaCore - Autenticação centralizada
export const LUNACORE_URL = normalizeBaseUrl(process.env.NEXT_PUBLIC_LUNACORE_URL) 
  || 'http://localhost:8080';

// 🏥 LunaTotem API - Backend principal
export const TOTEM_API_URL = normalizeBaseUrl(process.env.NEXT_PUBLIC_LUNATOTEM_API_URL) 
  || 'http://localhost:8081';

// 💳 LunaPay - Processamento de pagamentos
export const LUNAPAY_URL = normalizeBaseUrl(process.env.NEXT_PUBLIC_LUNAPAY_URL) 
  || 'http://localhost:8082';

// Mantém compatibilidade com código antigo
export const API_BASE_URL = TOTEM_API_URL;

// Log configuration on load
if (typeof window !== 'undefined') {
  console.log('[API CONFIG] 🔐 LunaCore URL:', LUNACORE_URL);
  console.log('[API CONFIG] 🏥 Totem API URL:', TOTEM_API_URL);
  console.log('[API CONFIG] 💳 LunaPay URL:', LUNAPAY_URL);
}

export const API_ENDPOINTS = {
  // ==============================================
  // 🏥 LunaTotem API Endpoints
  // ==============================================
  
  // Appointments
  appointments: `${TOTEM_API_URL}/api/appointments`,
  appointmentById: (id: string) => `${TOTEM_API_URL}/api/appointments/${id}`,
  appointmentStatus: (id: string) => `${TOTEM_API_URL}/api/appointments/${id}/status`,
  appointmentPhoto: (id: string) => `${TOTEM_API_URL}/api/appointments/${id}/photo`,
  appointmentReport: (id: string) => `${TOTEM_API_URL}/api/appointments/${id}/report`,
  appointmentNotify: (id: string) => `${TOTEM_API_URL}/api/appointments/${id}/notify`,
  appointmentUpcoming: `${TOTEM_API_URL}/api/appointments/upcoming`,
  appointmentSearch: (q: string) => `${TOTEM_API_URL}/api/appointments/search?q=${encodeURIComponent(q)}`,
  
  // Doctors
  doctors: `${TOTEM_API_URL}/api/doctors`,
  doctorById: (id: string) => `${TOTEM_API_URL}/api/doctors/${id}`,
  
  // Patients
  patients: `${TOTEM_API_URL}/api/patients`,
  patientById: (id: string) => `${TOTEM_API_URL}/api/patients/${id}`,
  patientByCpf: (cpf: string) => `${TOTEM_API_URL}/api/patients/cpf/${cpf}`,

  // Users
  users: `${TOTEM_API_URL}/api/users`,
  userById: (id: string | number) => `${TOTEM_API_URL}/api/users/${id}`,
  
  // Dashboard
  dashboardSummary: `${TOTEM_API_URL}/api/dashboard/summary`, // basic (sanitized)
  dashboardSummaryFull: `${TOTEM_API_URL}/api/dashboard/summary/full`, // admin only
  
  // Health Check
  health: `${TOTEM_API_URL}/actuator/health`,
  
  // ==============================================
  // 🔐 LunaCore Endpoints (Autenticação)
  // ==============================================
  authLogin: `${LUNACORE_URL}/auth/login`,
  authRegister: `${LUNACORE_URL}/auth/register`,
  authRefresh: `${LUNACORE_URL}/auth/refresh`,
  authForgotPassword: `${LUNACORE_URL}/auth/forgot-password`,
  authResetPassword: `${LUNACORE_URL}/auth/reset-password`,
  authRequestAccess: `${LUNACORE_URL}/auth/request-access`,
  
  // ==============================================
  // 💳 LunaPay Endpoints (Pagamentos)
  // ==============================================
  payments: `${LUNAPAY_URL}/api/payments`,
  paymentById: (id: string) => `${LUNAPAY_URL}/api/payments/${id}`,
  paymentStatus: (id: string) => `${LUNAPAY_URL}/api/payments/${id}/status`,
};

// API Client Configuration
export const apiConfig = {
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds
};

// Auth token (JWT) holder - updated after login
export let authToken: string | null = null;
export let authRole: string | null = null;

export function setAuth(token: string | null, role: string | null) {
  authToken = token;
  authRole = role;
}

export function clearAuth() {
  authToken = null;
  authRole = null;
  if (typeof window !== 'undefined') {
    localStorage.removeItem('lv_token');
    localStorage.removeItem('lv_role');
    localStorage.removeItem('lv_refresh');
  }
}

export async function ensureFreshToken() {
  if (typeof window === 'undefined') return;
  const token = localStorage.getItem('lv_token');
  const refresh = localStorage.getItem('lv_refresh');
  if (!token && refresh) {
    // Attempt silent refresh (only if token missing; simplistic strategy)
    try {
      const res = await fetch(API_ENDPOINTS.authRefresh, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: refresh })
      });
      if (res.ok) {
        const data = await res.json();
        localStorage.setItem('lv_token', data.token);
        localStorage.setItem('lv_refresh', data.refreshToken);
        setAuth(data.token, localStorage.getItem('lv_role'));
      } else if (res.status === 400 || res.status === 401) {
        clearAuth();
      }
    } catch {
      // ignore
    }
  }
}
