'use client';

import { useMemo } from 'react';
import L from 'leaflet';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import { motion } from 'framer-motion';
import { MapPosition } from '@/lib/world-view/types';
import { useEffect } from 'react';

interface VehicleMapViewProps {
  vehiclePosition: MapPosition | null;
  locationName: string;
  isLoading: boolean;
}

function MapCenterer({ position }: { position: MapPosition | null }) {
  const map = useMap();

  useEffect(() => {
    if (position) {
      map.setView([position.lat, position.lng], 15, {
        animate: true,
        duration: 0.5,
      });
    }
  }, [position, map]);

  return null;
}

const vehicleIcon = L.divIcon({
  className: 'vehicle-marker',
  html: `
    <div style="
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
    ">
      <div style="
        position: absolute;
        width: 60px;
        height: 60px;
        background: rgba(66, 133, 244, 0.2);
        border-radius: 50%;
        animation: pulse 2s ease-in-out infinite;
      "></div>
      <div style="
        width: 44px;
        height: 44px;
        background: linear-gradient(135deg, #4285F4 0%, #1A73E8 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(66, 133, 244, 0.4);
        border: 3px solid white;
      ">
        <span style="font-size: 22px;">🚗</span>
      </div>
    </div>
  `,
  iconSize: [60, 60],
  iconAnchor: [30, 30],
  popupAnchor: [0, -20],
});

const DEFAULT_CENTER: [number, number] = [48.7758, 9.1829];

export default function VehicleMapView({
  vehiclePosition,
  locationName,
  isLoading,
}: VehicleMapViewProps) {
  const mapCenter: [number, number] = useMemo(() => {
    return vehiclePosition
      ? [vehiclePosition.lat, vehiclePosition.lng]
      : DEFAULT_CENTER;
  }, [vehiclePosition]);

  return (
    <div className="relative w-full h-full">
      {isLoading && (
        <motion.div
          className="absolute inset-0 z-[1000] bg-white/80 flex items-center justify-center"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <div className="text-center">
            <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
            <p className="text-gray-600">Locating your vehicle...</p>
          </div>
        </motion.div>
      )}

      <MapContainer
        center={mapCenter}
        zoom={15}
        className="w-full h-full"
        zoomControl={true}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        <MapCenterer position={vehiclePosition} />

        {vehiclePosition && (
          <Marker
            position={[vehiclePosition.lat, vehiclePosition.lng]}
            icon={vehicleIcon}
          >
            <Popup>
              <div className="text-center p-2">
                <div className="text-2xl mb-1">🚗</div>
                <p className="font-semibold text-gray-800">Your Vehicle</p>
                <p className="text-sm text-gray-600">{locationName}</p>
                <p className="text-xs text-gray-400 mt-1">
                  {vehiclePosition.lat.toFixed(6)}, {vehiclePosition.lng.toFixed(6)}
                </p>
              </div>
            </Popup>
          </Marker>
        )}
      </MapContainer>

      {vehiclePosition && !isLoading && (
        <motion.div
          className="absolute bottom-4 left-4 bg-white rounded-lg shadow-lg p-4 z-[1000] max-w-xs"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
              <span className="text-xl">📍</span>
            </div>
            <div>
              <p className="text-xs text-gray-500 uppercase tracking-wide">Current Location</p>
              <p className="font-semibold text-gray-800">{locationName}</p>
            </div>
          </div>
        </motion.div>
      )}

      <style jsx global>{`
        @keyframes pulse {
          0%, 100% {
            transform: scale(1);
            opacity: 0.6;
          }
          50% {
            transform: scale(1.3);
            opacity: 0.2;
          }
        }
      `}</style>
    </div>
  );
}
