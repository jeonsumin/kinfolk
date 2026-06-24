"use client";

import type { ReactNode } from "react";
import { usePathname } from "next/navigation";
import { Sidebar } from "@/shared/ui/sidebar";
import { BottomNav } from "@/shared/ui/bottom-nav";

const AUTH_PREFIXES = ["/login", "/onboarding","/signup"];

export function ConditionalShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const isAuthRoute = AUTH_PREFIXES.some(
    (prefix) => pathname === prefix || pathname.startsWith(prefix + "/")
  );

  if (isAuthRoute) {
    return <div className="h-full">{children}</div>;
  }

  return (
    <div className="flex h-full">
      <Sidebar />
      <div className="flex flex-col flex-1 min-w-0 h-full overflow-hidden pb-16 lg:pb-0">
        {children}
      </div>
      <BottomNav />
    </div>
  );
}
