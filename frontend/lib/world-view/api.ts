import { 
  RouteDto, 
  JourneyStateDto,
  CoordinateUpdateDto,
  VehicleDataDto,
  VehicleType
} from './types';
import { mqttClient, JourneyEvent } from './mqtt-client';

const API_BASE_URL = process.env.NEXT_PUBLIC_WORLD_VIEW_API_URL || 'http://localhost:8084';

class WorldViewApi {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl;
  }

  // ==================== Route Endpoints ====================

  async getAllRoutes(): Promise<RouteDto[]> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes`);
    if (!response.ok) {
      throw new Error(`Failed to fetch routes: ${response.statusText}`);
    }
    return response.json();
  }

  async getRouteById(routeId: string): Promise<RouteDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes/${routeId}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch route: ${response.statusText}`);
    }
    return response.json();
  }

  async getRouteCount(): Promise<number> {
    const response = await fetch(`${this.baseUrl}/api/v1/routes/count`);
    if (!response.ok) {
      throw new Error(`Failed to fetch route count: ${response.statusText}`);
    }
    return response.json();
  }

  // ==================== Journey Endpoints ====================

  async isJourneyActive(): Promise<boolean> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/active`);
    if (!response.ok) {
      throw new Error(`Failed to check journey status: ${response.statusText}`);
    }
    return response.json();
  }

  async getCurrentJourney(): Promise<JourneyStateDto | null> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/current`);
    if (response.status === 204) {
      return null;
    }
    if (!response.ok) {
      throw new Error(`Failed to fetch current journey: ${response.statusText}`);
    }
    return response.json();
  }

  async getJourneyState(journeyId: string): Promise<JourneyStateDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/journeys/${journeyId}`);
    if (!response.ok) {
      throw new Error(`Failed to fetch journey state: ${response.statusText}`);
    }
    return response.json();
  }

  // ==================== Real-time Updates (MQTT) ====================

  subscribeToJourney(
    journeyId: string, 
    onUpdate: (update: CoordinateUpdateDto) => void,
    options?: {
      onError?: (error: Error) => void;
      onEvent?: (event: JourneyEvent) => void;
    }
  ): () => void {
    mqttClient.subscribeToJourney(journeyId, onUpdate, options?.onEvent)
      .catch((error) => {
        console.error('MQTT subscription failed:', error);
        if (options?.onError) {
          options.onError(error);
        }
      });

    return () => {
      mqttClient.unsubscribeFromJourney(journeyId);
    };
  }

  // ==================== Vehicle Endpoints ====================

  async getVehicleLocation(): Promise<VehicleDataDto['location']> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/location`);
    if (!response.ok) {
      throw new Error(`Failed to fetch vehicle location: ${response.statusText}`);
    }
    return response.json();
  }

  async getVehicleStatus(): Promise<VehicleDataDto['status']> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/status`);
    if (!response.ok) {
      throw new Error(`Failed to fetch vehicle status: ${response.statusText}`);
    }
    return response.json();
  }

  async getVehicleData(): Promise<VehicleDataDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/data`);
    if (!response.ok) {
      throw new Error(`Failed to fetch vehicle data: ${response.statusText}`);
    }
    return response.json();
  }

  async refreshVehicleData(): Promise<VehicleDataDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/refresh`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error(`Failed to refresh vehicle data: ${response.statusText}`);
    }
    return response.json();
  }

  async getVehicleType(): Promise<VehicleType> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/type`);
    if (!response.ok) {
      throw new Error(`Failed to fetch vehicle type: ${response.statusText}`);
    }
    const type = await response.text();
    return type as VehicleType;
  }

  async setVehicleType(vehicleType: VehicleType): Promise<VehicleDataDto> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/type/${vehicleType}`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error(`Failed to set vehicle type: ${response.statusText}`);
    }
    return response.json();
  }

  async getAvailableVehicleTypes(): Promise<VehicleType[]> {
    const response = await fetch(`${this.baseUrl}/api/v1/vehicle/types`);
    if (!response.ok) {
      throw new Error(`Failed to fetch vehicle types: ${response.statusText}`);
    }
    return response.json();
  }

  subscribeToVehicle(
    vehicleId: string,
    onData: (data: VehicleDataDto) => void,
    onError?: (error: Error) => void
  ): () => void {
    mqttClient.subscribeToVehicle(vehicleId, onData)
      .catch((error) => {
        console.error('MQTT vehicle subscription failed:', error);
        if (onError) {
          onError(error);
        }
      });

    return () => {
      mqttClient.unsubscribeFromVehicle(vehicleId);
    };
  }

  disconnect(): void {
    mqttClient.disconnect();
  }

  get isConnected(): boolean {
    return mqttClient.connected;
  }
}

export const worldViewApi = new WorldViewApi();
export { WorldViewApi };
