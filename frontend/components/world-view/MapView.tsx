'use client';

import { useEffect, useRef, useState, useMemo } from 'react';
import L from 'leaflet';
import { MapContainer, TileLayer, Polyline, Marker, Popup, useMap } from 'react-leaflet';
import { motion } from 'framer-motion';
import { MapPosition, JourneyStatus } from '@/lib/world-view/types';

interface MapViewProps {
  currentPosition: MapPosition | null;
  destination: MapPosition;
  startPoint: MapPosition;
  waypoints: MapPosition[];
  status: JourneyStatus;
  carRotation?: number;
}

function MapFollower({ position, shouldFollow }: { position: MapPosition | null; shouldFollow: boolean }) {
  const map = useMap();
  
  useEffect(() => {
    if (position && shouldFollow) {
      map.setView([position.lat, position.lng], map.getZoom(), {
        animate: true,
        duration: 0.5,
      });
    }
  }, [position, shouldFollow, map]);
  
  return null;
}

const createCarIcon = (rotation: number = 0) => {
  return L.divIcon({
    className: 'car-marker',
    html: `
      <div style="
        transform: rotate(${rotation}deg);
        font-size: 32px;
        filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
        transition: transform 0.3s ease;
      ">
        🚗
      </div>
    `,
    iconSize: [40, 40],
    iconAnchor: [20, 20],
  });
};

const dealershipIcon = L.divIcon({
  className: 'dealership-marker',
  html: `
    <div style="
      background: #EA4335;
      border-radius: 50% 50% 50% 0;
      transform: rotate(-45deg);
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 6px rgba(234, 67, 53, 0.5);
      border: 2px solid white;
    ">
      <span style="transform: rotate(45deg); font-size: 16px;">🏁</span>
    </div>
  `,
  iconSize: [36, 36],
  iconAnchor: [18, 36],
});

const startIcon = L.divIcon({
  className: 'start-marker',
  html: `
    <div style="
      background: #34A853;
      border-radius: 50% 50% 50% 0;
      transform: rotate(-45deg);
      width: 28px;
      height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 6px rgba(52, 168, 83, 0.5);
      border: 2px solid white;
    ">
      <span style="transform: rotate(45deg); font-size: 12px;">📍</span>
    </div>
  `,
  iconSize: [28, 28],
  iconAnchor: [14, 28],
});

