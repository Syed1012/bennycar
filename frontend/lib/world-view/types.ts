// API Types matching the world-view backend DTOs

export interface CoordinateDto {
  latitude: number;
  longitude: number;
}

export interface RouteDto {
  id: string;
  name: string;
  description: string;
  start_point: CoordinateDto;
  end_point: CoordinateDto;
  waypoints: CoordinateDto[];
  total_distance_meters: number;
  estimated_duration_seconds: number;
  total_waypoints: number;
}

export interface JourneyStateDto {
  journey_id: string;
  route: RouteDto;
  current_position: CoordinateDto;
  current_waypoint_index: number;
  status: JourneyStatus;
  speed_meters_per_second: number;
  progress_percentage: number;
}

export interface CoordinateUpdateDto {
  journey_id: string;
  coordinate: CoordinateDto;
  progress_percentage: number;
  status: JourneyStatus;
  current_waypoint_index: number;
  total_waypoints: number;
  timestamp: string;
}

export type JourneyStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'PAUSED' | 'COMPLETED' | 'WAITING';

// Frontend-specific types
export interface MapPosition {
  lat: number;
  lng: number;
}

export interface JourneyInfo {
  journeyId: string;
  routeName: string;
  status: JourneyStatus;
  progress: number;
  currentPosition: MapPosition;
  destination: MapPosition;
  startPoint: MapPosition;
  waypoints: MapPosition[];
  distanceRemaining: number;
  estimatedTimeRemaining: number;
  speedKmh: number;
}

// Vehicle Types
export type VehicleType = 'EV' | 'HYBRID' | 'PETROL' | 'DIESEL';

export interface VehicleLocationDto {
  vehicle_id: string;
  location: CoordinateDto;
  location_name: string;
  timestamp: string;
}

export interface VehicleStatusDto {
  vehicle_id: string;
  vehicle_type: VehicleType;

  // Fuel related (for non-EV vehicles)
  fuel_percentage: number | null;
  fuel_liters: number | null;
  fuel_capacity_liters: number | null;
  fuel_range_km: number | null;

  // Battery related (for EV and Hybrid)
  battery_percentage: number | null;
  battery_range_km: number | null;
  is_charging: boolean | null;
  estimated_charge_time_minutes: number | null;

  // Engine and maintenance
  engine_oil_status: string;
  engine_oil_percentage: number;
  tire_pressure_status: string;
  next_service_date: string;
  km_until_service: number;

  // General condition
  overall_condition: string;
  odometer_km: number;
  engine_warning: boolean;
  battery_warning: boolean;
  service_warning: boolean;

  // Tire pressures (PSI)
  tire_pressure_front_left: number;
  tire_pressure_front_right: number;
  tire_pressure_rear_left: number;
  tire_pressure_rear_right: number;

  // Temperature
  engine_temperature_celsius: number;
  outside_temperature_celsius: number;

  // Computed
  total_range_km: number;
  has_fuel: boolean;
  has_battery: boolean;

  last_updated: string;
}

export interface VehicleDataDto {
  location: VehicleLocationDto;
  status: VehicleStatusDto;
}
