import { userServiceClient, setToken, setRefreshToken, clearTokens } from "./client";
import { API_PATHS } from "./config";
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  User,
} from "@/types/user.types";

export const authService = {
  /**
   * Register a new user
   */
  async register(data: RegisterRequest): Promise<RegisterResponse> {
    const response = await userServiceClient.post<RegisterResponse>(
      `${API_PATHS.V1}${API_PATHS.AUTH.REGISTER}`,
      data
    );
    
    // Store tokens
    setToken(response.data.token);
    setRefreshToken(response.data.refreshToken);
    
    return response.data;
  },

  /**
   * Login user
   */
  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await userServiceClient.post<LoginResponse>(
      `${API_PATHS.V1}${API_PATHS.AUTH.LOGIN}`,
      data
    );
    
    // Store tokens
    setToken(response.data.token);
    setRefreshToken(response.data.refreshToken);
    
    return response.data;
  },

  /**
   * Logout user
   */
  async logout(): Promise<void> {
    try {
      await userServiceClient.post(`${API_PATHS.V1}${API_PATHS.AUTH.LOGOUT}`);
    } finally {
      // Always clear tokens, even if the API call fails
      clearTokens();
    }
  },

  /**
   * Refresh access token
   */
  async refreshToken(refreshToken: string): Promise<RefreshTokenResponse> {
    const response = await userServiceClient.post<RefreshTokenResponse>(
      `${API_PATHS.V1}${API_PATHS.AUTH.REFRESH}`,
      { refreshToken } as RefreshTokenRequest
    );
    
    // Store new token
    setToken(response.data.token);
    
    return response.data;
  },

  /**
   * Get current user profile
   */
  async getCurrentUser(): Promise<User> {
    const response = await userServiceClient.get<User>(
      `${API_PATHS.V1}/users/me`
    );
    return response.data;
  },
};
