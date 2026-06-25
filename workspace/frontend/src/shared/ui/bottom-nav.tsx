"use client";

import Link from "next/link";
import {usePathname} from "next/navigation";
import {Plus} from "lucide-react";
import {cn} from "@/shared/utils/index";
import {BOTTOM_NAV, SIDEBAR_NAV} from "@/shared/config";

export function BottomNav() {
    const pathname = usePathname();

    return (
        <nav className="lg:hidden fixed bottom-0 inset-x-0 bg-card border-t border-border z-50">
            <div className="flex items-center justify-around h-16 px-2 relative">
                {SIDEBAR_NAV.map((nav) => (

                    <NavItem
                        href={nav.href}
                        label={nav.label}
                        icon={nav.icon}
                        active={pathname === nav.href}
                    />
                ))}
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
            <Icon size={20} strokeWidth={active ? 2.2 : 1.8}/>
            <span className="text-[10px] font-medium">{label}</span>
        </Link>
    );
}
