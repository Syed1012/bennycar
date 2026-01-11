'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { motion } from 'framer-motion';
import { Car, MapPin, Navigation, Compass, ArrowLeft } from 'lucide-react';

interface HeaderProps {
  journeyActive?: boolean;
}

export default function WorldViewHeader({ journeyActive = false }: HeaderProps) {
  const pathname = usePathname();

  const navLinks = [
    { href: '/world-view', label: 'Journey', icon: Navigation },
    { href: '/world-view/vehicle', label: 'Locate Vehicle', icon: Compass },
  ];

  return (
    <motion.header
      className="fixed top-0 left-0 right-0 z-50 bg-white border-b border-[#DADCE0] shadow-sm"
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ duration: 0.5, ease: 'easeOut' }}
    >
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center gap-3">
          <Link href="/" className="mr-2 p-2 rounded-full hover:bg-gray-100 transition-colors">
            <ArrowLeft className="w-5 h-5 text-gray-600" />
          </Link>
          <div className="w-10 h-10 bg-[#4285F4] rounded-lg flex items-center justify-center">
            <Car className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-semibold text-[#202124]">World View</h1>
            <p className="text-xs text-[#5F6368]">Vehicle Tracking</p>
          </div>
        </div>

        {/* Navigation Links */}
        <nav className="flex items-center gap-1">
          {navLinks.map(({ href, label, icon: Icon }) => {
            const isActive = pathname === href;
            return (
              <Link
                key={href}
                href={href}
                className={`flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-[#E8F0FE] text-[#1A73E8] border border-[#4285F4]/30'
                    : 'text-[#5F6368] hover:bg-gray-100'
                }`}
              >
                <Icon className="w-4 h-4" />
                <span className="hidden sm:inline">{label}</span>
              </Link>
            );
          })}
        </nav>

        {/* Right side - Journey indicator */}
        <div className="flex items-center gap-2">
          {journeyActive && pathname === '/world-view' && (
            <motion.div
              className="hidden md:flex items-center gap-2 bg-green-50 px-3 py-1.5 rounded-full border border-green-200"
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
            >
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse" />
              <span className="text-xs text-green-700 font-medium">Live</span>
            </motion.div>
          )}
          <div className="flex items-center gap-2 text-[#5F6368]">
            <MapPin className="w-4 h-4 text-[#EA4335]" />
            <span className="text-sm hidden sm:inline">Stuttgart</span>
          </div>
        </div>
      </div>
    </motion.header>
  );
}
