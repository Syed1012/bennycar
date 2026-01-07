import { orderServiceClient } from "./client";
import { API_PATHS } from "./config";
import {
  Order,
  CreateOrderRequest,
  OrderListResponse,
  OrderStatus,
} from "@/types/order.types";

export const orderService = {
  /**
   * Create a new order
   */
  async createOrder(data: CreateOrderRequest): Promise<Order> {
    const response = await orderServiceClient.post<Order>(
      `${API_PATHS.V1}${API_PATHS.ORDERS.BASE}`,
      data
    );
    return response.data;
  },

  /**
   * Get order by ID
   */
  async getOrderById(id: string): Promise<Order> {
    const response = await orderServiceClient.get<Order>(
      `${API_PATHS.V1}${API_PATHS.ORDERS.BY_ID(id)}`
    );
    return response.data;
  },

  /**
   * Get user's orders
   */
  async getUserOrders(): Promise<OrderListResponse> {
    const response = await orderServiceClient.get<OrderListResponse>(
      `${API_PATHS.V1}${API_PATHS.ORDERS.BASE}/my-orders`
    );
    return response.data;
  },

  /**
   * Cancel an order
   */
  async cancelOrder(id: string): Promise<Order> {
    const response = await orderServiceClient.patch<Order>(
      `${API_PATHS.V1}${API_PATHS.ORDERS.BY_ID(id)}/cancel`
    );
    return response.data;
  },

  /**
   * Update order status (Admin only)
   */
  async updateOrderStatus(id: string, status: OrderStatus): Promise<Order> {
    const response = await orderServiceClient.patch<Order>(
      `${API_PATHS.V1}${API_PATHS.ORDERS.BY_ID(id)}/status`,
      { status }
    );
    return response.data;
  },
};
