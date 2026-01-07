export const API_CONFIG = {
  GATEWAY: process.env.NEXT_PUBLIC_API_GATEWAY_URL || "http://localhost:8080",
  USER_SERVICE: process.env.NEXT_PUBLIC_USER_SERVICE_URL || "http://localhost:8081",
  VEHICLE_SERVICE: process.env.NEXT_PUBLIC_VEHICLE_SERVICE_URL || "http://localhost:8082",
  ORDER_SERVICE: process.env.NEXT_PUBLIC_ORDER_SERVICE_URL || "http://localhost:8083",
  WORLD_VIEW: process.env.NEXT_PUBLIC_WORLD_VIEW_URL || "http://localhost:8084",
} as const;

export const API_PATHS = {
  V1: "/api/v1",
  AUTH: {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    LOGOUT: "/auth/logout",
    REFRESH: "/auth/refresh",
  },
  VEHICLES: {
    BASE: "/vehicles",
    BY_ID: (id: string) => `/vehicles/${id}`,
    SEARCH: "/vehicles",
  },
  BRANDS: {
    BASE: "/brands",
    BY_ID: (id: string) => `/brands/${id}`,
  },
  VEHICLE_TYPES: {
    BASE: "/vehicle-types",
  },
  ORDERS: {
    BASE: "/orders",
    BY_ID: (id: string) => `/orders/${id}`,
  },
} as const;
