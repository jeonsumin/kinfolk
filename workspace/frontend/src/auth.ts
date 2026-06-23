import NextAuth from "next-auth";
import Credentials from "next-auth/providers/credentials";

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [
    Credentials({
      credentials: {
        username: {},
        password: {},
      },
      async authorize(credentials) {
        if (!credentials?.username || !credentials?.password) return null;

        try {
          const res = await fetch(`${API_BASE}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              username: credentials.username,
              password: credentials.password,
            }),
          });

          const body = await res.json();
          if (!res.ok || !body.success) return null;

          return {
            id: "user",
            accessToken: body.data.accessToken as string,
            refreshToken: body.data.refreshToken as string,
          };
        } catch {
          return null;
        }
      },
    }),
  ],
  callbacks: {
    jwt({ token, user }) {
      if (user) {
        const u = user as typeof user & {
          accessToken: string;
          refreshToken: string;
        };
        token.accessToken = u.accessToken;
        token.refreshToken = u.refreshToken;
      }
      return token;
    },
    session({ session, token }) {
      return {
        ...session,
        accessToken: token.accessToken as string,
        refreshToken: token.refreshToken as string,
      };
    },
  },
  pages: {
    signIn: "/login",
  },
  session: { strategy: "jwt" },
});
