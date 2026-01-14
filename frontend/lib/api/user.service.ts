import { userServiceClient } from "./client";
import { API_PATHS } from "./config";

export interface UserProfile {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  profilePictureUrl?: string;
  address?: string;
  createdAt: string;
  updatedAt: string;
  status: string;
  roles: string[];
  emailVerified?: boolean;
}

export const userService = {
  /**
   * Get current user's full profile
   */
  async getProfile(): Promise<UserProfile> {
    const response = await userServiceClient.get<UserProfile>(
      `${API_PATHS.V1}/users/me`
    );
    return response.data;
  },
};
