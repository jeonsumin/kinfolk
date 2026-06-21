"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Plus } from "lucide-react";
import { cn } from "@/shared/utils/index";
import { SIDEBAR_NAV } from "@/shared/config";

export function Sidebar() {
  const pathname = usePathname();

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

      {/* Bottom action */}
      <div className="px-4 py-4 border-t border-sidebar-border">
        <button className="w-full flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg bg-sidebar-primary text-sidebar-primary-foreground text-sm font-semibold hover:bg-sidebar-primary/90 transition-colors">
          <Plus size={16} strokeWidth={2.5} />
          뽀빠 추가
        </button>
      </div>
    </aside>
  );
}
