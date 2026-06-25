import {
  LayoutDashboard,
  Images,
  Settings,
  ClipboardList,
  Home,
  Calendar,
  ShoppingCart,
} from "lucide-react";

export const SIDEBAR_NAV = [
  { href: "/", label: "대시보드", icon: LayoutDashboard },
  { href: "/calendar", label: "캘린더", icon: Calendar },
  { href: "/planner", label: "플래너", icon: ClipboardList },
  { href: "/shopping", label: "장보기", icon: ShoppingCart },
  { href: "/settings", label: "설정", icon: Settings },
] as const;

export const BOTTOM_NAV = [
  { href: "/", label: "홈", icon: Home },
  { href: "/schedule", label: "캘린더", icon: Calendar },
  { href: "/photos", label: "사진", icon: Images },
] as const;
