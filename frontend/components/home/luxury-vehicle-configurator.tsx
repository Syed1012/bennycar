'use client';

import { useState, useRef, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { ChevronLeft, ChevronRight, Zap, Gauge, Clock, Star, Award, TrendingUp, Info } from 'lucide-react';
import Link from 'next/link';

interface VehicleConfig {
  id: string;
  name: string;
  edition: string;
  image: string;
  price: {
    from: number;
    monthly: number;
  };
  badges: string[];
  power: {
    kw: number;
    ps: number;
    label: string;
  };
  range: {
    min: number;
    max: number;
    unit: string;
  };
  charging: {
    time: number;
    description: string;
  };
  specs: {
    transmission: string;
    drive: string;
    type: string;
    battery: string;
  };
  highlights: string[];
  gradient: string;
  accentColor: string;
}

const luxuryVehicles: VehicleConfig[] = [
  {
    id: 'porsche-taycan-turbo-s',
    name: 'Taycan Turbo S',
    edition: 'Electric Performance',
    image: 'https://images.unsplash.com/photo-1614200187524-dc4b892acf16?w=800&h=800&fit=crop&q=80',
    price: {
      from: 185000,
      monthly: 2245,
    },
    badges: ['Neu', 'Elektro', 'Allradantrieb', 'Turbo'],
    power: {
      kw: 560,
      ps: 761,
      label: 'Overboost-Leistung',
    },
    range: {
      min: 440,
      max: 508,
      unit: 'km',
    },
    charging: {
      time: 18,
      description: 'DC Ultraschnellladezeit (10-80%)',
    },
    specs: {
      transmission: '2-Gang Automatik',
      drive: 'Allradantrieb',
      type: 'Sport Limousine',
      battery: 'Performance Plus Pro',
    },
    highlights: [
      'Porsche Dynamic Chassis Control Sport',
      'Carbon Ceramic Brakes PCCB',
      'Rear-Axle Steering Plus',
      'Sport Chrono Package',
    ],
    gradient: 'from-rose-950 via-red-950 to-rose-950',
    accentColor: '#d4a574',
  },
  {
    id: 'lamborghini-aventador',
    name: 'Aventador SVJ',
    edition: 'Italian Supercar',
    image: 'https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=800&h=800&fit=crop&q=80',
    price: {
      from: 517770,
      monthly: 6280,
    },
    badges: ['Limited', 'V12', 'Allradantrieb', 'SVJ'],
    power: {
      kw: 566,
      ps: 770,
      label: 'Maximum Power',
    },
    range: {
      min: 380,
      max: 420,
      unit: 'km',
    },
    charging: {
      time: 0,
      description: '6.5L V12 Naturally Aspirated',
    },
    specs: {
      transmission: '7-Gang ISR',
      drive: 'Allradantrieb',
      type: 'Supersportwagen',
      battery: '6.5L V12 Engine',
    },
    highlights: [
      'ALA 2.0 Active Aerodynamics',
      'Magnetorheological Suspension',
      'Carbon Fiber Monocoque',
      'Rear-Wheel Steering',
    ],
    gradient: 'from-rose-950 via-red-950 to-rose-950',
    accentColor: '#d4a574',
  },
  {
    id: 'ferrari-sf90-stradale',
    name: 'SF90 Stradale',
    edition: 'Plug-in Hybrid',
    image: 'https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=800&h=800&fit=crop&q=80',
    price: {
      from: 430000,
      monthly: 5215,
    },
    badges: ['Neu', 'Hybrid', 'Allradantrieb', 'F1 Tech'],
    power: {
      kw: 735,
      ps: 1000,
      label: 'Combined System Power',
    },
    range: {
      min: 25,
      max: 25,
      unit: 'km',
    },
    charging: {
      time: 90,
      description: 'Full Electric Range',
    },
    specs: {
      transmission: '8-Gang Doppelkupplung',
      drive: 'Allradantrieb',
      type: 'Berlinetta Hybrid',
      battery: '7.9 kWh + V8 Biturbo',
    },
    highlights: [
      'eManettino Driving Modes',
      'RAC-e Torque Vectoring',
      '4-Motor System (1 ICE + 3 Electric)',
      'Active Aerodynamics',
    ],
    gradient: 'from-rose-950 via-red-950 to-rose-950',
    accentColor: '#d4a574',
  },
  {
    id: 'mercedes-amg-gt',
    name: 'AMG GT Black Series',
    edition: 'Track Performance',
    image: 'https://images.unsplash.com/photo-1617814076367-b759c7d7e738?w=800&h=800&fit=crop&q=80',
    price: {
      from: 335000,
      monthly: 4060,
    },
    badges: ['Limited', 'V8 Biturbo', 'Heckantrieb', 'AMG'],
    power: {
      kw: 537,
      ps: 730,
      label: 'Maximum Power',
    },
    range: {
      min: 380,
      max: 420,
      unit: 'km',
    },
    charging: {
      time: 0,
      description: '4.0L V8 Biturbo',
    },
    specs: {
      transmission: '7-Gang AMG Speedshift',
      drive: 'Heckantrieb',
      type: 'GT Coupé',
      battery: 'V8 Biturbo Engine',
    },
    highlights: [
      'AMG Track Pace',
      'Carbon Fiber Monocoque',
      'Adjustable Coilover Suspension',
      'AMG Aerodynamics Package',
    ],
    gradient: 'from-rose-950 via-red-950 to-rose-950',
    accentColor: '#d4a574',
  },
  {
    id: 'bmw-m8',
    name: 'M8 Competition',
    edition: 'High Performance Gran Coupé',
    image: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=800&fit=crop&q=80',
    price: {
      from: 146000,
      monthly: 1770,
    },
    badges: ['M Performance', 'V8 Biturbo', 'xDrive', 'Competition'],
    power: {
      kw: 460,
      ps: 625,
      label: 'Maximum Power',
    },
    range: {
      min: 420,
      max: 480,
      unit: 'km',
    },
    charging: {
      time: 0,
      description: '4.4L V8 Biturbo',
    },
    specs: {
      transmission: '8-Gang M Steptronic',
      drive: 'M xDrive Allrad',
      type: 'Gran Coupé',
      battery: 'V8 Biturbo Engine',
    },
    highlights: [
      'M xDrive All-Wheel Drive',
      'Active M Differential',
      'M Carbon Ceramic Brakes',
      'Adaptive M Suspension',
    ],
    gradient: 'from-rose-950 via-red-950 to-rose-950',
    accentColor: '#d4a574',
  },
  {
    id: 'audi-r8',
    name: 'R8 V10 Performance',
    edition: 'Naturally Aspirated Supercar',
    image: 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=800&fit=crop&q=80',
    price: {
      from: 208000,
      monthly: 2520,
    },
    badges: ['Performance', 'V10', 'Quattro', 'R8'],
    power: {
      kw: 456,
      ps: 620,
      label: 'Maximum Power',
    },
    range: {
      min: 400,
      max: 450,
      unit: 'km',
    },
    charging: {
      time: 0,
      description: '5.2L V10 FSI',
    },
    specs: {
      transmission: '7-Gang S tronic',
      drive: 'Quattro Allrad',
      type: 'Coupé',
      battery: 'V10 Engine',
    },
    highlights: [
      'Audi Space Frame ASF',
      'Magnetic Ride Suspension',
      'Virtual Cockpit Plus',
      'Laser Headlights',
    ],
    gradient: 'from-rose-950 via-red-950 to-rose-950',
    accentColor: '#d4a574',
  },
];

export function LuxuryVehicleConfigurator() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const [startX, setStartX] = useState(0);
  const [scrollLeft, setScrollLeft] = useState(0);
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  const currentVehicle = luxuryVehicles[currentIndex];

  // Auto-scroll functionality
  useEffect(() => {
    const interval = setInterval(() => {
      if (!isDragging) {
        setCurrentIndex((prev) => (prev + 1) % luxuryVehicles.length);
      }
    }, 7000);

    return () => clearInterval(interval);
  }, [isDragging]);

  const handlePrevious = () => {
    setCurrentIndex((prev) => (prev - 1 + luxuryVehicles.length) % luxuryVehicles.length);
  };

  const handleNext = () => {
    setCurrentIndex((prev) => (prev + 1) % luxuryVehicles.length);
  };

  const handleMouseDown = (e: React.MouseEvent) => {
    setIsDragging(true);
    setStartX(e.pageX - (scrollContainerRef.current?.offsetLeft || 0));
    setScrollLeft(scrollContainerRef.current?.scrollLeft || 0);
  };

  const handleMouseUp = () => {
    setIsDragging(false);
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isDragging) return;
    e.preventDefault();
    const x = e.pageX - (scrollContainerRef.current?.offsetLeft || 0);
    const walk = (x - startX) * 2;
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollLeft = scrollLeft - walk;
    }
  };

  return (
    <section className="relative py-24 overflow-hidden bg-linear-to-br from-[#fafaf8] via-[#f5ede4] to-[#e8d5c4] mt-0">
      {/* Animated Background Elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-10 w-96 h-96 bg-linear-to-r from-[#c89968]/20 to-[#d4a574]/20 rounded-full blur-3xl animate-pulse" />
        <div className="absolute bottom-20 right-10 w-96 h-96 bg-linear-to-r from-[#8b7355]/20 to-[#c89968]/20 rounded-full blur-3xl animate-pulse delay-1000" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-150 h-150 bg-linear-to-r from-[#d4a574]/10 to-transparent rounded-full blur-3xl" />
      </div>

      <div className="container mx-auto px-4 relative z-10">
        {/* Section Header */}
        <div className="text-center mb-16 space-y-4">
          <div className="inline-flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-[#c89968] to-[#d4a574] rounded-full text-white font-semibold text-sm shadow-lg shadow-[#c89968]/30 animate-pulse">
            <Award className="w-4 h-4" />
            <span>Premium Electric Collection</span>
          </div>
          <h2 className="text-5xl md:text-6xl font-bold bg-gradient-to-r from-[#4a3f35] via-[#8b7355] to-[#c89968] bg-clip-text text-transparent">
            Configure Your Dream Machine
          </h2>
          <p className="text-xl text-[#8b7355] max-w-2xl mx-auto">
            Explore our exclusive lineup of high-performance electric vehicles with cutting-edge technology
          </p>
        </div>

        {/* Main Showcase */}
        <div className="relative max-w-7xl mx-auto">
          {/* Featured Vehicle Display */}
          <div className="relative mb-12">
            <div className={`relative rounded-3xl overflow-hidden shadow-2xl bg-gradient-to-br ${currentVehicle.gradient}`}>
              {/* Background Pattern */}
              <div className="absolute inset-0 opacity-5">
                <div className="absolute inset-0" style={{
                  backgroundImage: 'radial-gradient(circle at 1px 1px, white 1px, transparent 0)',
                  backgroundSize: '40px 40px',
                }} />
              </div>

              <div className="relative grid lg:grid-cols-2 gap-8 p-8 md:p-12">
                {/* Left Side - Vehicle Info */}
                <div className="space-y-8 flex flex-col justify-center">
                  {/* Edition Badge */}
                  <div className="inline-flex items-center gap-2 px-4 py-2 bg-white/10 backdrop-blur-md rounded-full text-white text-sm font-medium w-fit border border-white/20">
                    <Star className="w-4 h-4 text-[#d4a574]" />
                    {currentVehicle.edition}
                  </div>

                  {/* Vehicle Name */}
                  <div className="space-y-2">
                    <h3 className="text-4xl md:text-5xl font-bold text-white">
                      {currentVehicle.name}
                    </h3>
                    <div className="flex flex-wrap gap-2">
                      {currentVehicle.badges.map((badge, idx) => (
                        <span
                          key={badge}
                          className={`px-3 py-1 text-xs font-semibold rounded-full border ${
                            idx === 0 
                              ? 'bg-[#d4a574] border-[#d4a574] text-white'
                              : 'bg-white/10 border-white/20 text-white'
                          }`}
                        >
                          {badge}
                        </span>
                      ))}
                    </div>
                  </div>

                  {/* Power Stats */}
                  <div className="bg-white/10 backdrop-blur-md rounded-2xl p-6 border border-white/20">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="p-2 rounded-lg bg-[#d4a574]">
                        <Zap className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <p className="text-white/70 text-sm">{currentVehicle.power.label}</p>
                        <p className="text-3xl font-bold text-white">
                          {currentVehicle.power.kw} kW / {currentVehicle.power.ps} PS
                        </p>
                      </div>
                    </div>
                    {/* Animated Power Bar */}
                    <div className="relative h-2 bg-white/20 rounded-full overflow-hidden">
                      <div
                        className="absolute inset-y-0 left-0 bg-[#d4a574] rounded-full transition-all duration-1000 ease-out"
                        style={{
                          width: `${(currentVehicle.power.ps / 800) * 100}%`,
                        }}
                      >
                        <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/40 to-transparent animate-shimmer" />
                      </div>
                    </div>
                  </div>

                  {/* Range & Charging Grid */}
                  <div className="grid grid-cols-2 gap-4">
                    {/* Range */}
                    <div className="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/20">
                      <div className="flex items-center gap-2 mb-2">
                        <Gauge className="w-5 h-5 text-[#d4a574]" />
                        <p className="text-white/70 text-sm font-medium">Reichweite</p>
                      </div>
                      <p className="text-2xl font-bold text-white">
                        {currentVehicle.range.min}–{currentVehicle.range.max}
                        <span className="text-lg ml-1">{currentVehicle.range.unit}</span>
                      </p>
                      <div className="mt-2 h-1 bg-white/20 rounded-full overflow-hidden">
                        <div
                          className="h-full bg-[#d4a574] rounded-full"
                          style={{
                            width: `${(currentVehicle.range.max / 700) * 100}%`,
                          }}
                        />
                      </div>
                    </div>

                    {/* Charging */}
                    <div className="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/20">
                      <div className="flex items-center gap-2 mb-2">
                        <Clock className="w-5 h-5 text-[#d4a574]" />
                        <p className="text-white/70 text-sm font-medium">Ladezeit</p>
                      </div>
                      <p className="text-2xl font-bold text-white">
                        {currentVehicle.charging.time}
                        <span className="text-lg ml-1">min</span>
                      </p>
                      <p className="text-xs text-white/60 mt-1">
                        {currentVehicle.charging.description}
                      </p>
                    </div>
                  </div>

                  {/* Price */}
                  <div className="bg-white/10 backdrop-blur-md rounded-2xl p-6 border border-white/20">
                    <p className="text-white/70 text-sm mb-1">Ab EUR</p>
                    <p className="text-4xl font-bold text-white mb-2">
                      {currentVehicle.price.from.toLocaleString('de-DE')}
                      <span className="text-lg ml-2">inkl. MwSt.</span>
                    </p>
                    <p className="text-white/60 text-sm">
                      z.B. {currentVehicle.price.monthly.toLocaleString('de-DE', { minimumFractionDigits: 2 })} € mtl.
                    </p>
                  </div>

                  {/* CTA Button */}
                  <Link href={`/vehicles/${currentVehicle.id}`}>
                    <Button
                      className="w-full h-14 text-lg font-semibold rounded-xl border-2 border-[#d4a574] bg-white text-[#8b7355] hover:bg-[#d4a574] hover:text-white hover:scale-105 transition-all duration-300 shadow-lg"
                    >
                      Individuell konfigurieren
                      <TrendingUp className="w-5 h-5 ml-2" />
                    </Button>
                  </Link>
                </div>

                {/* Right Side - Vehicle Image with Circular Sphere */}
                <div className="relative flex items-center justify-center min-h-[500px]">
                  <div className="relative group w-full max-w-125 mx-auto aspect-square">
                    {/* Multi-layer Circular Glow Effect - Creates sphere background */}
                    <div
                      className="absolute inset-0 blur-[120px] opacity-50 group-hover:opacity-70 transition-opacity duration-700 rounded-full scale-110"
                      style={{ 
                        backgroundColor: currentVehicle.accentColor,
                        filter: 'brightness(1.3)',
                      }}
                    />
                    <div
                      className="absolute inset-0 blur-[80px] opacity-40 group-hover:opacity-60 transition-opacity duration-500 rounded-full scale-95"
                      style={{ 
                        backgroundColor: currentVehicle.accentColor,
                      }}
                    />

                    {/* Perfect Circular Container - Clips image to circle */}
                    <div className="relative z-10 w-full h-full rounded-full overflow-hidden bg-linear-to-br from-amber-900/20 via-orange-900/10 to-amber-800/20 backdrop-blur-md border-4 border-[#d4a574]/30 shadow-2xl">
                      {/* Inner circle with car image - perfectly clipped */}
                      <div className="absolute inset-0 rounded-full overflow-hidden">
                        <img
                          src={currentVehicle.image}
                          alt={currentVehicle.name}
                          className="w-full h-full object-cover transform group-hover:scale-110 transition-all duration-700 filter brightness-110 contrast-110"
                          style={{
                            objectPosition: 'center center',
                          }}
                        />
                        
                        {/* Radial gradient overlay for vignette effect */}
                        <div 
                          className="absolute inset-0 pointer-events-none"
                          style={{
                            background: 'radial-gradient(circle at center, transparent 40%, rgba(0,0,0,0.3) 100%)',
                          }}
                        />
                      </div>
                      
                      {/* Shine effect */}
                      <div 
                        className="absolute inset-0 opacity-20 group-hover:opacity-30 transition-opacity duration-500"
                        style={{
                          background: 'linear-gradient(135deg, transparent 0%, rgba(255,255,255,0.2) 50%, transparent 100%)',
                        }}
                      />
                    </div>

                    {/* Rotating ring effect around sphere */}
                    <div className="absolute inset-0 rounded-full border-2 border-[#d4a574]/20 animate-pulse" />

                    {/* Bottom accent line with pulse effect */}
                    <div className="absolute -bottom-4 left-1/2 -translate-x-1/2 w-3/4 h-1.5 rounded-full opacity-60">
                      <div className="w-full h-full bg-linear-to-r from-transparent via-[#d4a574] to-transparent animate-pulse shadow-lg shadow-[#d4a574]/50" />
                    </div>
                  </div>
                </div>
              </div>

              {/* Specs Highlights - Bottom Bar */}
              <div className="bg-white/10 backdrop-blur-md border-t border-white/20 p-6">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  {currentVehicle.highlights.map((highlight, idx) => (
                    <div key={idx} className="flex items-center gap-2 text-white/90 text-sm">
                      <div className="w-1.5 h-1.5 rounded-full" style={{ backgroundColor: currentVehicle.accentColor }} />
                      <span>{highlight}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Thumbnail Slider */}
          <div className="relative">
            {/* Navigation Buttons */}
            <button
              onClick={handlePrevious}
              className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-4 z-20 p-3 rounded-full bg-white shadow-lg hover:shadow-xl hover:scale-110 transition-all duration-300 border-2 border-[#c89968]"
            >
              <ChevronLeft className="w-6 h-6 text-[#c89968]" />
            </button>
            
            <button
              onClick={handleNext}
              className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-4 z-20 p-3 rounded-full bg-white shadow-lg hover:shadow-xl hover:scale-110 transition-all duration-300 border-2 border-[#c89968]"
            >
              <ChevronRight className="w-6 h-6 text-[#c89968]" />
            </button>

            {/* Scrollable Thumbnails */}
            <div
              ref={scrollContainerRef}
              onMouseDown={handleMouseDown}
              onMouseUp={handleMouseUp}
              onMouseLeave={handleMouseUp}
              onMouseMove={handleMouseMove}
              className="flex gap-4 overflow-x-auto scrollbar-hide scroll-smooth px-4 py-2 cursor-grab active:cursor-grabbing select-none"
              style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
            >
              {luxuryVehicles.map((vehicle, idx) => (
                <button
                  key={vehicle.id}
                  onClick={() => setCurrentIndex(idx)}
                  className={`shrink-0 w-80 rounded-2xl overflow-hidden border-4 transition-all duration-500 hover:scale-105 ${
                    idx === currentIndex
                      ? 'border-[#c89968] shadow-2xl scale-105 ring-4 ring-[#c89968]/30'
                      : 'border-transparent shadow-md hover:border-[#e8d5c4]'
                  }`}
                >
                  <div className={`relative bg-linear-to-br ${vehicle.gradient} p-6 h-full`}>
                    {/* Fixed height image container */}
                    <div className="relative h-48 mb-4 flex items-center justify-center">
                      <img
                        src={vehicle.image}
                        alt={vehicle.name}
                        className="w-full h-full object-contain drop-shadow-2xl"
                        style={{
                          filter: 'drop-shadow(0 10px 30px rgba(0,0,0,0.4)) contrast(1.05)',
                        }}
                      />
                      {idx === currentIndex && (
                        <div className="absolute inset-0 bg-linear-to-t from-black/40 to-transparent rounded-lg flex items-end justify-center pb-2">
                          <div className="flex items-center gap-2 bg-white/90 px-3 py-1 rounded-full">
                            <div className="w-2 h-2 rounded-full bg-[#d4a574] animate-pulse" />
                            <span className="text-xs font-bold text-[#8b7355]">
                              AKTIV
                            </span>
                          </div>
                        </div>
                      )}
                    </div>

                    {/* Vehicle Info */}
                    <div className="space-y-3">
                      <h4 className="text-white font-bold text-lg leading-tight">{vehicle.name}</h4>
                      <div className="flex items-center justify-between text-white/90 text-sm bg-white/10 backdrop-blur-sm rounded-lg p-2">
                        <span className="flex items-center gap-1">
                          <Zap className="w-4 h-4" />
                          {vehicle.power.ps} PS
                        </span>
                        <span className="flex items-center gap-1">
                          <Gauge className="w-4 h-4" />
                          {vehicle.range.max} km
                        </span>
                      </div>

                      {/* Price */}
                      <div className="text-center bg-white/95 backdrop-blur-sm rounded-lg p-3">
                        <p className="text-xs text-gray-600 mb-1">Ab EUR</p>
                        <p className="text-xl font-bold text-[#8b7355]">
                          {vehicle.price.from.toLocaleString('de-DE')}
                        </p>
                      </div>
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Progress Indicators */}
          <div className="flex justify-center gap-2 mt-8">
            {luxuryVehicles.map((_, idx) => (
              <button
                key={idx}
                onClick={() => setCurrentIndex(idx)}
                className={`h-2 rounded-full transition-all duration-500 ${
                  idx === currentIndex
                    ? 'w-12 bg-gradient-to-r from-[#c89968] to-[#d4a574]'
                    : 'w-2 bg-[#e8d5c4] hover:bg-[#c89968]/50'
                }`}
              />
            ))}
          </div>
        </div>

        {/* Info Banner */}
        <div className="mt-16 max-w-4xl mx-auto bg-white/60 backdrop-blur-sm rounded-2xl p-6 border-2 border-[#e8d5c4] shadow-lg">
          <div className="flex items-start gap-4">
            <div className="p-3 rounded-xl bg-gradient-to-br from-[#c89968] to-[#d4a574] text-white">
              <Info className="w-6 h-6" />
            </div>
            <div className="flex-1">
              <h4 className="text-lg font-bold text-[#4a3f35] mb-2">
                Stromverbrauch & CO₂-Emissionen
              </h4>
              <p className="text-sm text-[#8b7355]">
                Stromverbrauch kombiniert: 19,4 – 18,0 kWh/100 km. CO₂-Emissionen kombiniert: 0 g/km. 
                CO₂-Klasse: A. Die angegebenen Werte wurden nach dem vorgeschriebenen Messverfahren WLTP ermittelt.
              </p>
            </div>
          </div>
        </div>
      </div>

      <style jsx>{`
        @keyframes shimmer {
          0% { transform: translateX(-100%); }
          100% { transform: translateX(100%); }
        }
        .animate-shimmer {
          animation: shimmer 2s infinite;
        }
        .scrollbar-hide::-webkit-scrollbar {
          display: none;
        }
      `}</style>
    </section>
  );
}
