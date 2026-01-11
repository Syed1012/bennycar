"use client";

import { useState, useRef, useEffect } from "react";
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
  const filterPanelRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        filterPanelRef.current &&
        !filterPanelRef.current.contains(event.target as Node) &&
        !(event.target as HTMLElement).closest('[data-filter-button]')
      ) {
        setShowFilters(false);
      }
    };

    if (showFilters) {
      document.addEventListener("mousedown", handleClickOutside);
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.body.style.overflow = "";
    };
  }, [showFilters]);

  const handleFilterChange = (key: keyof VehicleSearchParams, value: string | number) => {
    const newFilters = { ...filters, [key]: value || undefined };
    setFilters(newFilters);
    onFilterChange(newFilters);
  };

  const clearFilters = () => {
    const clearedFilters: VehicleSearchParams = {};
    setFilters(clearedFilters);
    onFilterChange(clearedFilters);
  };

  const activeFilterCount = Object.keys(filters).filter(
    (key) => filters[key as keyof VehicleSearchParams] !== undefined && filters[key as keyof VehicleSearchParams] !== ""
  ).length;

  return (
    <>
      {/* Compact Search Bar */}
      <div className="flex gap-3 items-center">
        <div className="relative flex-1 group">
          <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-[#8b7355]/50 group-focus-within:text-[#c89968] transition-colors" />
          <Input
            type="text"
            placeholder="Search by brand, model, or type..."
            className="pl-12 pr-4 h-12 border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 bg-white/80 backdrop-blur-sm shadow-sm hover:shadow-md transition-all rounded-xl"
            onChange={(e) => handleFilterChange("search", e.target.value)}
            value={filters.search || ""}
          />
        </div>
        
        {/* Icon-only Filter Button */}
        <Button
          data-filter-button
          variant="outline"
          onClick={() => setShowFilters(!showFilters)}
          className={`relative border-2 h-12 w-12 p-0 rounded-xl transition-all ${
            showFilters
              ? "bg-gradient-to-r from-[#c89968] to-[#d4a574] border-[#c89968] text-white shadow-lg"
              : "border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] text-[#4a3f35] hover:shadow-md"
          }`}
        >
          <SlidersHorizontal className="h-5 w-5" />
          {activeFilterCount > 0 && (
            <span className="absolute -top-1 -right-1 bg-red-500 text-white rounded-full h-5 w-5 flex items-center justify-center text-xs font-bold shadow-lg">
              {activeFilterCount}
            </span>
          )}
        </Button>
      </div>

      {/* Filter Panel Overlay */}
      {showFilters && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-start justify-end animate-in fade-in duration-200">
          <div
            ref={filterPanelRef}
            className="h-full w-full sm:w-[420px] bg-white shadow-2xl overflow-y-auto animate-in slide-in-from-right duration-300"
          >
            <div className="sticky top-0 bg-white border-b border-[#e8d5c4] z-10 p-6 flex items-center justify-between backdrop-blur-sm bg-white/95">
              <h3 className="text-xl font-bold text-[#4a3f35]">Filter Vehicles</h3>
              <div className="flex items-center gap-3">
                {activeFilterCount > 0 && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={clearFilters}
                    className="text-[#c89968] hover:bg-[#f5ede4] text-sm"
                  >
                    <X className="h-4 w-4 mr-1" />
                    Clear
                  </Button>
                )}
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setShowFilters(false)}
                  className="text-[#4a3f35] hover:bg-[#f5ede4]"
                >
                  <X className="h-5 w-5" />
                </Button>
              </div>
            </div>

            <div className="p-6 space-y-6">
              {/* Brand Filter */}
              <div className="space-y-2">
                <Label className="text-[#4a3f35] font-semibold text-sm">Brand</Label>
                <select
                  className="w-full rounded-xl border-2 border-[#e8d5c4] bg-[#fafaf8] p-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 text-[#4a3f35] transition-all"
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
                  className="w-full rounded-xl border-2 border-[#e8d5c4] bg-[#fafaf8] p-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 text-[#4a3f35] transition-all"
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
                  placeholder="e.g., 2024"
                  className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 bg-[#fafaf8] h-11 rounded-xl transition-all"
                  onChange={(e) =>
                    handleFilterChange("modelYear", parseInt(e.target.value) || 0)
                  }
                  value={filters.modelYear || ""}
                />
              </div>

              {/* Price Range */}
              <div className="space-y-3">
                <Label className="text-[#4a3f35] font-semibold text-sm">Price Range</Label>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1">
                    <Label className="text-xs text-[#8b7355]">Min Price</Label>
                    <Input
                      type="number"
                      placeholder="0"
                      className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 bg-[#fafaf8] h-10 rounded-xl text-sm transition-all"
                      onChange={(e) =>
                        handleFilterChange("minPrice", parseFloat(e.target.value) || 0)
                      }
                      value={filters.minPrice || ""}
                    />
                  </div>
                  <div className="space-y-1">
                    <Label className="text-xs text-[#8b7355]">Max Price</Label>
                    <Input
                      type="number"
                      placeholder="100000"
                      className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 bg-[#fafaf8] h-10 rounded-xl text-sm transition-all"
                      onChange={(e) =>
                        handleFilterChange("maxPrice", parseFloat(e.target.value) || 0)
                      }
                      value={filters.maxPrice || ""}
                    />
                  </div>
                </div>
              </div>

              {/* Status */}
              <div className="space-y-2">
                <Label className="text-[#4a3f35] font-semibold text-sm">Availability</Label>
                <select
                  className="w-full rounded-xl border-2 border-[#e8d5c4] bg-[#fafaf8] p-3 text-sm focus:border-[#c89968] focus:ring-2 focus:ring-[#c89968]/20 text-[#4a3f35] transition-all"
                  onChange={(e) => handleFilterChange("status", e.target.value)}
                  value={filters.status || ""}
                >
                  <option value="">All</option>
                  <option value="AVAILABLE">Available</option>
                  <option value="SOLD_OUT">Sold Out</option>
                  <option value="DISCONTINUED">Discontinued</option>
                </select>
              </div>

              {/* Apply Button */}
              <div className="pt-4 border-t border-[#e8d5c4]">
                <Button
                  onClick={() => setShowFilters(false)}
                  className="w-full bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white shadow-lg hover:shadow-xl transition-all rounded-xl h-12 font-semibold"
                >
                  Apply Filters
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
