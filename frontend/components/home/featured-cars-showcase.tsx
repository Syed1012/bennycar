"use client";

import { useState, useEffect } from "react";
import { ChevronLeft, ChevronRight, Gauge, Zap, Star } from "lucide-react";
import { Button } from "@/components/ui/button";

// Premium car data with real-world supercars
const featuredCars = [
  {
    id: 1,
    brand: "Porsche",
    model: "911 Turbo S",
    year: 2024,
    price: 207000,
    image: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
    specs: {
      horsepower: "640 HP",
      topSpeed: "205 mph",
      acceleration: "2.6s 0-60",
    },
    color: "Racing Yellow",
    description: "The ultimate sports car combining everyday usability with racetrack performance"
  },
  {
    id: 2,
    brand: "Lamborghini",
    model: "Huracán EVO",
    year: 2024,
    price: 287400,
    image: "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=800&h=600&fit=crop",
    specs: {
      horsepower: "631 HP",
      topSpeed: "202 mph",
      acceleration: "2.9s 0-60",
    },
    color: "Verde Mantis",
    description: "Italian masterpiece with naturally aspirated V10 power and all-wheel drive"
  },
  {
    id: 3,
    brand: "Ferrari",
    model: "F8 Tributo",
    year: 2024,
    price: 280000,
    image: "https://images.unsplash.com/photo-1592198084033-aade902d1aae?w=800&h=600&fit=crop",
    specs: {
      horsepower: "710 HP",
      topSpeed: "211 mph",
      acceleration: "2.9s 0-60",
    },
    color: "Rosso Corsa",
    description: "Mid-engine V8 supercar representing the pinnacle of Ferrari engineering"
  },
  {
    id: 4,
    brand: "BMW",
    model: "M8 Competition",
    year: 2024,
    price: 146000,
    image: "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop",
    specs: {
      horsepower: "617 HP",
      topSpeed: "190 mph",
      acceleration: "3.0s 0-60",
    },
    color: "Marina Bay Blue",
    description: "Luxury grand tourer with M division performance and cutting-edge technology"
  },
  {
    id: 5,
    brand: "Bugatti",
    model: "Chiron Super Sport",
    year: 2024,
    price: 3900000,
    image: "https://images.unsplash.com/photo-1566023888706-b2e23d37f8fa?w=800&h=600&fit=crop",
    specs: {
      horsepower: "1577 HP",
      topSpeed: "273 mph",
      acceleration: "2.4s 0-60",
    },
    color: "Nocturne Black",
    description: "The ultimate hypercar pushing the boundaries of automotive performance"
  },
];

