import { Car } from "lucide-react";

export function Footer() {
  return (
    <footer className="mt-auto bg-gradient-to-br from-[#4a3f35] via-[#5a4d42] to-[#4a3f35] text-white">
      <div className="container mx-auto py-12 md:py-16 px-4 max-w-7xl">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
          {/* Brand Section */}
          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <div className="h-10 w-10 rounded-full bg-gradient-to-br from-[#c89968] to-[#d4a574] flex items-center justify-center shadow-lg">
                <Car className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold text-[#e8d5c4]">BennyCar</span>
            </div>
            <p className="text-sm text-[#e8d5c4]/80 leading-relaxed">
              Your trusted platform for purchasing vehicles online.
              Experience luxury, quality, and convenience.
            </p>
          </div>

          {/* Shop Links */}
          <div>
            <h4 className="font-semibold mb-4 text-[#c89968] text-base">Shop</h4>
            <ul className="space-y-3 text-sm">
              <li>
                <a href="/vehicles" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  All Vehicles
                </a>
              </li>
              <li>
                <a href="/vehicles?type=sedan" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  Sedans
                </a>
              </li>
              <li>
                <a href="/vehicles?type=suv" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  SUVs
                </a>
              </li>
              <li>
                <a href="/vehicles?type=electric" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  Electric
                </a>
              </li>
            </ul>
          </div>

          {/* Support Links */}
          <div>
            <h4 className="font-semibold mb-4 text-[#c89968] text-base">Support</h4>
            <ul className="space-y-3 text-sm">
              <li>
                <a href="/help" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  Help Center
                </a>
              </li>
              <li>
                <a href="/contact" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  Contact Us
                </a>
              </li>
              <li>
                <a href="/faq" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  FAQ
                </a>
              </li>
            </ul>
          </div>

          {/* Legal Links */}
          <div>
            <h4 className="font-semibold mb-4 text-[#c89968] text-base">Legal</h4>
            <ul className="space-y-3 text-sm">
              <li>
                <a href="/privacy" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  Privacy Policy
                </a>
              </li>
              <li>
                <a href="/terms" className="text-[#e8d5c4]/80 hover:text-[#c89968] transition-colors">
                  Terms of Service
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="pt-8 border-t border-[#e8d5c4]/20 text-center">
          <p className="text-sm text-[#e8d5c4]/60">
            &copy; {new Date().getFullYear()} BennyCar. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
}
