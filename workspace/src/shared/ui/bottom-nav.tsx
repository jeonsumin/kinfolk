"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Plus } from "lucide-react";
import { cn } from "@/shared/utils/index";
import { BOTTOM_NAV } from "@/shared/config";

export function BottomNav() {
  const pathname = usePathname();

  return (
    <nav className="lg:hidden fixed bottom-0 inset-x-0 bg-card border-t border-border z-50">
      <div className="flex items-center justify-around h-16 px-2 relative">
        <NavItem
          href={BOTTOM_NAV[0].href}
          label={BOTTOM_NAV[0].label}
          icon={BOTTOM_NAV[0].icon}
          active={pathname === BOTTOM_NAV[0].href}
        />

        <NavItem
          href={BOTTOM_NAV[1].href}
          label={BOTTOM_NAV[1].label}
          icon={BOTTOM_NAV[1].icon}
          active={pathname === BOTTOM_NAV[1].href}
        />

        {/* FAB center */}
        <div className="relative -top-4">
          <Link
            href="/add"
            className="w-14 h-14 rounded-full bg-foreground text-background flex items-center justify-center shadow-lg hover:bg-foreground/90 transition-colors"
            aria-label="추가"
          >
            <Plus size={24} strokeWidth={2.5} />
          </Link>
          <span className="text-center block text-[10px] text-muted-foreground mt-1">추가</span>
        </div>

        <NavItem
          href={BOTTOM_NAV[2].href}
          label={BOTTOM_NAV[2].label}
          icon={BOTTOM_NAV[2].icon}
          active={pathname === BOTTOM_NAV[2].href}
        />

        {/* Spacer for FAB balance */}
        <div className="w-12" />
      </div>
    </nav>
  );
}

function NavItem({
  href,
  label,
  icon: Icon,
  active,
}: {
  href: string;
  label: string;
  icon: React.ElementType;
  active: boolean;
}) {
  return (
    <Link
      href={href}
      className={cn(
        "flex flex-col items-center gap-0.5 min-w-[48px] py-1 transition-colors",
        active ? "text-foreground" : "text-muted-foreground"
      )}
    >
      <Icon size={20} strokeWidth={active ? 2.2 : 1.8} />
      <span className="text-[10px] font-medium">{label}</span>
    </Link>
  );
}
