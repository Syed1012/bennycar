import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from "axios";
import { API_CONFIG } from "./config";

// Token management
const TOKEN_KEY = "bennycar_token";
const REFRESH_TOKEN_KEY = "bennycar_refresh_token";

export const getToken = (): string | null => {
  if (globalThis.window === undefined) return null;
  return localStorage.getItem(TOKEN_KEY);
};

export const setToken = (token: string): void => {
  if (globalThis.window !== undefined) {
    localStorage.setItem(TOKEN_KEY, token);
  }
};

export const getRefreshToken = (): string | null => {
  if (globalThis.window === undefined) return null;
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

export const setRefreshToken = (token: string): void => {
  if (globalThis.window !== undefined) {
    localStorage.setItem(REFRESH_TOKEN_KEY, token);
  }
};

export const clearTokens = (): void => {
  if (globalThis.window !== undefined) {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  }
};

// Create axios instances for each service
const createApiClient = (baseURL: string): AxiosInstance => {
  const client = axios.create({
    baseURL,
    headers: {
      "Content-Type": "application/json",
    },
    timeout: 10000,
  });

  // Request interceptor to add auth token
  client.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = getToken();
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response interceptor for error handling
  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

      // If 401 and not already retried, try to refresh token
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshToken = getRefreshToken();
          if (refreshToken) {
            const response = await axios.post(
              `${API_CONFIG.USER_SERVICE}/api/v1/auth/refresh`,
              { refreshToken }
            );
            
            // Backend returns accessToken (camelCase)
            const token = response.data.accessToken || response.data.token;
            setToken(token);

            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }

            return client(originalRequest);
          }
        } catch (refreshError) {
          // Refresh failed, clear tokens and redirect to login
          clearTokens();
          if (globalThis.window !== undefined) {
            globalThis.window.location.href = "/auth/login";
          }
          throw refreshError;
        }
      }

      throw error;
    }
  );

  return client;
};

// Export API clients
export const userServiceClient = createApiClient(API_CONFIG.USER_SERVICE);
export const vehicleServiceClient = createApiClient(API_CONFIG.VEHICLE_SERVICE);
export const orderServiceClient = createApiClient(API_CONFIG.ORDER_SERVICE);
export const worldViewClient = createApiClient(API_CONFIG.WORLD_VIEW);

// Default export for general use
export default createApiClient(API_CONFIG.GATEWAY);
