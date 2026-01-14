"use client";

import { useEffect } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Car, ShieldCheck, Zap, Clock } from "lucide-react";
import { LuxuryVehicleConfigurator } from "@/components/home/luxury-vehicle-configurator";
import { useAuthStore } from "@/store/auth.store";
import { UserRole } from "@/types/user.types";
import { userService } from "@/lib/api/user.service";
import { useState } from "react";

export default function HomePage() {
  const { user, isAuthenticated } = useAuthStore();
  const [userProfile, setUserProfile] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated && user) {
      setLoading(true);
      userService.getProfile()
        .then((profile) => {
          setUserProfile(profile);
        })
        .catch(() => {
          // If profile fetch fails, use the user from auth store
          setUserProfile(null);
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      // Clear profile when not authenticated
      setUserProfile(null);
    }
  }, [isAuthenticated, user?.id]); // Use user.id instead of entire user object to avoid unnecessary re-renders

  // Check if user is admin - check both role field and roles array
  const isAdmin = user?.role === UserRole.ADMIN || 
                 userProfile?.roles?.includes("ADMIN") === true;

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="py-24 px-4 md:py-32 bg-gradient-to-br from-[#f5ede4] via-[#e8d5c4] to-[#f5ede4]">
        <div className="container mx-auto text-center space-y-8">
          {isAuthenticated && user ? (
            <>
              <div className="inline-block mb-4">
                <span className="px-4 py-2 bg-white/50 rounded-full text-sm font-medium text-[#8b7355] backdrop-blur-sm border border-[#c89968]/20">
                  ✨ {isAdmin ? "Welcome Admin" : "Welcome User"}
                </span>
              </div>
              <h1 className="text-5xl md:text-7xl font-bold tracking-tight bg-gradient-to-r from-[#8b7355] via-[#c89968] to-[#8b7355] bg-clip-text text-transparent">
                {isAdmin ? "Admin Dashboard" : "User Dashboard"}
              </h1>
              <p className="text-xl text-[#4a3f35]/80 max-w-3xl mx-auto leading-relaxed">
                {isAdmin 
                  ? "Manage your platform, monitor activities, and oversee operations."
                  : `Welcome back, ${user.firstName}! Manage your account and explore our vehicle collection.`
                }
              </p>
            </>
          ) : (
            <>
              <div className="inline-block mb-4">
                <span className="px-4 py-2 bg-white/50 rounded-full text-sm font-medium text-[#8b7355] backdrop-blur-sm border border-[#c89968]/20">
                  ✨ Welcome to BennyCar
                </span>
              </div>
              <h1 className="text-5xl md:text-7xl font-bold tracking-tight bg-gradient-to-r from-[#8b7355] via-[#c89968] to-[#8b7355] bg-clip-text text-transparent">
                Find Your Perfect Vehicle
              </h1>
              <p className="text-xl text-[#4a3f35]/80 max-w-3xl mx-auto leading-relaxed">
                Browse our extensive collection of vehicles, customize them to your preferences,
                and purchase online with confidence. Experience luxury car buying reimagined.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center pt-6">
                <Button 
                  size="lg" 
                  asChild 
                  className="bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:from-[#d4a574] hover:to-[#c89968] text-white shadow-lg shadow-[#c89968]/30 h-14 px-8 text-base"
                >
                  <Link href="/vehicles">
                    <Car className="mr-2 h-5 w-5" />
                    Browse Vehicles
                  </Link>
                </Button>
                <Button 
                  size="lg" 
                  variant="outline" 
                  asChild 
                  className="border-2 border-[#c89968] text-[#8b7355] hover:bg-[#f5ede4] h-14 px-8 text-base"
                >
                  <Link href="/auth/register">Get Started</Link>
                </Button>
              </div>
            </>
          )}
        </div>
      </section>

      {/* Luxury Vehicle Configurator - Only show for non-authenticated users */}
      {!isAuthenticated && <LuxuryVehicleConfigurator />}

      {/* Why Choose BennyCar - Only show for non-authenticated users */}
      {!isAuthenticated && (
      <section className="py-24 px-4 bg-white">
        <div className="container mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold mb-4 text-[#4a3f35]">
              Why Choose <span className="text-[#c89968]">BennyCar</span>?
            </h2>
            <p className="text-[#4a3f35]/70 max-w-2xl mx-auto">
              Experience the future of automotive retail with our premium platform
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="group p-8 rounded-2xl bg-gradient-to-br from-[#f5ede4] to-white hover:shadow-xl transition-all duration-300 border border-[#e8d5c4]">
              <div className="h-16 w-16 rounded-2xl bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                <Car className="h-8 w-8 text-white" />
              </div>
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Wide Selection</h3>
              <p className="text-[#4a3f35]/70 leading-relaxed">
                Choose from hundreds of vehicles from top brands worldwide
              </p>
            </div>

            <div className="group p-8 rounded-2xl bg-gradient-to-br from-[#f5ede4] to-white hover:shadow-xl transition-all duration-300 border border-[#e8d5c4]">
              <div className="h-16 w-16 rounded-2xl bg-gradient-to-br from-[#d4a574] to-[#c89968] flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                <Zap className="h-8 w-8 text-white" />
              </div>
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Easy Customization</h3>
              <p className="text-[#4a3f35]/70 leading-relaxed">
                Personalize your vehicle with our intuitive configuration tool
              </p>
            </div>

            <div className="group p-8 rounded-2xl bg-gradient-to-br from-[#f5ede4] to-white hover:shadow-xl transition-all duration-300 border border-[#e8d5c4]">
              <div className="h-16 w-16 rounded-2xl bg-gradient-to-br from-[#8b7355] to-[#c89968] flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                <ShieldCheck className="h-8 w-8 text-white" />
              </div>
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Secure Transactions</h3>
              <p className="text-[#4a3f35]/70 leading-relaxed">
                Your purchase is protected with industry-leading security
              </p>
            </div>

            <div className="group p-8 rounded-2xl bg-gradient-to-br from-[#f5ede4] to-white hover:shadow-xl transition-all duration-300 border border-[#e8d5c4]">
              <div className="h-16 w-16 rounded-2xl bg-gradient-to-br from-[#c89968] to-[#8b7355] flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                <Clock className="h-8 w-8 text-white" />
              </div>
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Fast Delivery</h3>
              <p className="text-[#4a3f35]/70 leading-relaxed">
                Get your vehicle delivered to your doorstep quickly and safely
              </p>
            </div>
          </div>
        </div>
      </section>
      )}

      {/* CTA Section - Only show for non-authenticated users */}
      {!isAuthenticated && (
        <section className="py-24 px-4 bg-gradient-to-br from-[#c89968] via-[#d4a574] to-[#c89968]">
          <div className="container mx-auto text-center space-y-8">
            <h2 className="text-4xl md:text-5xl font-bold text-white">
              Ready to Get Started?
            </h2>
            <p className="text-xl text-white/90 max-w-2xl mx-auto leading-relaxed">
              Join thousands of satisfied customers who found their dream vehicle with BennyCar
            </p>
            <Button 
              size="lg" 
              asChild 
              className="bg-white text-[#8b7355] hover:bg-[#f5ede4] shadow-xl h-14 px-8 text-base font-semibold"
            >
              <Link href="/auth/register">Create Your Account</Link>
            </Button>
          </div>
        </section>
      )}
    </div>
  );
}
