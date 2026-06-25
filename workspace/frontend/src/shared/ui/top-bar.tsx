"use client";

import {Search, Bell, Home} from "lucide-react";
import { MEMBERS, EXTRA_MEMBER_COUNT, APP_NAME } from "@/shared/config";
import Link from "next/link";

export function TopBar() {
  const now = new Date();
  const dateStr = now.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "short",
  });

  return (
      <header
          className="flex h-16 shrink-0 items-center justify-between border-b border-border bg-background px-4 lg:px-8">
        <div className="flex items-center gap-3">
          <Link href="/planner" className="text-lg font-bold text-primary">Kinfolk Table</Link>
        </div>
      </header>
  );
}
