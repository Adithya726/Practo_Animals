import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:7681',
  headers: {
    'Content-Type': 'application/json',
  },
});

const AUTH_STORAGE_KEY = 'pawcare_admin';

export function getStoredAuth() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function saveStoredAuth(auth) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth));
}

export function clearStoredAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}

api.interceptors.request.use((config) => {
  const auth = getStoredAuth();

  if (auth?.token) {
    config.headers.Authorization = `Bearer ${auth.token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Only admin pages need a login, so only they are sent back to it on 401.
    if (error.response?.status === 401 && window.location.pathname.startsWith('/admin')) {
      clearStoredAuth();
      if (window.location.pathname !== '/admin/login') {
        window.location.href = '/admin/login';
      }
    }

    return Promise.reject(error);
  }
);

export function getErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  if (!error.response) {
    return 'Cannot reach the server. Check that the backend is running on port 7681.';
  }

  const data = error.response.data;

  if (!data) {
    return fallback;
  }

  if (typeof data.error === 'string') {
    return data.error;
  }

  const firstFieldMessage = Object.values(data).find((value) => typeof value === 'string');

  return firstFieldMessage || fallback;
}

/** Field errors from a 400 validation response, keyed by field name. */
export function getFieldErrors(error) {
  const data = error.response?.data;
  if (error.response?.status !== 400 || !data || typeof data.error === 'string') {
    return {};
  }
  return data;
}

export { AUTH_STORAGE_KEY };
export default api;