export function FeaturedCarsShowcase() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isAnimating, setIsAnimating] = useState(false);

  // Auto-slide every 5 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      handleNext();
    }, 5000);

    return () => clearInterval(interval);
  }, [currentIndex]);

  const handlePrevious = () => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex((prev) => (prev === 0 ? featuredCars.length - 1 : prev - 1));
    setTimeout(() => setIsAnimating(false), 500);
  };

  const handleNext = () => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex((prev) => (prev === featuredCars.length - 1 ? 0 : prev + 1));
    setTimeout(() => setIsAnimating(false), 500);
  };

  const currentCar = featuredCars[currentIndex];

  return (
    <section className="py-20 px-4 bg-gradient-to-br from-[#4a3f35] via-[#5a4d42] to-[#4a3f35] relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-10 left-10 w-72 h-72 bg-[#c89968] rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-10 right-10 w-96 h-96 bg-[#d4a574] rounded-full blur-3xl animate-pulse" style={{ animationDelay: "1s" }}></div>
      </div>

      <div className="container mx-auto relative z-10">
        {/* Section Header */}
        <div className="text-center mb-12">
          <div className="inline-flex items-center gap-2 mb-4 px-4 py-2 bg-white/10 backdrop-blur-sm rounded-full">
            <Star className="h-5 w-5 text-[#d4a574]" />
            <span className="text-white/90 text-sm font-semibold">Premium Collection</span>
          </div>
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-4">
            Featured Supercars
          </h2>
          <p className="text-white/70 text-lg max-w-2xl mx-auto">
            Discover our handpicked selection of the world's most extraordinary vehicles
          </p>
        </div>

        {/* Main Showcase */}
        <div className="max-w-7xl mx-auto">
          <div className="relative">
            {/* Car Display Card */}
            <div className="bg-gradient-to-br from-white/10 to-white/5 backdrop-blur-xl rounded-3xl p-8 md:p-12 border border-white/20 shadow-2xl">
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
                {/* Car Image */}
                <div className="relative group">
                  <div className="absolute inset-0 bg-gradient-to-br from-[#c89968] to-[#d4a574] rounded-2xl blur-xl opacity-50 group-hover:opacity-75 transition-opacity"></div>
                  <div className="relative overflow-hidden rounded-2xl shadow-2xl transform transition-transform duration-500 group-hover:scale-105">
                    <img
                      src={currentCar.image}
                      alt={`${currentCar.brand} ${currentCar.model}`}
                      className={`w-full h-80 object-cover transition-all duration-500 ${
                        isAnimating ? "scale-110 opacity-0" : "scale-100 opacity-100"
                      }`}
                    />
                    <div className="absolute top-4 right-4 bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white px-4 py-2 rounded-full text-sm font-bold shadow-lg">
                      {currentCar.year}
                    </div>
                  </div>
                </div>

                {/* Car Details */}
                <div className={`space-y-6 transition-all duration-500 ${
                  isAnimating ? "translate-x-10 opacity-0" : "translate-x-0 opacity-100"
                }`}>
                  {/* Brand & Model */}
                  <div>
                    <div className="text-[#d4a574] text-sm font-semibold mb-2 uppercase tracking-wider">
                      {currentCar.brand}
                    </div>
                    <h3 className="text-4xl md:text-5xl font-bold text-white mb-2">
                      {currentCar.model}
                    </h3>
                    <p className="text-white/70 leading-relaxed">
                      {currentCar.description}
                    </p>
                  </div>

                  {/* Specs */}
                  <div className="grid grid-cols-3 gap-4">
                    <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
                      <Gauge className="h-6 w-6 text-[#c89968] mb-2" />
                      <div className="text-white font-bold text-sm">{currentCar.specs.horsepower}</div>
                      <div className="text-white/50 text-xs">Power</div>
                    </div>
                    <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
                      <Zap className="h-6 w-6 text-[#c89968] mb-2" />
                      <div className="text-white font-bold text-sm">{currentCar.specs.acceleration}</div>
                      <div className="text-white/50 text-xs">0-60 mph</div>
                    </div>
                    <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
                      <Star className="h-6 w-6 text-[#c89968] mb-2" />
                      <div className="text-white font-bold text-sm">{currentCar.specs.topSpeed}</div>
                      <div className="text-white/50 text-xs">Top Speed</div>
                    </div>
                  </div>

                  {/* Color & Price */}
                  <div className="flex items-center justify-between pt-4 border-t border-white/10">
                    <div>
                      <div className="text-white/50 text-sm mb-1">Color</div>
                      <div className="text-white font-semibold">{currentCar.color}</div>
                    </div>
                    <div className="text-right">
                      <div className="text-white/50 text-sm mb-1">Starting at</div>
                      <div className="text-3xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
                        ${currentCar.price.toLocaleString()}
                      </div>
                    </div>
                  </div>

                  {/* CTA Button */}
                  <Button 
                    size="lg" 
                    className="w-full bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white h-14 text-base font-semibold shadow-lg"
                  >
                    View Details
                  </Button>
                </div>
              </div>
            </div>

            {/* Navigation Arrows */}
            <button
              onClick={handlePrevious}
              disabled={isAnimating}
              className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 md:-translate-x-16 w-14 h-14 bg-white/10 backdrop-blur-sm hover:bg-white/20 border border-white/20 rounded-full flex items-center justify-center transition-all disabled:opacity-50 disabled:cursor-not-allowed group"
            >
              <ChevronLeft className="h-6 w-6 text-white group-hover:scale-110 transition-transform" />
            </button>

            <button
              onClick={handleNext}
              disabled={isAnimating}
              className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 md:translate-x-16 w-14 h-14 bg-white/10 backdrop-blur-sm hover:bg-white/20 border border-white/20 rounded-full flex items-center justify-center transition-all disabled:opacity-50 disabled:cursor-not-allowed group"
            >
              <ChevronRight className="h-6 w-6 text-white group-hover:scale-110 transition-transform" />
            </button>
          </div>

          {/* Dots Indicator */}
          <div className="flex justify-center gap-2 mt-8">
            {featuredCars.map((_, index) => (
              <button
                key={index}
                onClick={() => {
                  if (!isAnimating) {
                    setIsAnimating(true);
                    setCurrentIndex(index);
                    setTimeout(() => setIsAnimating(false), 500);
                  }
                }}
                className={`transition-all duration-300 rounded-full ${
                  index === currentIndex
                    ? "w-8 h-2 bg-gradient-to-r from-[#c89968] to-[#d4a574]"
                    : "w-2 h-2 bg-white/30 hover:bg-white/50"
                }`}
              />
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
