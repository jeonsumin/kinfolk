# Commands

- Frontend checks: cd workspace/frontend && npm run lint; npx tsc --noEmit; production build: npm run build.
- Backend checks: cd workspace/backend-jwt && ./mvnw test or ./mvnw -DskipTests compile.
- Project state: git status --short, git diff --check from repository root.