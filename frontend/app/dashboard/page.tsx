"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/auth.store";
import { userService, UserProfile } from "@/lib/api/user.service";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { 
  User, 
  Mail, 
  Phone, 
  MapPin, 
  Calendar, 
  Shield, 
  ShoppingCart,
  Car,
  Loader2,
  Edit,
  CheckCircle,
  XCircle
} from "lucide-react";
import { formatDate } from "@/lib/utils";

export default function DashboardPage() {
  const router = useRouter();
  const { isAuthenticated, user: authUser } = useAuthStore();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/auth/login");
      return;
    }
    loadProfile();
  }, [isAuthenticated]);

  const loadProfile = async () => {
    setLoading(true);
    setError(null);
    try {
      const profileData = await userService.getProfile();
      setProfile(profileData);
    } catch (err) {
      setError("Failed to load profile");
      console.error("Error loading profile:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#fafaf8] flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="h-12 w-12 animate-spin text-[#c89968] mx-auto mb-4" />
          <p className="text-[#8b7355]">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div className="min-h-screen bg-[#fafaf8] flex items-center justify-center">
        <Card className="w-full max-w-md border-2 border-red-200">
          <CardContent className="py-8 text-center">
            <XCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-[#4a3f35] mb-2">
              {error || "Profile not found"}
            </h3>
            <Button onClick={loadProfile} className="mt-4">
              Try Again
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return "bg-green-100 text-green-700 border-green-200";
      case "INACTIVE":
        return "bg-yellow-100 text-yellow-700 border-yellow-200";
      case "SUSPENDED":
        return "bg-red-100 text-red-700 border-red-200";
      default:
        return "bg-gray-100 text-gray-700 border-gray-200";
    }
  };

  return (
    <div className="min-h-screen bg-[#fafaf8]">
      <div className="container mx-auto px-4 py-12">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent mb-2">
            Welcome back, {profile.firstName}!
          </h1>
          <p className="text-[#8b7355]">
            Manage your account and track your activity
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Profile Card */}
          <div className="lg:col-span-2 space-y-6">
            {/* Profile Information */}
            <Card className="border-2 border-[#e8d5c4] shadow-lg">
              <CardHeader className="pb-4">
                <div className="flex items-center justify-between">
                  <CardTitle className="text-2xl flex items-center gap-2">
                    <User className="h-6 w-6 text-[#c89968]" />
                    Profile Information
                  </CardTitle>
                  <Button variant="outline" size="sm" asChild>
                    <Link href="/dashboard/edit">
                      <Edit className="h-4 w-4 mr-2" />
                      Edit
                    </Link>
                  </Button>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Profile Picture & Name */}
                <div className="flex items-center gap-6 pb-6 border-b-2 border-[#e8d5c4]">
                  <div className="h-24 w-24 rounded-full bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center text-white text-3xl font-bold shadow-lg">
                    {profile.firstName[0]}{profile.lastName[0]}
                  </div>
                  <div className="flex-1">
                    <h2 className="text-2xl font-bold text-[#4a3f35] mb-1">
                      {profile.firstName} {profile.lastName}
                    </h2>
                    <div className="flex items-center gap-2 text-[#8b7355]">
                      <Mail className="h-4 w-4" />
                      <span>{profile.email}</span>
                      {profile.emailVerified !== false && (
                        <CheckCircle className="h-4 w-4 text-green-500" title="Email verified" />
                      )}
                    </div>
                  </div>
                </div>

                {/* Contact Information */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {profile.phoneNumber && (
                    <div className="flex items-start gap-3 p-4 bg-[#f5ede4] rounded-xl border border-[#e8d5c4]">
                      <Phone className="h-5 w-5 text-[#c89968] mt-0.5" />
                      <div>
                        <div className="text-sm text-[#8b7355] mb-1">Phone</div>
                        <div className="font-semibold text-[#4a3f35]">{profile.phoneNumber}</div>
                      </div>
                    </div>
                  )}

                  {profile.address && (
                    <div className="flex items-start gap-3 p-4 bg-[#f5ede4] rounded-xl border border-[#e8d5c4]">
                      <MapPin className="h-5 w-5 text-[#c89968] mt-0.5" />
                      <div>
                        <div className="text-sm text-[#8b7355] mb-1">Address</div>
                        <div className="font-semibold text-[#4a3f35]">{profile.address}</div>
                      </div>
                    </div>
                  )}

                  <div className="flex items-start gap-3 p-4 bg-[#f5ede4] rounded-xl border border-[#e8d5c4]">
                    <Calendar className="h-5 w-5 text-[#c89968] mt-0.5" />
                    <div>
                      <div className="text-sm text-[#8b7355] mb-1">Member Since</div>
                      <div className="font-semibold text-[#4a3f35]">
                        {formatDate(profile.createdAt)}
                      </div>
                    </div>
                  </div>

                  <div className="flex items-start gap-3 p-4 bg-[#f5ede4] rounded-xl border border-[#e8d5c4]">
                    <Shield className="h-5 w-5 text-[#c89968] mt-0.5" />
                    <div>
                      <div className="text-sm text-[#8b7355] mb-1">Account Status</div>
                      <div className={`px-3 py-1 rounded-full text-sm font-semibold border-2 inline-block ${getStatusColor(profile.status)}`}>
                        {profile.status}
                      </div>
                    </div>
                  </div>
                </div>

                {/* Roles */}
                {profile.roles && profile.roles.length > 0 && (
                  <div className="pt-4 border-t-2 border-[#e8d5c4]">
                    <div className="text-sm text-[#8b7355] mb-2">Roles</div>
                    <div className="flex flex-wrap gap-2">
                      {profile.roles.map((role) => (
                        <span
                          key={role}
                          className="px-3 py-1 bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white rounded-full text-sm font-semibold"
                        >
                          {role}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>

          {/* Quick Actions Sidebar */}
          <div className="space-y-6">
            {/* Quick Actions */}
            <Card className="border-2 border-[#e8d5c4] shadow-lg">
              <CardHeader>
                <CardTitle className="text-xl">Quick Actions</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <Button asChild className="w-full justify-start" variant="outline">
                  <Link href="/orders">
                    <ShoppingCart className="h-4 w-4 mr-2" />
                    My Orders
                  </Link>
                </Button>
                <Button asChild className="w-full justify-start" variant="outline">
                  <Link href="/vehicles">
                    <Car className="h-4 w-4 mr-2" />
                    Browse Vehicles
                  </Link>
                </Button>
                <Button asChild className="w-full justify-start" variant="outline">
                  <Link href="/dashboard/edit">
                    <Edit className="h-4 w-4 mr-2" />
                    Edit Profile
                  </Link>
                </Button>
              </CardContent>
            </Card>

            {/* Account Stats */}
            <Card className="border-2 border-[#e8d5c4] shadow-lg">
              <CardHeader>
                <CardTitle className="text-xl">Account Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between items-center pb-3 border-b border-[#e8d5c4]">
                  <span className="text-[#8b7355]">Account Created</span>
                  <span className="font-semibold text-[#4a3f35]">
                    {formatDate(profile.createdAt)}
                  </span>
                </div>
                <div className="flex justify-between items-center pb-3 border-b border-[#e8d5c4]">
                  <span className="text-[#8b7355]">Last Updated</span>
                  <span className="font-semibold text-[#4a3f35]">
                    {formatDate(profile.updatedAt)}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-[#8b7355]">Email Status</span>
                  <span className="font-semibold text-[#4a3f35]">
                    {profile.emailVerified !== false ? (
                      <span className="flex items-center gap-1 text-green-600">
                        <CheckCircle className="h-4 w-4" />
                        Verified
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 text-yellow-600">
                        <XCircle className="h-4 w-4" />
                        Unverified
                      </span>
                    )}
                  </span>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}
