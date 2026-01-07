"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { vehicleService } from "@/lib/api/vehicle.service";
import { Vehicle } from "@/types/vehicle.types";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { 
  ArrowLeft, 
  Calendar, 
  Gauge, 
  Zap, 
  Fuel, 
  Settings, 
  ShoppingCart,
  CheckCircle2,
  Star
} from "lucide-react";
import { formatCurrency } from "@/lib/utils";

export default function VehicleDetailsPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedImage, setSelectedImage] = useState(0);

  useEffect(() => {
    loadVehicle();
  }, [params.id]);

  const loadVehicle = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await vehicleService.getVehicleById(params.id);
      setVehicle(data);
    } catch (err) {
      setError("Failed to load vehicle details");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#fafaf8] flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-4 border-[#c89968] border-t-transparent mx-auto mb-4"></div>
          <p className="text-[#8b7355]">Loading vehicle details...</p>
        </div>
      </div>
    );
  }

  if (error || !vehicle) {
    return (
      <div className="min-h-screen bg-[#fafaf8] flex items-center justify-center p-4">
        <Card className="max-w-md w-full">
          <CardContent className="pt-6 text-center">
            <p className="text-red-600 mb-4">{error || "Vehicle not found"}</p>
            <Button onClick={() => router.push("/vehicles")}>
              Back to Vehicles
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  // Mock images for demo
  const images = [
    vehicle.imageUrl || "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1514316454349-750a7fd3da3a?w=800&h=600&fit=crop",
    "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop",
  ];

  return (
    <div className="min-h-screen bg-[#fafaf8]">
      <div className="container mx-auto px-4 py-8">
        {/* Back Button */}
        <Button
          variant="ghost"
          onClick={() => router.push("/vehicles")}
          className="mb-6"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Vehicles
        </Button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column - Images */}
          <div className="lg:col-span-2 space-y-4">
            {/* Main Image */}
            <div className="relative rounded-2xl overflow-hidden bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4] shadow-xl">
              <img
                src={images[selectedImage]}
                alt={vehicle.model}
                className="w-full h-96 md:h-[500px] object-cover"
              />
              {vehicle.status === "AVAILABLE" && (
                <div className="absolute top-4 right-4 bg-green-500 text-white px-4 py-2 rounded-full text-sm font-semibold flex items-center gap-2 shadow-lg">
                  <CheckCircle2 className="h-4 w-4" />
                  Available
                </div>
              )}
            </div>

            {/* Thumbnail Images */}
            <div className="grid grid-cols-4 gap-4">
              {images.map((image, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImage(index)}
                  className={`rounded-xl overflow-hidden border-2 transition-all ${
                    selectedImage === index
                      ? "border-[#c89968] scale-105 shadow-lg"
                      : "border-[#e8d5c4] hover:border-[#c89968]"
                  }`}
                >
                  <img
                    src={image}
                    alt={`View ${index + 1}`}
                    className="w-full h-24 object-cover"
                  />
                </button>
              ))}
            </div>

            {/* Specifications Card */}
            <Card className="border-2 border-[#e8d5c4] shadow-lg">
              <CardContent className="p-6">
                <h3 className="text-2xl font-bold text-[#4a3f35] mb-6">Specifications</h3>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-6">
                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Calendar className="h-5 w-5" />
                      <span className="text-sm">Year</span>
                    </div>
                    <div className="text-xl font-bold text-[#4a3f35]">{vehicle.modelYear}</div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Settings className="h-5 w-5" />
                      <span className="text-sm">Type</span>
                    </div>
                    <div className="text-xl font-bold text-[#4a3f35]">{vehicle.vehicleType.name}</div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Gauge className="h-5 w-5" />
                      <span className="text-sm">Engine</span>
                    </div>
                    <div className="text-xl font-bold text-[#4a3f35]">
                      {vehicle.specifications?.engineType || "V8 Turbo"}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Zap className="h-5 w-5" />
                      <span className="text-sm">Power</span>
                    </div>
                    <div className="text-xl font-bold text-[#4a3f35]">
                      {vehicle.specifications?.horsepower || "650"} HP
                    </div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Fuel className="h-5 w-5" />
                      <span className="text-sm">Fuel Type</span>
                    </div>
                    <div className="text-xl font-bold text-[#4a3f35]">
                      {vehicle.specifications?.fuelType || "Premium"}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Star className="h-5 w-5" />
                      <span className="text-sm">Rating</span>
                    </div>
                    <div className="text-xl font-bold text-[#4a3f35]">4.9/5.0</div>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Description */}
            <Card className="border-2 border-[#e8d5c4] shadow-lg">
              <CardContent className="p-6">
                <h3 className="text-2xl font-bold text-[#4a3f35] mb-4">Description</h3>
                <p className="text-[#8b7355] leading-relaxed">
                  {vehicle.description || 
                    `Experience the pinnacle of automotive excellence with this ${vehicle.brand.name} ${vehicle.model}. 
                    This remarkable vehicle combines cutting-edge technology with timeless design, delivering an 
                    unparalleled driving experience. Every detail has been crafted to perfection, from the powerful 
                    engine to the luxurious interior. Whether you're cruising on the highway or navigating city streets, 
                    this vehicle offers exceptional performance, comfort, and style.`
                  }
                </p>
              </CardContent>
            </Card>
          </div>

          {/* Right Column - Purchase Info */}
          <div className="space-y-6">
            <Card className="border-2 border-[#e8d5c4] shadow-2xl sticky top-24">
              <CardContent className="p-6 space-y-6">
                {/* Brand & Model */}
                <div>
                  <div className="text-sm text-[#8b7355] mb-1 uppercase tracking-wide">
                    {vehicle.brand.name}
                  </div>
                  <h1 className="text-3xl font-bold text-[#4a3f35] mb-2">
                    {vehicle.model}
                  </h1>
                  <div className="flex items-center gap-2 text-[#8b7355]">
                    <Star className="h-5 w-5 fill-[#c89968] text-[#c89968]" />
                    <span className="font-semibold">4.9</span>
                    <span className="text-sm">(127 reviews)</span>
                  </div>
                </div>

                {/* Price */}
                <div className="pt-4 border-t border-[#e8d5c4]">
                  <div className="text-sm text-[#8b7355] mb-1">Starting at</div>
                  <div className="text-4xl font-bold bg-linear-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
                    {formatCurrency(vehicle.basePrice)}
                  </div>
                  <div className="text-sm text-[#8b7355] mt-1">
                    + taxes and fees
                  </div>
                </div>

                {/* Status Badge */}
                <div className={`p-4 rounded-xl border-2 ${
                  vehicle.status === "AVAILABLE" 
                    ? "bg-green-50 border-green-200"
                    : "bg-red-50 border-red-200"
                }`}>
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className={`h-5 w-5 ${
                      vehicle.status === "AVAILABLE" ? "text-green-600" : "text-red-600"
                    }`} />
                    <span className={`font-semibold ${
                      vehicle.status === "AVAILABLE" ? "text-green-700" : "text-red-700"
                    }`}>
                      {vehicle.status === "AVAILABLE" ? "In Stock - Ready to Order" : "Currently Unavailable"}
                    </span>
                  </div>
                </div>

                {/* Action Buttons */}
                <div className="space-y-3">
                  <Button 
                    size="lg" 
                    className="w-full h-12"
                    disabled={vehicle.status !== "AVAILABLE"}
                  >
                    <ShoppingCart className="h-5 w-5 mr-2" />
                    Order Now
                  </Button>
                  
                  <Button 
                    size="lg" 
                    variant="outline"
                    className="w-full h-12"
                    disabled={vehicle.status !== "AVAILABLE"}
                  >
                    <Settings className="h-5 w-5 mr-2" />
                    Configure Vehicle
                  </Button>
                </div>

                {/* Features List */}
                <div className="pt-4 border-t border-[#e8d5c4]">
                  <h4 className="font-semibold text-[#4a3f35] mb-3">Key Features</h4>
                  <ul className="space-y-2">
                    {[
                      "Premium Leather Interior",
                      "Advanced Driver Assistance",
                      "Panoramic Sunroof",
                      "Premium Sound System",
                      "Wireless Charging",
                      "Adaptive Cruise Control"
                    ].map((feature, index) => (
                      <li key={index} className="flex items-center gap-2 text-sm text-[#8b7355]">
                        <CheckCircle2 className="h-4 w-4 text-[#c89968]" />
                        {feature}
                      </li>
                    ))}
                  </ul>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}
