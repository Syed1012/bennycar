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
  UserRole,
} from "@/types/user.types";

export const authService = {
  /**
   * Register a new user
   */
  async register(data: RegisterRequest): Promise<RegisterResponse> {
    const response = await userServiceClient.post<{ accessToken: string; refreshToken: string }>(
      `${API_PATHS.V1}${API_PATHS.AUTH.REGISTER}`,
      data
    );
    
    // Store tokens - backend returns accessToken (camelCase)
    setToken(response.data.accessToken);
    setRefreshToken(response.data.refreshToken);
    
    // Fetch user profile after registration
    const user = await this.getCurrentUser();
    
    return {
      token: response.data.accessToken,
      refreshToken: response.data.refreshToken,
      user,
    };
  },

  /**
   * Login user
   */
  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await userServiceClient.post<{ accessToken: string; refreshToken: string }>(
      `${API_PATHS.V1}${API_PATHS.AUTH.LOGIN}`,
      data
    );
    
    // Store tokens - backend returns accessToken (camelCase)
    setToken(response.data.accessToken);
    setRefreshToken(response.data.refreshToken);
    
    // Fetch user profile after login
    const user = await this.getCurrentUser();
    
    return {
      token: response.data.accessToken,
      refreshToken: response.data.refreshToken,
      user,
    };
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
    const response = await userServiceClient.post<{ accessToken: string }>(
      `${API_PATHS.V1}${API_PATHS.AUTH.REFRESH}`,
      { refreshToken } as RefreshTokenRequest
    );
    
    // Store new token - backend returns accessToken (camelCase)
    setToken(response.data.accessToken);
    
    return {
      token: response.data.accessToken,
    };
  },

  /**
   * Get current user profile
   */
  async getCurrentUser(): Promise<User> {
    const response = await userServiceClient.get<{
      userId: string;
      email: string;
      firstName: string;
      lastName: string;
      roles: string[];
      createdAt: string;
      updatedAt: string;
    }>(`${API_PATHS.V1}/users/me`);
    
    // Map UserProfileResponse to User type
    const profile = response.data;
    return {
      id: profile.userId,
      email: profile.email,
      firstName: profile.firstName,
      lastName: profile.lastName,
      role: profile.roles?.includes("ADMIN") 
        ? UserRole.ADMIN 
        : profile.roles?.includes("DEALER")
        ? UserRole.DEALER
        : UserRole.CUSTOMER,
      createdAt: profile.createdAt,
      updatedAt: profile.updatedAt,
    };
  },
};
