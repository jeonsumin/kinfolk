import { createPersistedStore } from "@/shared/store";
import type { UserProfile } from "@/shared/api/user";

export interface Workspace {
  id: string;
  name: string;
}

interface AuthState {
  isLoggedIn: boolean;
  accessToken: string | null;
  refreshToken: string | null;
  /** 서버에서 조회한 사용자 프로필 */
  profile: UserProfile | null;
  /** 사이드바 표시용 이름 (profile.name 또는 온보딩 임시값) */
  userName: string;
  currentWorkspace: Workspace | null;
  workspaces: Workspace[];
  // actions
  setTokens: (accessToken: string, refreshToken: string) => void;
  setProfile: (profile: UserProfile) => void;
  setUserName: (name: string) => void;
  addWorkspace: (workspace: Workspace) => void;
  setWorkspaces: (workspaces: Workspace[]) => void;
  setCurrentWorkspace: (workspace: Workspace) => void;
  selectWorkspace: (id: string) => void;
  reset: () => void;
}

export const useAuthStore = createPersistedStore<AuthState>("auth", (set) => ({
  isLoggedIn: false,
  accessToken: null,
  refreshToken: null,
  profile: null,
  userName: "",
  currentWorkspace: null,
  workspaces: [],
  setTokens: (accessToken, refreshToken) =>
    set({ accessToken, refreshToken, isLoggedIn: true }),
  setProfile: (profile) =>
    set({ profile, userName: profile.name }),
  setUserName: (name) => set({ userName: name }),
  addWorkspace: (workspace) =>
    set((state) => ({
      workspaces: [...state.workspaces, workspace],
      currentWorkspace: state.currentWorkspace ?? workspace,
    })),
  setWorkspaces: (workspaces) => set({ workspaces }),
  setCurrentWorkspace: (workspace) => set({ currentWorkspace: workspace }),
  selectWorkspace: (id) =>
    set((state) => ({
      currentWorkspace:
        state.workspaces.find((w) => w.id === id) ?? state.currentWorkspace,
    })),
  reset: () =>
    set({
      isLoggedIn: false,
      accessToken: null,
      refreshToken: null,
      profile: null,
      userName: "",
      currentWorkspace: null,
      workspaces: [],
    }),
}));
