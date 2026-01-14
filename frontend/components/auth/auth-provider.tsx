"use client";

import { useEffect, useRef } from "react";
import { useAuthStore } from "@/store/auth.store";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const checkAuth = useAuthStore((state) => state.checkAuth);
  const hasChecked = useRef(false);

  useEffect(() => {
    // Check authentication status only once on mount
    if (!hasChecked.current) {
      hasChecked.current = true;
      // Wrap in try-catch to prevent errors from crashing the app
      checkAuth().catch((error) => {
        // Silently handle auth check errors (e.g., invalid token, network issues)
        console.debug("Auth check failed:", error);
      });
    }
  }, []); // Empty dependency array - only run once on mount

  return <>{children}</>;
}
