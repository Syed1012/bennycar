'use client';

import { motion } from 'framer-motion';
import { RouteDto } from '@/lib/world-view/types';
import { MapPin, Navigation, Clock, Ruler, Route } from 'lucide-react';

interface AvailableRoutesProps {
  routes: RouteDto[];
  currentRouteId: string | null;
  isLoading: boolean;
}

export default function RouteSelection({
  routes,
  currentRouteId,
  isLoading,
}: AvailableRoutesProps) {

  const formatDistance = (meters: number) => {
    return `${(meters / 1000).toFixed(1)} km`;
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    if (mins >= 60) {
      const hrs = Math.floor(mins / 60);
      const remainingMins = mins % 60;
      return `${hrs}h ${remainingMins}m`;
    }
    return `${mins} min`;
  };

  if (isLoading) {
    return (
      <div className="bg-white rounded-xl p-4 border border-gray-200 shadow-sm">
        <div className="flex items-center justify-center py-8">
          <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
        </div>
      </div>
    );
  }

  return (
    <motion.div
      className="bg-gradient-to-br from-white to-gray-50 rounded-2xl p-6 border border-gray-200/50 shadow-lg"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="flex items-center gap-2 mb-2">
        <Route className="w-5 h-5 text-[#4285F4]" />
        <h2 className="text-lg font-semibold text-[#202124]">Available Routes</h2>
      </div>

      <p className="text-sm text-[#5F6368] mb-4">
        Routes are automatically selected by the system
      </p>

      <div className="space-y-3 max-h-64 overflow-y-auto">
        {routes.map((route, index) => {
          const isCurrentRoute = currentRouteId === route.id;

          return (
            <motion.div
              key={route.id}
              className={`p-4 rounded-lg border transition-all ${
                isCurrentRoute
                  ? 'bg-[#E8F0FE] border-[#4285F4] ring-2 ring-[#4285F4]/20'
                  : 'bg-[#F8F9FA] border-[#E8EAED]'
              }`}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.05 }}
            >
              <div className="flex items-start justify-between mb-2">
                <div className="flex items-center gap-2">
                  <MapPin className={`w-4 h-4 ${isCurrentRoute ? 'text-[#4285F4]' : 'text-[#5F6368]'}`} />
                  <span className={`font-medium ${isCurrentRoute ? 'text-[#4285F4]' : 'text-[#202124]'}`}>
                    {route.name}
                  </span>
                </div>
                {isCurrentRoute && (
                  <span className="flex items-center gap-1 text-xs bg-[#4285F4] text-white px-2 py-1 rounded-full animate-pulse">
                    <span className="w-2 h-2 bg-white rounded-full" />
                    Active
                  </span>
                )}
              </div>

              <p className="text-sm text-[#5F6368] mb-2 line-clamp-1">{route.description}</p>

              <div className="flex items-center gap-4 text-xs text-[#80868B]">
                <div className="flex items-center gap-1">
                  <Ruler className="w-3 h-3" />
                  <span>{formatDistance(route.total_distance_meters)}</span>
                </div>
                <div className="flex items-center gap-1">
                  <Clock className="w-3 h-3" />
                  <span>{formatTime(route.estimated_duration_seconds)}</span>
                </div>
              </div>
            </motion.div>
          );
        })}
      </div>

      {routes.length === 0 && (
        <div className="text-center py-8 text-gray-400">
          <Navigation className="w-12 h-12 mx-auto mb-2 opacity-50" />
          <p>No routes available</p>
        </div>
      )}

      <div className="mt-4 pt-3 border-t border-[#E8EAED]">
        <p className="text-xs text-center text-[#80868B]">
          {routes.length} routes available • Destination: Dealership
        </p>
      </div>
    </motion.div>
  );
}
