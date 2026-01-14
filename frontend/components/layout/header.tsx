"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { Car, User, ShoppingCart, LogOut, Mail, Home, Globe } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/store/auth.store";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";

export function Header() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, isAuthenticated, logout } = useAuthStore();

  const handleLogout = async () => {
    await logout();
    router.push("/auth/login");
  };

  // Navigation links - always visible for everyone
  const publicNavLinks = [
    { href: "/", label: isAuthenticated ? "Dashboard" : "Home", icon: Home },
    { href: "/vehicles", label: "Browse Cars", icon: Car },
    { href: "/world-view", label: "World View", icon: Globe },
    { href: "/contact", label: "Contact Us", icon: Mail },
  ];

  const authNavLinks = [
    { href: "/orders", label: "My Orders", icon: ShoppingCart },
  ];

  return (
    <div className="fixed top-0 left-0 right-0 z-50 flex justify-center pt-4 px-4">
      <header className="w-full max-w-7xl bg-white/95 backdrop-blur-xl rounded-2xl shadow-xl border-2 border-[#e8d5c4]">
        <div className="flex h-16 items-center justify-between px-6">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-2 group">
            <div className="h-10 w-10 rounded-full bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
              <Car className="h-5 w-5 text-white" />
            </div>
            <span className="text-xl font-bold bg-gradient-to-r from-[#c89968] to-[#8b7355] bg-clip-text text-transparent">
              BennyCar
            </span>
          </Link>

          {/* Navigation - Always visible */}
          <nav className="hidden lg:flex items-center gap-1">
            {publicNavLinks.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                  pathname === link.href
                    ? "bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white"
                    : "text-[#4a3f35] hover:bg-[#f5ede4]"
                }`}
              >
                <link.icon className="h-4 w-4" />
                {link.label}
              </Link>
            ))}
            
            {/* Authenticated links */}
            {isAuthenticated && authNavLinks.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                  pathname === link.href
                    ? "bg-gradient-to-r from-[#c89968] to-[#d4a574] text-white"
                    : "text-[#4a3f35] hover:bg-[#f5ede4]"
                }`}
              >
                <link.icon className="h-4 w-4" />
                {link.label}
              </Link>
            ))}
          </nav>

          {/* Auth Section */}
          <div className="flex items-center gap-3">
            {isAuthenticated && user ? (
              <DropdownMenu>
                <DropdownMenuTrigger>
                  <div className="flex items-center gap-2 px-3 py-2 rounded-full bg-gradient-to-r from-[#f5ede4] to-[#e8d5c4] border border-[#e8d5c4] hover:border-[#c89968] transition-colors cursor-pointer">
                    <div className="h-8 w-8 rounded-full bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center text-white text-sm font-bold">
                      {user.firstName[0]}{user.lastName[0]}
                    </div>
                    <span className="hidden md:inline text-sm font-semibold text-[#4a3f35]">
                      {user.firstName} {user.lastName}
                    </span>
                  </div>
                </DropdownMenuTrigger>
                <DropdownMenuContent>
                  <DropdownMenuItem onClick={handleLogout}>
                    <LogOut className="h-4 w-4" />
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            ) : (
              <div className="flex items-center gap-2">
                <Button 
                  variant="ghost" 
                  size="sm" 
                  asChild
                >
                  <Link href="/auth/login">Login</Link>
                </Button>
                <Button 
                  size="sm" 
                  asChild
                >
                  <Link href="/auth/register">Get Started</Link>
                </Button>
              </div>
            )}
          </div>
        </div>
      </header>
    </div>
  );
}
