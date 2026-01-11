"use client";

import Image from "next/image";
import Link from "next/link";
import { Vehicle } from "@/types/vehicle.types";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { formatCurrency } from "@/lib/utils";
import { Car, Fuel, Users, Package, Zap, ArrowRight, Star } from "lucide-react";

interface VehicleCardProps {
  vehicle: Vehicle;
}

export function VehicleCard({ vehicle }: VehicleCardProps) {
  const isAvailable = vehicle.status === "AVAILABLE";

  return (
    <Card className="group overflow-hidden hover:shadow-2xl transition-all duration-500 border-2 border-[#e8d5c4] bg-white rounded-2xl hover:border-[#c89968]/50 hover:-translate-y-2">
      {/* Image Section with Overlay Effects */}
      <div className="relative h-64 bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4] overflow-hidden">
        {vehicle.mainImageUrl ? (
          <>
            <Image
              src={vehicle.mainImageUrl}
              alt={`${vehicle.brand.name} ${vehicle.model}`}
              fill
              className="object-cover group-hover:scale-110 transition-transform duration-700 ease-out"
            />
            {/* Gradient Overlay on Hover */}
            <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
          </>
        ) : (
          <div className="flex items-center justify-center h-full">
            <Car className="h-24 w-24 text-[#c89968]/30" />
          </div>
        )}
        
        {/* Status Badge */}
        {!isAvailable && (
          <div className="absolute inset-0 bg-black/70 flex items-center justify-center backdrop-blur-sm z-10">
            <span className="bg-red-500 text-white px-5 py-2.5 rounded-full font-bold text-sm shadow-xl">
              {vehicle.status.replace("_", " ")}
            </span>
          </div>
        )}
        
        {/* Brand Badge - Top Left */}
        <div className="absolute top-4 left-4 z-20">
          <div className="bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white px-4 py-2 rounded-full text-sm font-bold shadow-xl backdrop-blur-sm bg-white/10 border border-white/20">
            {vehicle.brand.name}
          </div>
        </div>

        {/* Stock Badge - Top Right */}
        {isAvailable && vehicle.stockQuantity > 0 && (
          <div className="absolute top-4 right-4 z-20">
            <div className="bg-green-500/90 backdrop-blur-sm text-white px-3 py-1.5 rounded-full text-xs font-semibold shadow-lg flex items-center gap-1.5">
              <div className="h-2 w-2 rounded-full bg-white animate-pulse" />
              {vehicle.stockQuantity} in stock
            </div>
          </div>
        )}

        {/* Quick View Button - Appears on Hover */}
        <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 translate-y-4 opacity-0 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-300 z-20">
          <Button
            asChild
            className="bg-white/95 backdrop-blur-sm text-[#4a3f35] hover:bg-white shadow-xl hover:shadow-2xl border-2 border-white/50"
          >
            <Link href={`/vehicles/${vehicle.id}`} className="flex items-center gap-2">
              Quick View
              <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        </div>
      </div>

      <CardContent className="p-6 space-y-4">
        {/* Title and Year */}
        <div>
          <h3 className="text-xl font-bold text-[#4a3f35] group-hover:text-[#c89968] transition-colors duration-300 mb-1 line-clamp-1">
            {vehicle.brand.name} {vehicle.model}
          </h3>
          <div className="flex items-center gap-2">
            <p className="text-sm text-[#8b7355] font-medium">{vehicle.modelYear}</p>
            <span className="text-[#8b7355]/50">•</span>
            <div className="flex items-center gap-1">
              <Star className="h-3.5 w-3.5 fill-[#c89968] text-[#c89968]" />
              <span className="text-xs text-[#8b7355] font-medium">Premium</span>
            </div>
          </div>
        </div>

        {/* Key Specs Grid */}
        <div className="grid grid-cols-2 gap-3">
          <div className="flex items-center gap-2.5 p-2.5 rounded-xl bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4]/50 group-hover:from-[#e8d5c4] group-hover:to-[#c89968]/10 transition-all duration-300">
            <div className="h-9 w-9 rounded-lg bg-white/80 flex items-center justify-center shadow-sm">
              <Fuel className="h-4 w-4 text-[#c89968]" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs text-[#8b7355] font-medium">Fuel</p>
              <p className="text-sm font-semibold text-[#4a3f35] truncate">{vehicle.fuelType}</p>
            </div>
          </div>
          
          <div className="flex items-center gap-2.5 p-2.5 rounded-xl bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4]/50 group-hover:from-[#e8d5c4] group-hover:to-[#c89968]/10 transition-all duration-300">
            <div className="h-9 w-9 rounded-lg bg-white/80 flex items-center justify-center shadow-sm">
              <Users className="h-4 w-4 text-[#c89968]" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs text-[#8b7355] font-medium">Seats</p>
              <p className="text-sm font-semibold text-[#4a3f35]">{vehicle.seatingCapacity}</p>
            </div>
          </div>
          
          <div className="flex items-center gap-2.5 p-2.5 rounded-xl bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4]/50 group-hover:from-[#e8d5c4] group-hover:to-[#c89968]/10 transition-all duration-300">
            <div className="h-9 w-9 rounded-lg bg-white/80 flex items-center justify-center shadow-sm">
              <Package className="h-4 w-4 text-[#c89968]" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs text-[#8b7355] font-medium">Transmission</p>
              <p className="text-sm font-semibold text-[#4a3f35] truncate">{vehicle.transmission}</p>
            </div>
          </div>
          
          <div className="flex items-center gap-2.5 p-2.5 rounded-xl bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4]/50 group-hover:from-[#e8d5c4] group-hover:to-[#c89968]/10 transition-all duration-300">
            <div className="h-9 w-9 rounded-lg bg-white/80 flex items-center justify-center shadow-sm">
              <Zap className="h-4 w-4 text-[#c89968]" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs text-[#8b7355] font-medium">Power</p>
              <p className="text-sm font-semibold text-[#4a3f35] truncate">{vehicle.horsepower}</p>
            </div>
          </div>
        </div>

        {/* Description */}
        <p className="text-sm text-[#4a3f35]/70 line-clamp-2 leading-relaxed min-h-[2.5rem]">
          {vehicle.description}
        </p>

        {/* Price Section */}
        <div className="pt-4 border-t-2 border-[#e8d5c4]">
          <div className="flex items-baseline justify-between">
            <div>
              <p className="text-2xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
                {formatCurrency(vehicle.basePrice)}
              </p>
              <p className="text-xs text-[#8b7355] mt-0.5">Starting price</p>
            </div>
            {vehicle.vehicleType && (
              <div className="px-3 py-1.5 rounded-lg bg-[#f5ede4] border border-[#e8d5c4]">
                <p className="text-xs font-semibold text-[#8b7355]">{vehicle.vehicleType.name}</p>
              </div>
            )}
          </div>
        </div>
      </CardContent>

      <CardFooter className="p-6 pt-0">
        <Button
          asChild
          className="w-full bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:from-[#d4a574] hover:to-[#c89968] text-white shadow-lg hover:shadow-xl transition-all duration-300 rounded-xl h-12 font-semibold group/btn"
          disabled={!isAvailable}
        >
          <Link href={`/vehicles/${vehicle.id}`} className="flex items-center justify-center gap-2">
            {isAvailable ? (
              <>
                View Details
                <ArrowRight className="h-4 w-4 group-hover/btn:translate-x-1 transition-transform" />
              </>
            ) : (
              "Not Available"
            )}
          </Link>
        </Button>
      </CardFooter>
    </Card>
  );
}
