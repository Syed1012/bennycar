"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { orderService } from "@/lib/api/order.service";
import { useAuthStore } from "@/store/auth.store";
import { Order } from "@/types/order.types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Package, Calendar, DollarSign, MapPin, Loader2 } from "lucide-react";
import { formatCurrency, formatDate } from "@/lib/utils";

export default function OrdersPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/auth/login");
      return;
    }
    loadOrders();
  }, [isAuthenticated]);

  const loadOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await orderService.getUserOrders();
      setOrders(response.orders);
    } catch (err) {
      setError("Failed to load orders");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "CONFIRMED":
        return "bg-blue-100 text-blue-700 border-blue-200";
      case "PROCESSING":
        return "bg-yellow-100 text-yellow-700 border-yellow-200";
      case "SHIPPED":
        return "bg-purple-100 text-purple-700 border-purple-200";
      case "DELIVERED":
        return "bg-green-100 text-green-700 border-green-200";
      case "CANCELLED":
        return "bg-red-100 text-red-700 border-red-200";
      default:
        return "bg-gray-100 text-gray-700 border-gray-200";
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#fafaf8] flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="h-12 w-12 animate-spin text-[#c89968] mx-auto mb-4" />
          <p className="text-[#8b7355]">Loading your orders...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#fafaf8]">
      <div className="container mx-auto px-4 py-12">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold bg-linear-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent mb-2">
            My Orders
          </h1>
          <p className="text-[#8b7355]">
            Track and manage your vehicle orders
          </p>
        </div>

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border-2 border-red-200 rounded-xl p-6 mb-6">
            <p className="text-red-600">{error}</p>
            <Button onClick={loadOrders} className="mt-4">
              Try Again
            </Button>
          </div>
        )}

        {/* Orders List */}
        {orders.length === 0 ? (
          <Card className="border-2 border-[#e8d5c4]">
            <CardContent className="py-16 text-center">
              <Package className="h-24 w-24 text-[#e8d5c4] mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-[#4a3f35] mb-2">
                No Orders Yet
              </h3>
              <p className="text-[#8b7355] mb-6">
                Start shopping to see your orders here
              </p>
              <Button asChild>
                <a href="/vehicles">Browse Vehicles</a>
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <Card key={order.id} className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
                <CardHeader className="pb-4">
                  <div className="flex items-start justify-between">
                    <div>
                      <CardTitle className="text-xl mb-2">
                        Order #{order.id.slice(0, 8)}
                      </CardTitle>
                      <div className="flex items-center gap-2 text-sm text-[#8b7355]">
                        <Calendar className="h-4 w-4" />
                        {formatDate(order.createdAt)}
                      </div>
                    </div>
                    <div className={`px-4 py-2 rounded-full text-sm font-semibold border-2 ${getStatusColor(order.status)}`}>
                      {order.status}
                    </div>
                  </div>
                </CardHeader>

                <CardContent className="space-y-4">
                  {/* Order Item */}
                  <div className="space-y-3">
                    <div className="flex items-center justify-between p-4 bg-[#fafaf8] rounded-xl border border-[#e8d5c4]">
                      <div className="flex items-center gap-4">
                        <div className="h-16 w-16 rounded-lg bg-linear-to-br from-[#f5ede4] to-[#e8d5c4] flex items-center justify-center">
                          <Package className="h-8 w-8 text-[#c89968]" />
                        </div>
                        <div>
                          <div className="font-semibold text-[#4a3f35]">
                            {order.vehicle?.brand?.name} {order.vehicle?.model}
                          </div>
                          <div className="text-sm text-[#8b7355]">
                            Year: {order.vehicle?.modelYear}
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-[#c89968]">
                          {formatCurrency(order.totalAmount)}
                        </div>
                        <div className="text-xs text-[#8b7355]">Total</div>
                      </div>
                    </div>
                  </div>

                  {/* Delivery Address */}
                  {order.deliveryAddress && (
                    <div className="flex items-start gap-3 p-4 bg-[#f5ede4] rounded-xl border border-[#e8d5c4]">
                      <MapPin className="h-5 w-5 text-[#c89968] mt-0.5" />
                      <div>
                        <div className="font-semibold text-[#4a3f35] mb-1">
                          Delivery Address
                        </div>
                        <div className="text-sm text-[#8b7355]">
                          {order.deliveryAddress.street}, {order.deliveryAddress.city}
                          <br />
                          {order.deliveryAddress.state} {order.deliveryAddress.zipCode}
                          <br />
                          {order.deliveryAddress.country}
                        </div>
                      </div>
                    </div>
                  )}

                  {/* Estimated Delivery */}
                  {order.estimatedDeliveryDate && (
                    <div className="flex items-center gap-3 p-4 bg-linear-to-r from-[#f5ede4] to-[#e8d5c4] rounded-xl border border-[#c89968]/30">
                      <Calendar className="h-5 w-5 text-[#c89968]" />
                      <div>
                        <div className="text-sm text-[#8b7355]">Estimated Delivery</div>
                        <div className="font-semibold text-[#4a3f35]">
                          {formatDate(order.estimatedDeliveryDate)}
                        </div>
                      </div>
                    </div>
                  )}

                  {/* Total */}
                  <div className="flex items-center justify-between pt-4 border-t-2 border-[#e8d5c4]">
                    <div className="flex items-center gap-2 text-[#4a3f35] font-semibold">
                      <DollarSign className="h-5 w-5" />
                      Total Amount
                    </div>
                    <div className="text-2xl font-bold bg-linear-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
                      {formatCurrency(order.totalAmount)}
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
