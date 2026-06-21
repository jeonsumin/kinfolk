import {
  LayoutDashboard,
  Users,
  CalendarPlus,
  Images,
  TreePine,
  Share2,
  Settings,
  ClipboardList,
  Home,
  Calendar,
} from "lucide-react";

export const SIDEBAR_NAV = [
  { href: "/", label: "대시보드", icon: LayoutDashboard },
  { href: "/members", label: "멤버", icon: Users },
  { href: "/schedule", label: "일정 추가", icon: CalendarPlus },
  { href: "/photos", label: "사진첩", icon: Images },
  { href: "/family-tree", label: "가족트리스토리", icon: TreePine },
  { href: "/sharing", label: "사진공유", icon: Share2 },
  { href: "/settings", label: "설정", icon: Settings },
  { href: "/items", label: "사용 물품 목록", icon: ClipboardList },
] as const;

export const BOTTOM_NAV = [
  { href: "/", label: "홈", icon: Home },
  { href: "/schedule", label: "캘린더", icon: Calendar },
  { href: "/photos", label: "사진", icon: Images },
] as const;
