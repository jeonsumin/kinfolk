"use client";

import { Search, Bell } from "lucide-react";
import { MEMBERS, EXTRA_MEMBER_COUNT, APP_NAME } from "@/shared/config";

export function TopBar() {
  const now = new Date();
  const dateStr = now.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "short",
  });

  return (
    <header className="shrink-0 flex items-center justify-between px-4 lg:px-6 py-3 bg-card border-b border-border h-14">
      {/* Mobile: logo / Desktop: date */}
      <div>
        <span className="lg:hidden text-base font-bold text-foreground tracking-tight">
          {APP_NAME}
        </span>
        <p className="hidden lg:block text-sm text-muted-foreground">{dateStr}</p>
      </div>

      <div className="flex items-center gap-3">
        {/* Desktop: weather */}
        <div className="hidden lg:flex items-center gap-1.5 text-sm text-muted-foreground">
          <span>☀️</span>
          <span className="font-medium text-foreground">22°C</span>
        </div>

        {/* Search */}
        <button
          className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-muted text-muted-foreground text-sm hover:bg-accent transition-colors"
          aria-label="검색"
        >
          <Search size={14} strokeWidth={2} />
          <span className="hidden lg:inline text-sm">검색</span>
        </button>

        {/* Mobile: bell */}
        <button
          className="lg:hidden flex items-center justify-center w-8 h-8 rounded-lg bg-muted text-muted-foreground hover:bg-accent transition-colors"
          aria-label="알림"
        >
          <Bell size={16} strokeWidth={1.8} />
        </button>

        {/* Member avatars */}
        <div className="flex items-center -space-x-2">
          {MEMBERS.map((m, i) => (
            <div
              key={i}
              className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-semibold text-white ring-2 ring-card"
              style={{ backgroundColor: m.color }}
            >
              {m.initials}
            </div>
          ))}
          <div className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-semibold text-muted-foreground bg-muted ring-2 ring-card lg:hidden">
            +{EXTRA_MEMBER_COUNT}
          </div>
        </div>
      </div>
    </header>
  );
}
