# Frontend

- Next.js 16 App Router at workspace/frontend; route pages are src/app/<route>/page.tsx.
- Auth: NextAuth Credentials posts to backend /login; client API functions use src/shared/api/client.ts and the barrel @/shared/api.
- Use existing @/shared/ui barrel components and design-token classes. AGENTS.md requires reading relevant Next docs in node_modules/next/dist/docs before changes.
- Auth state uses Zustand in src/stores/auth-store.ts.