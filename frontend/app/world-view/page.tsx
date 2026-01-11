'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import dynamic from 'next/dynamic';
import { motion, AnimatePresence } from 'framer-motion';
import { worldViewApi } from '@/lib/world-view/api';
import { JourneyStateDto, RouteDto, CoordinateUpdateDto, MapPosition, JourneyStatus } from '@/lib/world-view/types';
import JourneyStatusDisplay from '@/components/world-view/JourneyStatusDisplay';
import RouteSelection from '@/components/world-view/RouteSelection';

// Dynamically import MapView to avoid SSR issues with Leaflet
const MapView = dynamic(() => import('@/components/world-view/MapView'), {
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

// Dealership coordinates (Stuttgart)
const DEALERSHIP_LOCATION: MapPosition = {
  lat: 48.8354,
  lng: 9.152,
};

const JOURNEY_POLL_INTERVAL = 2000;

export default function WorldViewPage() {
  const [routes, setRoutes] = useState<RouteDto[]>([]);
  const [journeyState, setJourneyState] = useState<JourneyStateDto | null>(null);
  const [currentPosition, setCurrentPosition] = useState<MapPosition | null>(null);
  const [isConnecting, setIsConnecting] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isRoutesLoading, setIsRoutesLoading] = useState(true);

  const cleanupRef = useRef<(() => void) | null>(null);
  const pollIntervalRef = useRef<NodeJS.Timeout | null>(null);
  const currentJourneyIdRef = useRef<string | null>(null);

  const status: JourneyStatus = journeyState?.status || 'WAITING';
  const progress = journeyState?.progress_percentage || 0;
  const speedKmh = (journeyState?.speed_meters_per_second || 0) * 3.6;
  
  const currentRoute = journeyState?.route || null;
  const distanceRemaining = currentRoute
    ? currentRoute.total_distance_meters * (1 - progress / 100)
    : 0;
  const estimatedTimeRemaining = speedKmh > 0 
    ? (distanceRemaining / 1000) / speedKmh * 3600 
    : 0;

  const waypoints: MapPosition[] = currentRoute?.waypoints.map(wp => ({
    lat: wp.latitude,
    lng: wp.longitude,
  })) || [];

  const startPoint: MapPosition = currentRoute?.start_point
    ? { lat: currentRoute.start_point.latitude, lng: currentRoute.start_point.longitude }
    : DEALERSHIP_LOCATION;

  useEffect(() => {
    const fetchRoutes = async () => {
      try {
        setIsRoutesLoading(true);
        const routeData = await worldViewApi.getAllRoutes();
        setRoutes(routeData);
      } catch (err) {
        console.error('Failed to fetch routes:', err);
        setError('Failed to load routes. Is the world-view backend running?');
      } finally {
        setIsRoutesLoading(false);
      }
    };

    fetchRoutes();
  }, []);

  const handleCoordinateUpdate = useCallback((update: CoordinateUpdateDto) => {
    setCurrentPosition({
      lat: update.coordinate.latitude,
      lng: update.coordinate.longitude,
    });

    setJourneyState(prev => {
      if (!prev) return prev;
      return {
        ...prev,
        current_position: update.coordinate,
        current_waypoint_index: update.current_waypoint_index,
        progress_percentage: update.progress_percentage,
        status: update.status,
      };
    });

    if (update.status === 'COMPLETED') {
      console.log('Journey completed, will wait for next journey...');
    }
  }, []);

  const cleanupConnection = useCallback(() => {
    if (cleanupRef.current) {
      cleanupRef.current();
      cleanupRef.current = null;
    }
  }, []);

  const subscribeToJourney = useCallback((journeyId: string) => {
    if (currentJourneyIdRef.current === journeyId && cleanupRef.current) {
      return;
    }

    cleanupConnection();
    currentJourneyIdRef.current = journeyId;

    console.log('Subscribing to journey:', journeyId);
    cleanupRef.current = worldViewApi.subscribeToJourney(
      journeyId,
      handleCoordinateUpdate,
      {
        onError: (err) => {
          console.error('MQTT connection error:', err);
        },
        onEvent: (event) => {
          console.log('Journey event:', event);
        }
      }
    );
  }, [handleCoordinateUpdate, cleanupConnection]);

  const pollCurrentJourney = useCallback(async () => {
    try {
      const journey = await worldViewApi.getCurrentJourney();

      if (journey) {
        setJourneyState(journey);
        setCurrentPosition({
          lat: journey.current_position.latitude,
          lng: journey.current_position.longitude,
        });

        subscribeToJourney(journey.journey_id);
        setIsConnecting(false);
      } else {
        if (journeyState?.status === 'COMPLETED') {
          // Keep showing completed state briefly
        } else {
          setJourneyState(null);
          currentJourneyIdRef.current = null;
          cleanupConnection();
        }
        setIsConnecting(false);
      }

      setError(null);
    } catch (err) {
      console.error('Failed to poll journey:', err);
      setIsConnecting(false);
    }
  }, [journeyState?.status, subscribeToJourney, cleanupConnection]);

  useEffect(() => {
    pollCurrentJourney();

    pollIntervalRef.current = setInterval(pollCurrentJourney, JOURNEY_POLL_INTERVAL);

    return () => {
      if (pollIntervalRef.current) {
        clearInterval(pollIntervalRef.current);
      }
      cleanupConnection();
    };
  }, [pollCurrentJourney, cleanupConnection]);

  return (
    <div className="min-h-[calc(100vh-5rem)] bg-gradient-to-br from-gray-50 via-white to-gray-50">
      {/* Live Journey Indicator Badge */}
      {status === 'IN_PROGRESS' && (
        <motion.div
          className="fixed top-24 right-4 z-50 flex items-center gap-2 bg-gradient-to-r from-green-500 to-emerald-500 text-white px-4 py-2 rounded-full shadow-lg"
          initial={{ opacity: 0, scale: 0.8, y: -20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
        >
          <div className="w-2 h-2 bg-white rounded-full animate-pulse" />
          <span className="text-sm font-semibold">Live Journey</span>
        </motion.div>
      )}

      <div className="flex flex-col lg:flex-row h-[calc(100vh-5rem)]">
        {/* Map area - takes full height */}
        <div className="flex-1 relative min-h-[60vh] lg:min-h-0 lg:h-full">
          <MapView
            currentPosition={currentPosition}
            destination={DEALERSHIP_LOCATION}
            startPoint={startPoint}
            waypoints={waypoints}
            status={status}
          />

          <AnimatePresence>
            {error && (
              <motion.div
                className="absolute top-4 left-4 right-4 md:left-auto md:right-4 md:w-96 bg-white border-2 border-red-300 text-red-800 p-4 rounded-xl shadow-xl z-[1000]"
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
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
          </AnimatePresence>
        </div>

        {/* Control panel - elegant sidebar */}
        <div className="lg:w-[420px] bg-white/95 backdrop-blur-sm border-t lg:border-t-0 lg:border-l border-gray-200/50 shadow-xl overflow-y-auto lg:h-full">
          <div className="p-6 space-y-6">
            <JourneyStatusDisplay
              status={status}
              progress={progress}
              speedKmh={speedKmh}
              distanceRemaining={distanceRemaining}
              estimatedTimeRemaining={estimatedTimeRemaining}
              routeName={currentRoute?.name || 'Waiting for journey...'}
              journeyId={journeyState?.journey_id || null}
              isConnecting={isConnecting}
            />

            <RouteSelection
              routes={routes}
              currentRouteId={currentRoute?.id || null}
              isLoading={isRoutesLoading}
            />

            <motion.div
              className="bg-gradient-to-br from-red-50 to-orange-50 rounded-2xl p-5 border border-red-100 shadow-sm"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
            >
              <h3 className="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                <span className="text-xl">🏁</span>
                Destination
              </h3>
              <div className="flex items-center gap-3">
                <div className="w-14 h-14 bg-gradient-to-br from-red-500 to-orange-500 rounded-xl flex items-center justify-center text-2xl shadow-md">
                  🏁
                </div>
                <div>
                  <p className="font-bold text-gray-900 text-lg">BennyCar Dealership</p>
                  <p className="text-sm text-gray-600 mt-0.5">Stuttgart, Germany</p>
                </div>
              </div>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
}
