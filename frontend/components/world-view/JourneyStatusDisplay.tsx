'use client';

import { motion } from 'framer-motion';
import { JourneyStatus } from '@/lib/world-view/types';
import {
  Navigation,
  Gauge,
  Timer,
  MapPin,
  Car,
  Loader2,
  CheckCircle2,
  Clock
} from 'lucide-react';

interface JourneyStatusDisplayProps {
  status: JourneyStatus;
  progress: number;
  speedKmh: number;
  distanceRemaining: number;
  estimatedTimeRemaining: number;
  routeName: string;
  journeyId: string | null;
  isConnecting: boolean;
}

export default function JourneyStatusDisplay({
  status,
  progress,
  speedKmh,
  distanceRemaining,
  estimatedTimeRemaining,
  routeName,
  journeyId,
  isConnecting,
}: JourneyStatusDisplayProps) {

  const formatDistance = (meters: number) => {
    if (meters >= 1000) {
      return `${(meters / 1000).toFixed(1)} km`;
    }
    return `${Math.round(meters)} m`;
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    if (mins >= 60) {
      const hrs = Math.floor(mins / 60);
      const remainingMins = mins % 60;
      return `${hrs}h ${remainingMins}m`;
    }
    return `${mins}m ${secs}s`;
  };

  const getStatusInfo = () => {
    if (isConnecting) {
      return {
        badge: 'bg-yellow-100 text-yellow-800 border-yellow-200',
        text: 'Connecting...',
        icon: <Loader2 className="w-4 h-4 animate-spin" />,
        description: 'Connecting to journey service...'
      };
    }

    switch (status) {
      case 'WAITING':
        return {
          badge: 'bg-blue-100 text-blue-800 border-blue-200',
          text: 'Waiting',
          icon: <Clock className="w-4 h-4" />,
          description: 'Waiting for next journey to start automatically...'
        };
      case 'IN_PROGRESS':
        return {
          badge: 'bg-green-100 text-green-800 border-green-200',
          text: 'In Progress',
          icon: <Car className="w-4 h-4" />,
          description: 'Vehicle is on its way to the dealership'
        };
      case 'COMPLETED':
        return {
          badge: 'bg-purple-100 text-purple-800 border-purple-200',
          text: 'Completed',
          icon: <CheckCircle2 className="w-4 h-4" />,
          description: 'Journey completed! A new journey will start soon.'
        };
      default:
        return {
          badge: 'bg-gray-100 text-gray-800 border-gray-200',
          text: 'No Active Journey',
          icon: <Clock className="w-4 h-4" />,
          description: 'Waiting for a journey to start automatically...'
        };
    }
  };

  const statusInfo = getStatusInfo();

  return (
    <motion.div
      className="bg-gradient-to-br from-white to-gray-50 rounded-2xl p-6 border border-gray-200/50 shadow-lg w-full"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Navigation className="w-5 h-5 text-[#4285F4]" />
          <h2 className="text-lg font-semibold text-[#202124]">Live Journey</h2>
        </div>
        <span className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-medium border ${statusInfo.badge}`}>
          {statusInfo.icon}
          {statusInfo.text}
        </span>
      </div>

      <p className="text-sm text-[#5F6368] mb-4">{statusInfo.description}</p>

      {(status === 'IN_PROGRESS' || status === 'COMPLETED') && (
        <>
          {/* Route name */}
          <div className="flex items-center gap-2 mb-4 p-3 bg-[#F8F9FA] rounded-lg border border-[#E8EAED]">
            <MapPin className="w-4 h-4 text-[#EA4335]" />
            <div className="flex-1">
              <span className="text-xs text-[#5F6368]">Current Route</span>
              <p className="text-sm font-medium text-[#202124] truncate">{routeName}</p>
            </div>
            {journeyId && (
              <span className="text-xs text-[#80868B] font-mono">{journeyId.substring(0, 16)}...</span>
            )}
          </div>

          {/* Progress bar */}
          <div className="mb-4">
            <div className="flex justify-between text-sm text-[#5F6368] mb-1">
              <span>Progress</span>
              <span className="font-medium">{progress.toFixed(1)}%</span>
            </div>
            <div className="h-3 bg-[#E8EAED] rounded-full overflow-hidden">
              <motion.div
                className="h-full rounded-full"
                style={{
                  background: status === 'COMPLETED'
                    ? 'linear-gradient(90deg, #34A853, #4CAF50)'
                    : 'linear-gradient(90deg, #4285F4, #34A853)'
                }}
                initial={{ width: 0 }}
                animate={{ width: `${progress}%` }}
                transition={{ duration: 0.5, ease: 'easeOut' }}
              />
            </div>
          </div>

          {/* Stats grid */}
          <div className="grid grid-cols-3 gap-3">
            <div className="bg-[#F8F9FA] border border-[#E8EAED] rounded-lg p-3 text-center">
              <Gauge className="w-5 h-5 text-[#34A853] mx-auto mb-1" />
              <div className="text-lg font-bold text-[#202124]">{speedKmh.toFixed(0)}</div>
              <div className="text-xs text-[#5F6368]">km/h</div>
            </div>
            <div className="bg-[#F8F9FA] border border-[#E8EAED] rounded-lg p-3 text-center">
              <Navigation className="w-5 h-5 text-[#4285F4] mx-auto mb-1" />
              <div className="text-lg font-bold text-[#202124]">{formatDistance(distanceRemaining)}</div>
              <div className="text-xs text-[#5F6368]">remaining</div>
            </div>
            <div className="bg-[#F8F9FA] border border-[#E8EAED] rounded-lg p-3 text-center">
              <Timer className="w-5 h-5 text-[#FBBC04] mx-auto mb-1" />
              <div className="text-lg font-bold text-[#202124]">{formatTime(estimatedTimeRemaining)}</div>
              <div className="text-xs text-[#5F6368]">ETA</div>
            </div>
          </div>
        </>
      )}

      {(status === 'WAITING' || status === 'NOT_STARTED') && !isConnecting && (
        <div className="flex flex-col items-center justify-center py-8">
          <div className="relative">
            <div className="w-16 h-16 border-4 border-[#E8EAED] border-t-[#4285F4] rounded-full animate-spin" />
            <Car className="w-6 h-6 text-[#4285F4] absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2" />
          </div>
          <p className="text-sm text-[#5F6368] mt-4">A new journey will start automatically...</p>
        </div>
      )}

      {isConnecting && (
        <div className="flex flex-col items-center justify-center py-8">
          <Loader2 className="w-12 h-12 text-[#4285F4] animate-spin" />
          <p className="text-sm text-[#5F6368] mt-4">Connecting to journey service...</p>
        </div>
      )}

      <div className="mt-4 pt-4 border-t border-[#E8EAED]">
        <p className="text-xs text-center text-[#80868B]">
          🚗 Journeys are automatically managed by the system
        </p>
      </div>
    </motion.div>
  );
}
