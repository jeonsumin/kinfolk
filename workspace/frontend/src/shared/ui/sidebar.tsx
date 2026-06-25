"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { LogOut, Settings, UserRound } from "lucide-react";
import { signOut } from "next-auth/react";
import { cn } from "@/shared/utils/index";
import { SIDEBAR_NAV } from "@/shared/config";
import { useAuthStore } from "@/stores/auth-store";
import { logout } from "@/shared/api";

export function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { currentWorkspace, userName, reset } = useAuthStore();
  const initial = userName ? userName.charAt(0) : "K";

  const handleLogout = async () => {
    try {
      await signOut({ redirect: false });
      await logout();
    } catch {
      // 백엔드 로그아웃 실패와 무관하게 클라이언트 세션은 종료한다.
    } finally {
      reset();
      router.replace("/login");
      router.refresh();
    }
  };

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
      <div className="relative px-3 py-3 border-t border-sidebar-border">
        <details className="group">
          <summary className="flex cursor-pointer list-none items-center gap-3 rounded-lg px-3 py-2.5 transition-colors hover:bg-sidebar-accent/60 [&::-webkit-details-marker]:hidden">
            <div className="w-8 h-8 rounded-full bg-sidebar-primary flex items-center justify-center shrink-0 text-xs font-bold text-sidebar-primary-foreground">
              {initial}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold text-sidebar-foreground truncate leading-snug">
                {currentWorkspace?.name ?? "워크스페이스"}
              </p>
              <p className="text-[11px] text-sidebar-foreground/50 truncate leading-snug">
                {userName || "프로필 설정하기"}
              </p>
            </div>
            <UserRound size={14} className="text-sidebar-foreground/40 shrink-0" />
          </summary>
          <div className="absolute bottom-[68px] left-3 right-3 z-20 overflow-hidden rounded-lg border border-sidebar-border bg-sidebar shadow-xl">
            <Link
              href="/settings"
              className="flex items-center gap-2 px-3 py-2 text-xs text-sidebar-foreground/80 transition-colors hover:bg-sidebar-accent"
            >
              <Settings size={14} />
              설정
            </Link>
            <button
              type="button"
              onClick={handleLogout}
              className="flex w-full items-center gap-2 px-3 py-2 text-left text-xs text-sidebar-foreground/80 transition-colors hover:bg-sidebar-accent"
            >
              <LogOut size={14} />
              로그아웃
            </button>
          </div>
        </details>
      </div>
    </aside>
  );
}
