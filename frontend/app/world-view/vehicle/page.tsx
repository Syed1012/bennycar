'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import dynamic from 'next/dynamic';
import { motion } from 'framer-motion';
import { worldViewApi } from '@/lib/world-view/api';
import { VehicleDataDto, VehicleType, MapPosition } from '@/lib/world-view/types';
import VehicleStatusPanel from '@/components/world-view/VehicleStatusPanel';

// Dynamically import VehicleMapView to avoid SSR issues with Leaflet
const VehicleMapView = dynamic(() => import('@/components/world-view/VehicleMapView'), {
  ssr: false,
  loading: () => (
    <div className="w-full h-full bg-gray-100 flex items-center justify-center">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-gray-500">Loading map...</p>
      </div>
    </div>
  ),
});

export default function VehiclePage() {
  const [vehicleData, setVehicleData] = useState<VehicleDataDto | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [vehiclePosition, setVehiclePosition] = useState<MapPosition | null>(null);

  const vehicleCleanupRef = useRef<(() => void) | null>(null);

  const fetchVehicleData = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await worldViewApi.refreshVehicleData();
      setVehicleData(data);
      if (data.location) {
        setVehiclePosition({
          lat: data.location.location.latitude,
          lng: data.location.location.longitude,
        });
      }

      // Subscribe to vehicle MQTT updates
      if (vehicleCleanupRef.current) {
        vehicleCleanupRef.current();
      }
      vehicleCleanupRef.current = worldViewApi.subscribeToVehicle(
        data.location.vehicle_id,
        (vehicleUpdate) => {
          setVehicleData(vehicleUpdate);
          if (vehicleUpdate.location) {
            setVehiclePosition({
              lat: vehicleUpdate.location.location.latitude,
              lng: vehicleUpdate.location.location.longitude,
            });
          }
        },
        (err) => {
          console.error('Vehicle MQTT error:', err);
        }
      );
    } catch (err) {
      console.error('Failed to fetch vehicle data:', err);
      setError('Failed to load vehicle data. Is the world-view backend running?');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchVehicleData();

    return () => {
      if (vehicleCleanupRef.current) {
        vehicleCleanupRef.current();
      }
    };
  }, [fetchVehicleData]);

  const handleVehicleTypeChange = useCallback(async (type: VehicleType) => {
    try {
      const data = await worldViewApi.setVehicleType(type);
      setVehicleData(data);
      if (data.location) {
        setVehiclePosition({
          lat: data.location.location.latitude,
          lng: data.location.location.longitude,
        });
      }
    } catch (err) {
      console.error('Failed to change vehicle type:', err);
      setError('Failed to update vehicle type');
    }
  }, []);

  const handleRefresh = useCallback(() => {
    fetchVehicleData();
  }, [fetchVehicleData]);

  return (
    <div className="min-h-[calc(100vh-5rem)] bg-gradient-to-br from-gray-50 via-white to-gray-50">
      <div className="flex flex-col lg:flex-row h-[calc(100vh-5rem)]">
        {/* Map area */}
        <div className="flex-1 relative min-h-[60vh] lg:min-h-0 lg:h-full">
          <VehicleMapView
            vehiclePosition={vehiclePosition}
            locationName={vehicleData?.location?.location_name || 'Unknown'}
            isLoading={isLoading}
          />

          {error && (
            <motion.div
              className="absolute top-4 left-4 right-4 md:left-auto md:right-4 md:w-96 bg-white border-2 border-red-300 text-red-800 p-4 rounded-xl shadow-xl z-[1000]"
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
            >
              <div className="flex items-start justify-between">
                <p className="text-sm font-medium">{error}</p>
                <button
                  onClick={() => setError(null)}
                  className="text-red-400 hover:text-red-600 ml-2 transition-colors"
                >
                  ✕
                </button>
              </div>
            </motion.div>
          )}
        </div>

        {/* Vehicle Status Panel */}
        <div className="lg:w-[420px] bg-white/95 backdrop-blur-sm border-t lg:border-t-0 lg:border-l border-gray-200/50 shadow-xl overflow-y-auto lg:h-full">
          <div className="p-6">
            <VehicleStatusPanel
              vehicleData={vehicleData}
              isLoading={isLoading}
              onVehicleTypeChange={handleVehicleTypeChange}
              onRefresh={handleRefresh}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
