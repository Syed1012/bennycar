"use client";

import { useEffect, useState } from "react";
import { vehicleService } from "@/lib/api/vehicle.service";
import { Vehicle, VehicleSearchParams, Brand, VehicleType } from "@/types/vehicle.types";
import { VehicleCard } from "@/components/vehicles/vehicle-card";
import { VehicleFilters } from "@/components/vehicles/vehicle-filters";
import { Button } from "@/components/ui/button";
import { Loader2, ChevronLeft, ChevronRight, Sparkles } from "lucide-react";

export default function VehiclesPage() {
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [brands, setBrands] = useState<Brand[]>([]);
  const [vehicleTypes, setVehicleTypes] = useState<VehicleType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<VehicleSearchParams>({ page: 0, size: 12 });
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 1,
    totalElements: 0,
  });

  useEffect(() => {
    loadBrandsAndTypes();
  }, []);

  useEffect(() => {
    loadVehicles();
  }, [filters]);

  const loadBrandsAndTypes = async () => {
    try {
      const [brandsData, typesData] = await Promise.all([
        vehicleService.getBrands(),
        vehicleService.getVehicleTypes(),
      ]);
      setBrands(brandsData);
      setVehicleTypes(typesData);
    } catch (err) {
      // Error loading brands and types - using defaults
    }
  };

  const loadVehicles = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await vehicleService.searchVehicles(filters);
      setVehicles(response.content);
      setPagination({
        currentPage: response.number,
        totalPages: response.totalPages,
        totalElements: response.totalElements,
      });
    } catch (err) {
      setError("Failed to load vehicles. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (newFilters: VehicleSearchParams) => {
    setFilters({ ...newFilters, page: 0, size: 12 });
  };

  const handlePageChange = (newPage: number) => {
    setFilters({ ...filters, page: newPage });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#fafaf8] via-white to-[#fafaf8]">
      {/* Hero Section */}
      <div className="relative overflow-hidden bg-gradient-to-br from-[#c89968]/10 via-[#d4a574]/5 to-transparent">
        <div className="absolute inset-0 bg-[url('/grid-pattern.svg')] opacity-[0.02]" />
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 pt-12 pb-8 relative">
          <div className="text-center max-w-3xl mx-auto">
            <div className="inline-flex items-center gap-2 mb-4 px-4 py-2 rounded-full bg-gradient-to-r from-[#c89968]/10 to-[#d4a574]/10 border border-[#c89968]/20">
              <Sparkles className="h-4 w-4 text-[#c89968]" />
              <span className="text-sm font-medium text-[#8b7355]">Premium Collection</span>
            </div>
            <h1 className="text-5xl md:text-6xl font-bold mb-4 bg-gradient-to-r from-[#4a3f35] via-[#c89968] to-[#4a3f35] bg-clip-text text-transparent leading-tight">
              Discover Your Dream Car
            </h1>
            <p className="text-lg md:text-xl text-[#8b7355] leading-relaxed">
              Explore our curated selection of luxury vehicles, each one a masterpiece of engineering and design
            </p>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 sm:px-6 lg:px-8 pb-16">
        {/* Compact Search and Filter Bar */}
        <div className="mb-8 -mt-4">
          <VehicleFilters
            onFilterChange={handleFilterChange}
            brands={brands}
            vehicleTypes={vehicleTypes}
          />
        </div>

        {/* Results Count */}
        {!loading && !error && (
          <div className="mb-6 flex items-center justify-between">
            <div className="text-sm text-[#8b7355]">
              <span className="font-semibold text-[#4a3f35]">{pagination.totalElements}</span>{" "}
              {pagination.totalElements === 1 ? "vehicle" : "vehicles"} found
            </div>
            {pagination.totalElements > 0 && (
              <div className="text-xs text-[#8b7355]/70">
                Page {pagination.currentPage + 1} of {pagination.totalPages}
              </div>
            )}
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="flex flex-col justify-center items-center py-24">
            <div className="relative">
              <div className="absolute inset-0 bg-gradient-to-r from-[#c89968] to-[#d4a574] rounded-full blur-xl opacity-30 animate-pulse" />
              <Loader2 className="h-16 w-16 animate-spin text-[#c89968] relative z-10" />
            </div>
            <p className="mt-6 text-[#8b7355] font-medium">Loading premium vehicles...</p>
          </div>
        )}

        {/* Error State */}
        {error && !loading && (
          <div className="max-w-md mx-auto mt-12">
            <div className="bg-gradient-to-br from-red-50 to-orange-50 border-2 border-red-200/50 rounded-3xl p-8 text-center shadow-xl backdrop-blur-sm">
              <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-red-100 flex items-center justify-center">
                <svg className="w-8 h-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
              </div>
              <p className="text-red-700 text-lg font-semibold mb-2">{error}</p>
              <p className="text-red-600/80 text-sm mb-6">We're having trouble loading vehicles right now.</p>
              <Button
                onClick={loadVehicles}
                className="bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white shadow-lg hover:shadow-xl transition-all"
              >
                Try Again
              </Button>
            </div>
          </div>
        )}

        {/* Vehicle Grid */}
        {!loading && !error && vehicles.length > 0 && (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-12">
              {vehicles.map((vehicle: Vehicle) => (
                <VehicleCard key={vehicle.id} vehicle={vehicle} />
              ))}
            </div>

            {/* Pagination */}
            {pagination.totalPages > 1 && (
              <div className="flex items-center justify-center gap-2">
                <Button
                  variant="outline"
                  onClick={() => handlePageChange(pagination.currentPage - 1)}
                  disabled={pagination.currentPage === 0}
                  className="border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] disabled:opacity-40 disabled:cursor-not-allowed text-[#4a3f35] transition-all"
                >
                  <ChevronLeft className="h-4 w-4 mr-1" />
                  Previous
                </Button>

                <div className="flex items-center gap-1">
                  {Array.from({ length: Math.min(5, pagination.totalPages) }, (_, i) => {
                    const page = i + Math.max(0, pagination.currentPage - 2);
                    if (page >= pagination.totalPages) return null;
                    return (
                      <Button
                        key={page}
                        variant={page === pagination.currentPage ? "default" : "outline"}
                        onClick={() => handlePageChange(page)}
                        className={
                          page === pagination.currentPage
                            ? "bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white border-0 min-w-[44px] shadow-md"
                            : "border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] text-[#4a3f35] min-w-[44px] transition-all"
                        }
                      >
                        {page + 1}
                      </Button>
                    );
                  })}
                </div>

                <Button
                  variant="outline"
                  onClick={() => handlePageChange(pagination.currentPage + 1)}
                  disabled={pagination.currentPage >= pagination.totalPages - 1}
                  className="border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] disabled:opacity-40 disabled:cursor-not-allowed text-[#4a3f35] transition-all"
                >
                  Next
                  <ChevronRight className="h-4 w-4 ml-1" />
                </Button>
              </div>
            )}
          </>
        )}

        {/* Empty State */}
        {!loading && !error && vehicles.length === 0 && (
          <div className="max-w-lg mx-auto mt-12">
            <div className="text-center bg-white rounded-3xl border-2 border-[#e8d5c4] shadow-xl p-12">
              <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4] flex items-center justify-center">
                <svg className="w-10 h-10 text-[#c89968]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <h3 className="text-xl font-bold text-[#4a3f35] mb-2">No vehicles found</h3>
              <p className="text-[#8b7355] mb-6">
                We couldn't find any vehicles matching your criteria. Try adjusting your filters.
              </p>
              <Button
                onClick={() => handleFilterChange({})}
                className="bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white shadow-lg hover:shadow-xl transition-all"
              >
                Clear All Filters
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
