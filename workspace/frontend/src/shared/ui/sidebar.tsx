"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronRight } from "lucide-react";
import { cn } from "@/shared/utils/index";
import { SIDEBAR_NAV } from "@/shared/config";
import { useAuthStore } from "@/stores/auth-store";

export function Sidebar() {
  const pathname = usePathname();
  const { currentWorkspace, userName } = useAuthStore();

  return (
    <aside className="hidden lg:flex w-56 shrink-0 flex-col h-full bg-sidebar text-sidebar-foreground">
      {/* Logo */}
      <div className="px-5 py-5 border-b border-sidebar-border">
        <p className="text-[11px] font-semibold uppercase tracking-widest text-sidebar-primary opacity-70 mb-0.5">
          Kinfolk
        </p>
        <h1 className="text-base font-bold leading-tight text-sidebar-foreground">
          Table
        </h1>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
        {SIDEBAR_NAV.map(({ href, label, icon: Icon }) => {
          const active = pathname === href;
          return (
            <Link
              key={href}
              href={href}
              className={cn(
                "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors",
                active
                  ? "bg-sidebar-accent text-sidebar-accent-foreground font-medium"
                  : "text-sidebar-foreground/70 hover:bg-sidebar-accent/60 hover:text-sidebar-foreground"
              )}
            >
              <Icon size={16} strokeWidth={1.8} />
              <span>{label}</span>
            </Link>
          );
        })}
      </nav>

      {/* Profile / Workspace switcher */}
      <div className="px-3 py-3 border-t border-sidebar-border">
        <Link
          href="/workspaces"
          className="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-sidebar-accent/60 transition-colors group"
        >
          <div className="w-8 h-8 rounded-full bg-sidebar-primary flex items-center justify-center shrink-0 text-xs font-bold text-sidebar-primary-foreground">
            {userName ? userName.charAt(0) : "K"}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-semibold text-sidebar-foreground truncate leading-snug">
              {currentWorkspace?.name ?? "워크스페이스"}
            </p>
            <p className="text-[11px] text-sidebar-foreground/50 truncate leading-snug">
              {userName || "프로필 설정하기"}
            </p>
          </div>
          <ChevronRight
            size={14}
            className="text-sidebar-foreground/30 group-hover:text-sidebar-foreground/60 transition-colors shrink-0"
          />
        </Link>
      </div>
    </aside>
  );
}
