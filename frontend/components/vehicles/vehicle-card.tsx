"use client";

import Image from "next/image";
import Link from "next/link";
import { Vehicle } from "@/types/vehicle.types";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { formatCurrency } from "@/lib/utils";
import { Car, Fuel, Users, Package } from "lucide-react";

interface VehicleCardProps {
  vehicle: Vehicle;
}

export function VehicleCard({ vehicle }: VehicleCardProps) {
  const isAvailable = vehicle.status === "AVAILABLE";

  return (
    <Card className="overflow-hidden hover:shadow-2xl transition-all duration-300 border-[#e8d5c4] group">
      {/* Image Section */}
      <div className="relative h-52 bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4] overflow-hidden">
        {vehicle.mainImageUrl ? (
          <Image
            src={vehicle.mainImageUrl}
            alt={`${vehicle.brand.name} ${vehicle.model}`}
            fill
            className="object-cover group-hover:scale-105 transition-transform duration-300"
          />
        ) : (
          <div className="flex items-center justify-center h-full">
            <Car className="h-24 w-24 text-[#c89968]/30" />
          </div>
        )}
        {!isAvailable && (
          <div className="absolute inset-0 bg-black/60 flex items-center justify-center backdrop-blur-sm">
            <span className="bg-red-500 text-white px-4 py-2 rounded-full font-semibold text-sm">
              {vehicle.status}
            </span>
          </div>
        )}
        {/* Brand Badge */}
        <div className="absolute top-3 left-3 bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white px-4 py-1.5 rounded-full text-sm font-semibold shadow-lg">
          {vehicle.brand.name}
        </div>
      </div>

      <CardContent className="p-5 space-y-4">
        {/* Title and Year */}
        <div>
          <h3 className="text-lg font-bold text-[#4a3f35] group-hover:text-[#c89968] transition-colors">
            {vehicle.brand.name} {vehicle.model}
          </h3>
          <p className="text-sm text-[#8b7355] font-medium">{vehicle.modelYear}</p>
        </div>

        {/* Key Specs */}
        <div className="grid grid-cols-2 gap-3 text-sm">
          <div className="flex items-center gap-2 text-[#4a3f35]/80">
            <div className="h-8 w-8 rounded-lg bg-[#f5ede4] flex items-center justify-center">
              <Fuel className="h-4 w-4 text-[#c89968]" />
            </div>
            <span>{vehicle.fuelType}</span>
          </div>
          <div className="flex items-center gap-2 text-[#4a3f35]/80">
            <div className="h-8 w-8 rounded-lg bg-[#f5ede4] flex items-center justify-center">
              <Users className="h-4 w-4 text-[#c89968]" />
            </div>
            <span>{vehicle.seatingCapacity} seats</span>
          </div>
          <div className="flex items-center gap-2 text-[#4a3f35]/80">
            <div className="h-8 w-8 rounded-lg bg-[#f5ede4] flex items-center justify-center">
              <Package className="h-4 w-4 text-[#c89968]" />
            </div>
            <span className="text-xs">{vehicle.transmission}</span>
          </div>
          <div className="flex items-center gap-2 text-[#4a3f35]/80">
            <div className="h-8 w-8 rounded-lg bg-[#f5ede4] flex items-center justify-center">
              <Car className="h-4 w-4 text-[#c89968]" />
            </div>
            <span className="text-xs">{vehicle.horsepower}</span>
          </div>
        </div>

        {/* Description */}
        <p className="text-sm text-[#4a3f35]/70 line-clamp-2 leading-relaxed">{vehicle.description}</p>

        {/* Price */}
        <div className="pt-3 border-t border-[#e8d5c4]">
          <p className="text-2xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
            {formatCurrency(vehicle.basePrice)}
          </p>
          <p className="text-xs text-[#4a3f35]/60">Starting price</p>
        </div>
      </CardContent>

      <CardFooter className="p-5 pt-0 flex-col gap-3">
        <Button
          asChild
          className="w-full bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:from-[#d4a574] hover:to-[#c89968] text-white shadow-md"
          disabled={!isAvailable}
        >
          <Link href={`/vehicles/${vehicle.id}`}>View Details</Link>
        </Button>
        {isAvailable && vehicle.stockQuantity > 0 && (
          <div className="flex items-center justify-center gap-2 text-xs">
            <div className="h-2 w-2 rounded-full bg-green-500 animate-pulse" />
            <span className="text-green-600 font-medium">
              {vehicle.stockQuantity} available
            </span>
          </div>
        )}
      </CardFooter>
    </Card>
  );
}
