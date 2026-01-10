import { Vehicle, VehicleConfiguration } from "./vehicle.types";
import { User } from "./user.types";

// Order Status
export enum OrderStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  PROCESSING = "PROCESSING",
  SHIPPED = "SHIPPED",
  DELIVERED = "DELIVERED",
  CANCELLED = "CANCELLED",
}

// Payment Status
export enum PaymentStatus {
  PENDING = "PENDING",
  COMPLETED = "COMPLETED",
  FAILED = "FAILED",
  REFUNDED = "REFUNDED",
}

// Order
export interface Order {
  id: string;
  orderNumber: string;
  user: User;
  vehicle: Vehicle;
  configuration?: VehicleConfiguration;
  totalAmount: number;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  deliveryAddress: Address;
  estimatedDeliveryDate?: string;
  actualDeliveryDate?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

// Address
export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}

// Create Order Request
export interface CreateOrderRequest {
  vehicleId: string;
  configurationId?: string;
  deliveryAddress: Address;
  notes?: string;
}

// Order List Response
export interface OrderListResponse {
  orders: Order[];
  totalCount: number;
}
