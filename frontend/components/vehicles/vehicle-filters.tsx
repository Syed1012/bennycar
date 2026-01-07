"use client";

import { useState } from "react";
import { Search, SlidersHorizontal, X } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { VehicleSearchParams } from "@/types/vehicle.types";

interface VehicleFiltersProps {
  onFilterChange: (filters: VehicleSearchParams) => void;
  brands?: Array<{ id: string; name: string }>;
  vehicleTypes?: Array<{ id: string; name: string }>;
}

export function VehicleFilters({
  onFilterChange,
  brands = [],
  vehicleTypes = [],
}: VehicleFiltersProps) {
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState<VehicleSearchParams>({});

  const handleFilterChange = (key: keyof VehicleSearchParams, value: string | number) => {
    const newFilters = { ...filters, [key]: value || undefined };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const clearFilters = () => {
    setFilters({});
    onFilterChange({});
  };

  const activeFilterCount = Object.keys(filters).filter(
    (key) => filters[key as keyof VehicleSearchParams] !== undefined
  ).length;

  return (
    <div className="space-y-4">
      {/* Search and Filter Toggle */}
      <div className="flex gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-[#8b7355]/50" />
          <Input
            type="text"
            placeholder="Search by brand, model, or type..."
            className="pl-11 h-12 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] bg-white"
            onChange={(e) => {
              console.log("Search:", e.target.value);
            }}
          />
        </div>
        <Button
          variant="outline"
          onClick={() => setShowFilters(!showFilters)}
          className="border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] text-[#4a3f35] h-12 px-6"
        >
          <SlidersHorizontal className="h-5 w-5 mr-2" />
          Filters
          {activeFilterCount > 0 && (
            <span className="ml-2 bg-linear-to-r from-[#c89968] to-[#d4a574] text-white rounded-full px-2.5 py-0.5 text-xs font-semibold">
              {activeFilterCount}
            </span>
          )}
        </Button>
      </div>

      {/* Filter Panel */}
      {showFilters && (
        <div className="bg-white rounded-2xl p-6 space-y-6 border-2 border-[#e8d5c4] shadow-lg">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-lg font-bold text-[#4a3f35]">Filter Vehicles</h3>
            {activeFilterCount > 0 && (
              <Button
                variant="ghost"
                size="sm"
                onClick={clearFilters}
                className="text-[#c89968] hover:bg-[#f5ede4]"
              >
                <X className="h-4 w-4 mr-1" />
                Clear All
              </Button>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Brand Filter */}
            <div className="space-y-2">
              <Label className="text-[#4a3f35] font-semibold text-sm">Brand</Label>
              <select
                className="w-full rounded-lg border-2 border-[#e8d5c4] bg-[#fafaf8] p-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 text-[#4a3f35]"
                onChange={(e) => handleFilterChange("brandId", e.target.value)}
                value={filters.brandId || ""}
              >
                <option value="">All Brands</option>
                {brands.map((brand) => (
                  <option key={brand.id} value={brand.id}>
                    {brand.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Vehicle Type Filter */}
            <div className="space-y-2">
              <Label className="text-[#4a3f35] font-semibold text-sm">Vehicle Type</Label>
              <select
                className="w-full rounded-lg border-2 border-[#e8d5c4] bg-[#fafaf8] p-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 text-[#4a3f35]"
                onChange={(e) => handleFilterChange("vehicleTypeId", e.target.value)}
                value={filters.vehicleTypeId || ""}
              >
                <option value="">All Types</option>
                {vehicleTypes.map((type) => (
                  <option key={type.id} value={type.id}>
                    {type.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Model Year */}
            <div className="space-y-2">
              <Label className="text-[#4a3f35] font-semibold text-sm">Model Year</Label>
              <Input
                type="number"
                placeholder="2024"
                className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] bg-[#fafaf8] h-11"
                onChange={(e) =>
                  handleFilterChange("modelYear", parseInt(e.target.value) || 0)
                }
                value={filters.modelYear || ""}
              />
            </div>

            {/* Min Price */}
            <div className="space-y-2">
              <Label className="text-[#4a3f35] font-semibold text-sm">Min Price ($)</Label>
              <Input
                type="number"
                placeholder="0"
                className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] bg-[#fafaf8] h-11"
                onChange={(e) =>
                  handleFilterChange("minPrice", parseFloat(e.target.value) || 0)
                }
                value={filters.minPrice || ""}
              />
            </div>

            {/* Max Price */}
            <div className="space-y-2">
              <Label className="text-[#4a3f35] font-semibold text-sm">Max Price ($)</Label>
              <Input
                type="number"
                placeholder="100000"
                className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] bg-[#fafaf8] h-11"
                onChange={(e) =>
                  handleFilterChange("maxPrice", parseFloat(e.target.value) || 0)
                }
                value={filters.maxPrice || ""}
              />
            </div>

            {/* Status */}
            <div className="space-y-2">
              <Label className="text-[#4a3f35] font-semibold text-sm">Availability</Label>
              <select
                className="w-full rounded-lg border-2 border-[#e8d5c4] bg-[#fafaf8] p-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 text-[#4a3f35]"
                onChange={(e) => handleFilterChange("status", e.target.value)}
                value={filters.status || ""}
              >
                <option value="">All</option>
                <option value="AVAILABLE">Available</option>
                <option value="SOLD_OUT">Sold Out</option>
                <option value="DISCONTINUED">Discontinued</option>
              </select>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
