import { vehicleServiceClient } from "./client";
import { API_PATHS } from "./config";
import {
  Vehicle,
  VehicleSearchParams,
  PageableResponse,
  Brand,
  VehicleType,
  CustomizationOption,
  VehicleConfiguration,
} from "@/types/vehicle.types";

export const vehicleService = {
  /**
   * Search vehicles with filters and pagination
   */
  async searchVehicles(
    params: VehicleSearchParams = {}
  ): Promise<PageableResponse<Vehicle>> {
    const response = await vehicleServiceClient.get<PageableResponse<Vehicle>>(
      `${API_PATHS.V1}${API_PATHS.VEHICLES.BASE}`,
      { params }
    );
    return response.data;
  },

  /**
   * Get vehicle by ID
   */
  async getVehicleById(id: string): Promise<Vehicle> {
    const response = await vehicleServiceClient.get<Vehicle>(
      `${API_PATHS.V1}${API_PATHS.VEHICLES.BY_ID(id)}`
    );
    return response.data;
  },

  /**
   * Get all brands
   */
  async getBrands(): Promise<Brand[]> {
    const response = await vehicleServiceClient.get<Brand[]>(
      `${API_PATHS.V1}${API_PATHS.BRANDS.BASE}`
    );
    return response.data;
  },

  /**
   * Get brand by ID
   */
  async getBrandById(id: string): Promise<Brand> {
    const response = await vehicleServiceClient.get<Brand>(
      `${API_PATHS.V1}${API_PATHS.BRANDS.BY_ID(id)}`
    );
    return response.data;
  },

  /**
   * Get all vehicle types
   */
  async getVehicleTypes(): Promise<VehicleType[]> {
    const response = await vehicleServiceClient.get<VehicleType[]>(
      `${API_PATHS.V1}${API_PATHS.VEHICLE_TYPES.BASE}`
    );
    return response.data;
  },

  /**
   * Get customization options for a vehicle
   */
  async getCustomizationOptions(vehicleId: string): Promise<CustomizationOption[]> {
    const response = await vehicleServiceClient.get<CustomizationOption[]>(
      `${API_PATHS.V1}/vehicles/${vehicleId}/customization-options`
    );
    return response.data;
  },

  /**
   * Create vehicle configuration
   */
  async createConfiguration(
    vehicleId: string,
    optionIds: string[]
  ): Promise<VehicleConfiguration> {
    const response = await vehicleServiceClient.post<VehicleConfiguration>(
      `${API_PATHS.V1}/configurations`,
      {
        vehicleId,
        selectedOptionIds: optionIds,
      }
    );
    return response.data;
  },

  /**
   * Update vehicle configuration
   */
  async updateConfiguration(
    configId: string,
    optionIds: string[]
  ): Promise<VehicleConfiguration> {
    const response = await vehicleServiceClient.put<VehicleConfiguration>(
      `${API_PATHS.V1}/configurations/${configId}`,
      {
        selectedOptionIds: optionIds,
      }
    );
    return response.data;
  },

  /**
   * Get configuration by ID
   */
  async getConfiguration(configId: string): Promise<VehicleConfiguration> {
    const response = await vehicleServiceClient.get<VehicleConfiguration>(
      `${API_PATHS.V1}/configurations/${configId}`
    );
    return response.data;
  },

  /**
   * Finalize configuration
   */
  async finalizeConfiguration(configId: string): Promise<VehicleConfiguration> {
    const response = await vehicleServiceClient.patch<VehicleConfiguration>(
      `${API_PATHS.V1}/configurations/${configId}/finalize`
    );
    return response.data;
  },
};
