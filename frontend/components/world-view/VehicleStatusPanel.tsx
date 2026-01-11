'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { VehicleDataDto, VehicleType } from '@/lib/world-view/types';
import { RefreshCw, ChevronDown, ChevronUp } from 'lucide-react';

interface VehicleStatusPanelProps {
  vehicleData: VehicleDataDto | null;
  isLoading: boolean;
  onVehicleTypeChange: (type: VehicleType) => void;
  onRefresh: () => void;
}

const vehicleTypeLabels: Record<VehicleType, { label: string; icon: string }> = {
  EV: { label: 'Electric', icon: '⚡' },
  HYBRID: { label: 'Hybrid', icon: '🔋' },
  PETROL: { label: 'Petrol', icon: '⛽' },
  DIESEL: { label: 'Diesel', icon: '🛢️' },
};

const vehicleTypeColors: Record<VehicleType, string> = {
  EV: 'bg-green-100 text-green-800 border-green-300',
  HYBRID: 'bg-blue-100 text-blue-800 border-blue-300',
  PETROL: 'bg-orange-100 text-orange-800 border-orange-300',
  DIESEL: 'bg-gray-100 text-gray-800 border-gray-300',
};

export default function VehicleStatusPanel({
  vehicleData,
  isLoading,
  onVehicleTypeChange,
  onRefresh,
}: VehicleStatusPanelProps) {
  const [selectedType, setSelectedType] = useState<VehicleType>('PETROL');
  const [showTires, setShowTires] = useState(false);
  const [showWarnings, setShowWarnings] = useState(true);

  const status = vehicleData?.status;
  const location = vehicleData?.location;

  useEffect(() => {
    if (status?.vehicle_type) {
      setSelectedType(status.vehicle_type);
    }
  }, [status?.vehicle_type]);

  const handleTypeChange = (type: VehicleType) => {
    setSelectedType(type);
    onVehicleTypeChange(type);
  };

  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  const getProgressColor = (value: number, warning: number, critical: number) => {
    if (value <= critical) return 'bg-red-500';
    if (value <= warning) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  const hasWarnings = status && (status.engine_warning || status.battery_warning || status.service_warning);

  if (isLoading && !vehicleData) {
    return (
      <div className="space-y-4">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded mb-4" />
          <div className="h-24 bg-gray-100 rounded mb-4" />
          <div className="h-32 bg-gray-100 rounded" />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Header with Refresh */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-2xl">🚗</span>
          <h2 className="text-xl font-bold text-gray-800">Your Vehicle</h2>
        </div>
        <motion.button
          onClick={onRefresh}
          className="p-2 rounded-full hover:bg-gray-100 transition-colors"
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
          disabled={isLoading}
        >
          <RefreshCw className={`w-5 h-5 text-gray-600 ${isLoading ? 'animate-spin' : ''}`} />
        </motion.button>
      </div>

      {/* Location Card */}
      {location && (
        <motion.div
          className="bg-gradient-to-r from-blue-50 to-blue-100 rounded-xl p-4 border border-blue-200"
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center">
              <span className="text-2xl">📍</span>
            </div>
            <div>
              <p className="text-xs text-blue-600 font-medium uppercase tracking-wide">Current Location</p>
              <p className="text-lg font-semibold text-gray-800">{location.location_name}</p>
              <p className="text-xs text-gray-500">
                {location.location.latitude.toFixed(4)}, {location.location.longitude.toFixed(4)}
              </p>
            </div>
          </div>
        </motion.div>
      )}

      {/* Vehicle Type Selector */}
      <div className="bg-gray-50 rounded-xl p-4">
        <label className="text-xs font-medium text-gray-500 mb-3 block uppercase tracking-wide">
          Vehicle Type
        </label>
        <div className="grid grid-cols-2 gap-2">
          {(Object.keys(vehicleTypeLabels) as VehicleType[]).map((type) => (
            <button
              key={type}
              onClick={() => handleTypeChange(type)}
              className={`px-3 py-2.5 rounded-lg text-sm font-medium border transition-all flex items-center justify-center gap-2 ${
                selectedType === type
                  ? vehicleTypeColors[type]
                  : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50'
              }`}
            >
              <span>{vehicleTypeLabels[type].icon}</span>
              <span>{vehicleTypeLabels[type].label}</span>
            </button>
          ))}
        </div>
      </div>

      {status && (
        <>
          {/* Warnings Section */}
          {hasWarnings && (
            <motion.div
              className="bg-red-50 border border-red-200 rounded-xl p-4"
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
            >
              <button
                onClick={() => setShowWarnings(!showWarnings)}
                className="w-full flex items-center justify-between"
              >
                <div className="flex items-center gap-2">
                  <span className="text-xl">⚠️</span>
                  <span className="font-medium text-red-800">Warnings</span>
                </div>
                {showWarnings ? (
                  <ChevronUp className="w-4 h-4 text-red-600" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-red-600" />
                )}
              </button>
              <AnimatePresence>
                {showWarnings && (
                  <motion.div
                    initial={{ height: 0, opacity: 0 }}
                    animate={{ height: 'auto', opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }}
                    className="mt-3 space-y-2"
                  >
                    {status.engine_warning && (
                      <div className="flex items-center gap-2 text-sm text-red-700">
                        <span>🔧</span>
                        <span>Engine needs attention</span>
                      </div>
                    )}
                    {status.battery_warning && (
                      <div className="flex items-center gap-2 text-sm text-red-700">
                        <span>🔋</span>
                        <span>Low battery - charge soon</span>
                      </div>
                    )}
                    {status.service_warning && (
                      <div className="flex items-center gap-2 text-sm text-red-700">
                        <span>🛠️</span>
                        <span>Service due soon</span>
                      </div>
                    )}
                  </motion.div>
                )}
              </AnimatePresence>
            </motion.div>
          )}

          {/* Fuel/Battery Levels */}
          <div className="space-y-4">
            {status.has_fuel && status.fuel_percentage !== null && (
              <div className="bg-white rounded-xl p-4 border border-gray-200">
                <div className="flex justify-between items-center mb-2">
                  <span className="font-medium text-gray-800 flex items-center gap-2">
                    <span>⛽</span> Fuel Level
                  </span>
                  <span className="text-lg font-bold text-gray-800">
                    {status.fuel_percentage}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3 mb-2">
                  <div
                    className={`h-3 rounded-full transition-all ${getProgressColor(
                      status.fuel_percentage,
                      30,
                      15
                    )}`}
                    style={{ width: `${status.fuel_percentage}%` }}
                  />
                </div>
                <div className="flex justify-between text-xs text-gray-500">
                  <span>{status.fuel_liters?.toFixed(1)}L / {status.fuel_capacity_liters}L</span>
                  <span>Range: {status.fuel_range_km?.toFixed(0)} km</span>
                </div>
              </div>
            )}

            {status.has_battery && status.battery_percentage !== null && (
              <div className="bg-white rounded-xl p-4 border border-gray-200">
                <div className="flex justify-between items-center mb-2">
                  <span className="font-medium text-gray-800 flex items-center gap-2">
                    <span>🔋</span> Battery Level
                    {status.is_charging && (
                      <span className="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full">
                        ⚡ Charging
                      </span>
                    )}
                  </span>
                  <span className="text-lg font-bold text-gray-800">
                    {status.battery_percentage}%
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3 mb-2">
                  <div
                    className={`h-3 rounded-full transition-all ${getProgressColor(
                      status.battery_percentage,
                      30,
                      15
                    )}`}
                    style={{ width: `${status.battery_percentage}%` }}
                  />
                </div>
                <div className="flex justify-between text-xs text-gray-500">
                  <span>Range: {status.battery_range_km?.toFixed(0)} km</span>
                  {status.is_charging && status.estimated_charge_time_minutes && (
                    <span>Full in {status.estimated_charge_time_minutes} min</span>
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Maintenance Section */}
          <div className="bg-white rounded-xl p-4 border border-gray-200">
            <h3 className="font-medium text-gray-800 mb-3 flex items-center gap-2">
              <span>🔧</span> Maintenance
            </h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-gray-600">Engine Oil</span>
                <span className={`text-sm font-medium ${
                  status.engine_oil_status === 'Good' ? 'text-green-600' : 'text-yellow-600'
                }`}>
                  {status.engine_oil_status} ({status.engine_oil_percentage}%)
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-gray-600">Next Service</span>
                <span className={`text-sm font-medium ${
                  status.service_warning ? 'text-yellow-600' : 'text-gray-800'
                }`}>
                  {formatDate(status.next_service_date)}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-gray-600">Distance to Service</span>
                <span className="text-sm font-medium text-gray-800">
                  {status.km_until_service.toLocaleString()} km
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-gray-600">Overall Condition</span>
                <span className={`text-sm font-medium ${
                  status.overall_condition === 'Excellent' ? 'text-green-600' : 'text-yellow-600'
                }`}>
                  {status.overall_condition}
                </span>
              </div>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-gray-50 rounded-xl p-3 text-center">
              <p className="text-xs text-gray-500 uppercase tracking-wide">Odometer</p>
              <p className="text-lg font-bold text-gray-800">
                {status.odometer_km.toLocaleString()}
              </p>
              <p className="text-xs text-gray-400">km</p>
            </div>
            <div className="bg-gray-50 rounded-xl p-3 text-center">
              <p className="text-xs text-gray-500 uppercase tracking-wide">Total Range</p>
              <p className="text-lg font-bold text-gray-800">
                {status.total_range_km.toFixed(0)}
              </p>
              <p className="text-xs text-gray-400">km</p>
            </div>
            <div className="bg-gray-50 rounded-xl p-3 text-center">
              <p className="text-xs text-gray-500 uppercase tracking-wide">Engine Temp</p>
              <p className="text-lg font-bold text-gray-800">
                {status.engine_temperature_celsius.toFixed(0)}°C
              </p>
            </div>
            <div className="bg-gray-50 rounded-xl p-3 text-center">
              <p className="text-xs text-gray-500 uppercase tracking-wide">Outside Temp</p>
              <p className="text-lg font-bold text-gray-800">
                {status.outside_temperature_celsius.toFixed(0)}°C
              </p>
            </div>
          </div>

          {/* Tire Pressures */}
          <div className="bg-white rounded-xl p-4 border border-gray-200">
            <button
              onClick={() => setShowTires(!showTires)}
              className="w-full flex items-center justify-between"
            >
              <span className="font-medium text-gray-800 flex items-center gap-2">
                <span>🛞</span> Tire Pressure
              </span>
              <div className="flex items-center gap-2">
                <span className={`text-sm font-medium ${
                  status.tire_pressure_status === 'Normal' ? 'text-green-600' : 'text-yellow-600'
                }`}>
                  {status.tire_pressure_status}
                </span>
                {showTires ? (
                  <ChevronUp className="w-4 h-4 text-gray-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-gray-400" />
                )}
              </div>
            </button>
            <AnimatePresence>
              {showTires && (
                <motion.div
                  initial={{ height: 0, opacity: 0 }}
                  animate={{ height: 'auto', opacity: 1 }}
                  exit={{ height: 0, opacity: 0 }}
                  className="mt-4"
                >
                  <div className="grid grid-cols-2 gap-2 text-center text-sm">
                    <div className="bg-gray-50 rounded-lg p-3">
                      <p className="text-xs text-gray-500">Front Left</p>
                      <p className="font-bold text-gray-800">{status.tire_pressure_front_left.toFixed(1)} PSI</p>
                    </div>
                    <div className="bg-gray-50 rounded-lg p-3">
                      <p className="text-xs text-gray-500">Front Right</p>
                      <p className="font-bold text-gray-800">{status.tire_pressure_front_right.toFixed(1)} PSI</p>
                    </div>
                    <div className="bg-gray-50 rounded-lg p-3">
                      <p className="text-xs text-gray-500">Rear Left</p>
                      <p className="font-bold text-gray-800">{status.tire_pressure_rear_left.toFixed(1)} PSI</p>
                    </div>
                    <div className="bg-gray-50 rounded-lg p-3">
                      <p className="text-xs text-gray-500">Rear Right</p>
                      <p className="font-bold text-gray-800">{status.tire_pressure_rear_right.toFixed(1)} PSI</p>
                    </div>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Last Updated */}
          <p className="text-xs text-center text-gray-400">
            Last updated: {new Date(status.last_updated).toLocaleTimeString()}
          </p>
        </>
      )}
    </div>
  );
}