export default function MapView({
  currentPosition,
  destination,
  startPoint,
  waypoints,
  status,
  carRotation = 0,
}: MapViewProps) {
  const [followCar, setFollowCar] = useState(true);
  const mapRef = useRef<L.Map | null>(null);

  const routeCoordinates = useMemo(() => {
    return waypoints.map((wp) => [wp.lat, wp.lng] as [number, number]);
  }, [waypoints]);

  const findNearestWaypointIndex = (position: MapPosition): number => {
    if (waypoints.length === 0) return -1;
    
    let minDistance = Infinity;
    let nearestIdx = 0;
    
    waypoints.forEach((wp, idx) => {
      const distance = Math.sqrt(
        Math.pow(wp.lat - position.lat, 2) + 
        Math.pow(wp.lng - position.lng, 2)
      );
      if (distance < minDistance) {
        minDistance = distance;
        nearestIdx = idx;
      }
    });
    
    return nearestIdx;
  };

  const remainingRoute = useMemo(() => {
    if (!currentPosition || routeCoordinates.length === 0) return routeCoordinates;
    
    const nearestIdx = findNearestWaypointIndex(currentPosition);
    const remainingWaypoints = routeCoordinates.slice(nearestIdx);
    
    return [[currentPosition.lat, currentPosition.lng], ...remainingWaypoints] as [number, number][];
  }, [currentPosition, waypoints, routeCoordinates]);

  const completedRoute = useMemo(() => {
    if (!currentPosition || routeCoordinates.length === 0) return [];
    
    const nearestIdx = findNearestWaypointIndex(currentPosition);
    const passedWaypoints = routeCoordinates.slice(0, nearestIdx + 1);
    
    return [...passedWaypoints, [currentPosition.lat, currentPosition.lng]] as [number, number][];
  }, [currentPosition, waypoints, routeCoordinates]);

  const calculatedRotation = useMemo(() => {
    if (remainingRoute.length < 2) return carRotation;
    
    const [lat1, lng1] = remainingRoute[0];
    const [lat2, lng2] = remainingRoute[1];
    
    const angle = Math.atan2(lng2 - lng1, lat2 - lat1) * (180 / Math.PI);
    return 90 - angle;
  }, [remainingRoute, carRotation]);

  const mapCenter: [number, number] = currentPosition 
    ? [currentPosition.lat, currentPosition.lng]
    : [startPoint.lat, startPoint.lng];

  return (
    <div className="relative w-full h-full">
      <MapContainer
        center={mapCenter}
        zoom={14}
        className="w-full h-full rounded-lg"
        ref={mapRef}
        zoomControl={false}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        <MapFollower position={currentPosition} shouldFollow={followCar && status === 'IN_PROGRESS'} />

        {completedRoute.length > 1 && (
          <Polyline
            positions={completedRoute}
            pathOptions={{
              color: '#34A853',
              weight: 5,
              opacity: 0.8,
            }}
          />
        )}

        {remainingRoute.length > 1 && (
          <Polyline
            positions={remainingRoute}
            pathOptions={{
              color: '#4285F4',
              weight: 5,
              opacity: 0.9,
              lineCap: 'round',
              lineJoin: 'round',
            }}
          />
        )}

        {remainingRoute.length > 1 && (
          <Polyline
            positions={remainingRoute}
            pathOptions={{
              color: '#4285F4',
              weight: 10,
              opacity: 0.15,
            }}
          />
        )}

        <Marker position={[startPoint.lat, startPoint.lng]} icon={startIcon}>
          <Popup>
            <div className="text-center">
              <strong>🚀 Start Point</strong>
              <br />
              Journey begins here
            </div>
          </Popup>
        </Marker>

        <Marker position={[destination.lat, destination.lng]} icon={dealershipIcon}>
          <Popup>
            <div className="text-center">
              <strong>🏁 Dealership</strong>
              <br />
              Destination
            </div>
          </Popup>
        </Marker>

        {currentPosition && (
          <Marker
            position={[currentPosition.lat, currentPosition.lng]}
            icon={createCarIcon(calculatedRotation)}
          >
            <Popup>
              <div className="text-center">
                <strong>🏎️ Your Vehicle</strong>
                <br />
                Status: {status}
              </div>
            </Popup>
          </Marker>
        )}
      </MapContainer>

      <motion.button
        className={`absolute bottom-6 right-6 z-[1000] px-5 py-3 rounded-xl font-semibold text-sm transition-all shadow-lg backdrop-blur-sm ${
          followCar 
            ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white hover:from-blue-700 hover:to-blue-800' 
            : 'bg-white/90 text-gray-700 hover:bg-white border border-gray-300'
        }`}
        onClick={() => setFollowCar(!followCar)}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        {followCar ? '📍 Following' : '🗺️ Free View'}
      </motion.button>

      <div className="absolute top-6 right-6 z-[1000] flex flex-col gap-3 bg-white/90 backdrop-blur-sm rounded-xl p-2 shadow-lg border border-gray-200/50">
        <motion.button
          className="w-10 h-10 bg-white hover:bg-gray-50 rounded-lg flex items-center justify-center text-gray-700 font-bold shadow-sm border border-gray-200 transition-colors"
          onClick={() => mapRef.current?.zoomIn()}
          whileHover={{ scale: 1.1, backgroundColor: '#f3f4f6' }}
          whileTap={{ scale: 0.9 }}
        >
          +
        </motion.button>
        <motion.button
          className="w-10 h-10 bg-white hover:bg-gray-50 rounded-lg flex items-center justify-center text-gray-700 font-bold shadow-sm border border-gray-200 transition-colors"
          onClick={() => mapRef.current?.zoomOut()}
          whileHover={{ scale: 1.1, backgroundColor: '#f3f4f6' }}
          whileTap={{ scale: 0.9 }}
        >
          −
        </motion.button>
      </div>
    </div>
  );
}
