import { auth } from "@/auth";
import { NextResponse } from "next/server";

/** 인증 없이 접근 가능한 경로 */
const PUBLIC_PATHS = ["/login","/signup"];

export const proxy = auth((req) => {
  // 개발 환경에서는 인증 없이 접근 허용
  // if (process.env.NODE_ENV === "development") {
  //   return NextResponse.next();
  // }

  const { pathname } = req.nextUrl;

  const isPublic = PUBLIC_PATHS.some(
    (p) => pathname === p || pathname.startsWith(p + "/")
  );

  // 미인증 → /login 리다이렉트
  if (!req.auth && !isPublic) {
    const loginUrl = new URL("/login", req.url);
    loginUrl.searchParams.set("callbackUrl", pathname);
    return NextResponse.redirect(loginUrl);
  }

  // 인증된 사용자가 /login 접근 → 메인으로
  if (req.auth && pathname === "/login") {
    return NextResponse.redirect(new URL("/", req.url));
  }

  return NextResponse.next();
});


export const config = {
  matcher: [
    "/((?!login|signup|onboarding|api/auth|_next/static|_next/image|favicon\\.ico).*)",
  ],
};
