"use client";

import { Globe, MapPin, TrendingUp, Users } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function WorldViewPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-[#fafaf8] via-[#f5ede4] to-[#fafaf8]">
      <div className="container mx-auto px-4 py-12">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="inline-flex items-center gap-3 mb-4">
            <div className="h-16 w-16 rounded-2xl bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center shadow-xl">
              <Globe className="h-8 w-8 text-white" />
            </div>
          </div>
          <h1 className="text-5xl font-bold bg-linear-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent mb-4">
            World View
          </h1>
          <p className="text-[#8b7355] text-lg max-w-2xl mx-auto">
            Explore global automotive trends, market insights, and real-time statistics from around the world
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12 max-w-7xl mx-auto">
          <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
            <CardHeader className="pb-3">
              <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center mb-2">
                <Globe className="h-6 w-6 text-white" />
              </div>
              <CardTitle className="text-lg">Global Sales</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-[#c89968]">2.5M+</div>
              <p className="text-sm text-[#8b7355]">Vehicles sold worldwide</p>
            </CardContent>
          </Card>

          <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
            <CardHeader className="pb-3">
              <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#d4a574] to-[#c89968] flex items-center justify-center mb-2">
                <MapPin className="h-6 w-6 text-white" />
              </div>
              <CardTitle className="text-lg">Countries</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-[#c89968]">150+</div>
              <p className="text-sm text-[#8b7355]">Active markets</p>
            </CardContent>
          </Card>

          <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
            <CardHeader className="pb-3">
              <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#8b7355] to-[#c89968] flex items-center justify-center mb-2">
                <Users className="h-6 w-6 text-white" />
              </div>
              <CardTitle className="text-lg">Happy Customers</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-[#c89968]">1M+</div>
              <p className="text-sm text-[#8b7355]">Satisfied buyers</p>
            </CardContent>
          </Card>

          <Card className="border-2 border-[#e8d5c4] shadow-lg hover:shadow-xl transition-shadow">
            <CardHeader className="pb-3">
              <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-[#c89968] to-[#8b7355] flex items-center justify-center mb-2">
                <TrendingUp className="h-6 w-6 text-white" />
              </div>
              <CardTitle className="text-lg">Growth Rate</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-[#c89968]">+45%</div>
              <p className="text-sm text-[#8b7355]">Year over year</p>
            </CardContent>
          </Card>
        </div>

        {/* Main Content */}
        <div className="max-w-7xl mx-auto">
          <Card className="border-2 border-[#e8d5c4] shadow-2xl">
            <CardContent className="p-12">
              <div className="text-center space-y-6">
                <div className="inline-block p-6 rounded-full bg-gradient-to-br from-[#f5ede4] to-[#e8d5c4]">
                  <Globe className="h-24 w-24 text-[#c89968]" />
                </div>
                <h2 className="text-3xl font-bold text-[#4a3f35]">
                  World View Service
                </h2>
                <p className="text-[#8b7355] text-lg max-w-2xl mx-auto leading-relaxed">
                  The World View service provides comprehensive insights into global automotive markets, 
                  trends, and statistics. This feature will be integrated with our backend service to 
                  deliver real-time data and analytics.
                </p>
                <div className="pt-6">
                  <div className="inline-block px-6 py-3 bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white rounded-full text-sm font-semibold">
                    🚀 Coming Soon - Backend Integration in Progress
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Features Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-12 max-w-7xl mx-auto">
          <Card className="border-2 border-[#e8d5c4] shadow-lg">
            <CardContent className="pt-6">
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Market Analytics</h3>
              <p className="text-[#8b7355]">
                Real-time market analysis and trends from major automotive markets worldwide
              </p>
            </CardContent>
          </Card>

          <Card className="border-2 border-[#e8d5c4] shadow-lg">
            <CardContent className="pt-6">
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Sales Insights</h3>
              <p className="text-[#8b7355]">
                Comprehensive sales data and performance metrics across different regions
              </p>
            </CardContent>
          </Card>

          <Card className="border-2 border-[#e8d5c4] shadow-lg">
            <CardContent className="pt-6">
              <h3 className="font-bold text-xl mb-3 text-[#4a3f35]">Global Trends</h3>
              <p className="text-[#8b7355]">
                Stay updated with the latest trends in electric vehicles, autonomous driving, and more
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
