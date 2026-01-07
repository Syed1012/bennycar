// Brand Types
export interface Brand {
  id: string;
  name: string;
  description?: string;
  logoUrl?: string;
  countryOfOrigin?: string;
  foundedYear?: number;
}

// Vehicle Type
export interface VehicleType {
  id: string;
  name: string;
  description?: string;
  iconUrl?: string;
}

// Vehicle Status
export enum VehicleStatus {
  AVAILABLE = "AVAILABLE",
  SOLD_OUT = "SOLD_OUT",
  DISCONTINUED = "DISCONTINUED",
}

// Vehicle
export interface Vehicle {
  id: string;
  brand: Brand;
  vehicleType: VehicleType;
  model: string;
  modelYear: number;
  description: string;
  basePrice: number;
  engine: string;
  transmission: string;
  fuelType: string;
  horsepower: string;
  seatingCapacity: number;
  cargoCapacityLiters: number;
  fuelEfficiency: string;
  mainImageUrl: string;
  additionalImages: string[];
  status: VehicleStatus;
  stockQuantity: number;
  createdAt: string;
  updatedAt: string;
}

// Vehicle Search/Filter Params
export interface VehicleSearchParams {
  brandId?: string;
  vehicleTypeId?: string;
  modelYear?: number;
  minPrice?: number;
  maxPrice?: number;
  status?: VehicleStatus;
  page?: number;
  size?: number;
  sort?: string;
}

// Paginated Response
export interface PageableResponse<T> {
  content: T[];
  pageable: {
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}

// Customization Options (for vehicle configuration)
export interface CustomizationCategory {
  id: string;
  name: string;
  description?: string;
  displayOrder: number;
}

export interface CustomizationOption {
  id: string;
  category: CustomizationCategory;
  name: string;
  description?: string;
  additionalPrice: number;
  imageUrl?: string;
  isAvailable: boolean;
}

// Vehicle Configuration
export interface VehicleConfiguration {
  id: string;
  vehicle: Vehicle;
  selectedOptions: CustomizationOption[];
  totalPrice: number;
  status: ConfigurationStatus;
  createdAt: string;
  updatedAt: string;
}

export enum ConfigurationStatus {
  DRAFT = "DRAFT",
  FINALIZED = "FINALIZED",
  ORDERED = "ORDERED",
}
