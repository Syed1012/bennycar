"use client";

import * as React from "react";
import { cn } from "@/lib/utils";

interface DropdownMenuProps {
  children: React.ReactNode;
  className?: string;
}

interface DropdownMenuTriggerProps {
  children: React.ReactNode;
  className?: string;
}

interface DropdownMenuContentProps {
  children: React.ReactNode;
  className?: string;
  align?: "left" | "right";
}

interface DropdownMenuItemProps {
  children: React.ReactNode;
  onClick?: () => void;
  className?: string;
  asChild?: boolean;
}

const DropdownMenuContext = React.createContext<{
  open: boolean;
  setOpen: (open: boolean) => void;
}>({
  open: false,
  setOpen: () => {},
});

export function DropdownMenu({ children, className }: DropdownMenuProps) {
  const [open, setOpen] = React.useState(false);

  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      if (!target.closest('[data-dropdown-menu]')) {
        setOpen(false);
      }
    };

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
      return () => document.removeEventListener("mousedown", handleClickOutside);
    }
  }, [open]);

  return (
    <DropdownMenuContext.Provider value={{ open, setOpen }}>
      <div className={cn("relative", className)} data-dropdown-menu>
        {children}
      </div>
    </DropdownMenuContext.Provider>
  );
}

export function DropdownMenuTrigger({ children, className }: DropdownMenuTriggerProps) {
  const { open, setOpen } = React.useContext(DropdownMenuContext);

  return (
    <button
      type="button"
      onClick={() => setOpen(!open)}
      className={cn("focus:outline-none", className)}
    >
      {children}
    </button>
  );
}

export function DropdownMenuContent({
  children,
  className,
  align = "right",
}: DropdownMenuContentProps) {
  const { open } = React.useContext(DropdownMenuContext);

  if (!open) return null;

  return (
    <div
      className={cn(
        "absolute z-50 mt-2 min-w-[200px] rounded-lg bg-white shadow-lg border-2 border-[#e8d5c4] py-2",
        align === "right" ? "right-0" : "left-0",
        className
      )}
    >
      {children}
    </div>
  );
}

export function DropdownMenuItem({
  children,
  onClick,
  className,
  asChild = false,
}: DropdownMenuItemProps) {
  const { setOpen } = React.useContext(DropdownMenuContext);

  const handleClick = () => {
    if (onClick) {
      onClick();
    }
    setOpen(false);
  };

  if (asChild && React.isValidElement(children)) {
    return React.cloneElement(children, {
      onClick: handleClick,
      className: cn(
        "w-full text-left px-4 py-2 text-sm text-[#4a3f35] hover:bg-[#f5ede4] cursor-pointer flex items-center gap-2",
        className
      ),
    });
  }

  return (
    <div
      onClick={handleClick}
      className={cn(
        "px-4 py-2 text-sm text-[#4a3f35] hover:bg-[#f5ede4] cursor-pointer flex items-center gap-2 transition-colors",
        className
      )}
    >
      {children}
    </div>
  );
}
