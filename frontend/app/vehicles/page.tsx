"use client";

import { useEffect, useState } from "react";
import { vehicleService } from "@/lib/api/vehicle.service";
import { Vehicle, VehicleSearchParams, Brand, VehicleType } from "@/types/vehicle.types";
import { VehicleCard } from "@/components/vehicles/vehicle-card";
import { VehicleFilters } from "@/components/vehicles/vehicle-filters";
import { Button } from "@/components/ui/button";
import { Loader2, ChevronLeft, ChevronRight } from "lucide-react";

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
    <div className="min-h-screen bg-[#fafaf8]">
      <div className="container mx-auto py-12 px-4">
        {/* Header */}
        <div className="mb-10 text-center">
          <h1 className="text-5xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent mb-3">
            Browse Vehicles
          </h1>
          <p className="text-[#8b7355] text-lg max-w-2xl mx-auto">
            Discover your perfect vehicle from our extensive collection of luxury automobiles
          </p>
        </div>

        {/* Filters */}
        <VehicleFilters
          onFilterChange={handleFilterChange}
          brands={brands}
          vehicleTypes={vehicleTypes}
        />

        {/* Loading State */}
        {loading && (
          <div className="flex justify-center items-center py-20">
            <Loader2 className="h-12 w-12 animate-spin text-[#c89968]" />
          </div>
        )}

        {/* Error State */}
        {error && !loading && (
          <div className="bg-red-50 border-2 border-red-200 rounded-2xl p-8 text-center shadow-sm">
            <p className="text-red-600 text-lg mb-4">{error}</p>
            <Button
              onClick={loadVehicles}
              className="bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white"
            >
              Try Again
            </Button>
          </div>
        )}

        {/* Results Count */}
        {!loading && !error && (
          <div className="mb-6 text-sm text-[#8b7355] font-medium">
            Showing <span className="text-[#c89968] font-bold">{vehicles.length}</span> of{" "}
            <span className="text-[#c89968] font-bold">{pagination.totalElements}</span> vehicles
          </div>
        )}

        {/* Vehicle Grid */}
        {!loading && !error && vehicles.length > 0 && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-10">
              {vehicles.map((vehicle: Vehicle) => (
                <VehicleCard key={vehicle.id} vehicle={vehicle} />
              ))}
            </div>

            {/* Pagination */}
            {pagination.totalPages > 1 && (
              <div className="flex items-center justify-center gap-3">
                <Button
                  variant="outline"
                  onClick={() => handlePageChange(pagination.currentPage - 1)}
                  disabled={pagination.currentPage === 0}
                  className="border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] disabled:opacity-40 disabled:cursor-not-allowed text-[#4a3f35]"
                >
                  <ChevronLeft className="h-4 w-4 mr-1" />
                  Previous
                </Button>

                <div className="flex items-center gap-2">
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
                            ? "bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white border-0"
                            : "border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] text-[#4a3f35]"
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
                  className="border-2 border-[#e8d5c4] hover:bg-[#f5ede4] hover:border-[#c89968] disabled:opacity-40 disabled:cursor-not-allowed text-[#4a3f35]"
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
          <div className="text-center py-20 bg-white rounded-2xl border-2 border-[#e8d5c4] shadow-sm">
            <p className="text-[#8b7355] text-lg mb-6">
              No vehicles found matching your criteria
            </p>
            <Button
              onClick={() => handleFilterChange({})}
              className="bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white"
            >
              Clear Filters
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

