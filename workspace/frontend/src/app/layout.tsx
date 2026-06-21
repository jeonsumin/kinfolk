import type { Metadata } from "next";
import { Plus_Jakarta_Sans } from "next/font/google";
import "./globals.css";
import { Sidebar } from "@/shared/ui/sidebar";
import { BottomNav } from "@/shared/ui/bottom-nav";

const plusJakartaSans = Plus_Jakarta_Sans({
  variable: "--font-sans",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
});

export const metadata: Metadata = {
  title: "Kinfolk Table",
  description: "가족을 위한 공간",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className={`${plusJakartaSans.variable} h-full`}>
      <body className="h-full flex antialiased">
        <Sidebar />
        <div className="flex flex-col flex-1 min-w-0 h-full overflow-hidden pb-16 lg:pb-0">
          {children}
        </div>
        <BottomNav />
      </body>
    </html>
  );
}
