import { create, type StateCreator } from "zustand";
import { devtools, persist } from "zustand/middleware";

export { useStore } from "zustand";
export type { StateCreator, StoreApi, UseBoundStore } from "zustand";

/**
 * devtools가 적용된 store를 생성합니다.
 *
 * @example
 * const useMemosStore = createStore('memos', (set) => ({
 *   memo: '',
 *   setMemo: (text: string) => set({ memo: text }),
 *   memos: [] as string[],
 *   addMemo: (newMemo: string) =>
 *     set((prev) => ({ memos: [...prev.memos, newMemo] })),
 * }));
 *
 * const { memo, setMemo } = useMemosStore();
 */
export function createStore<T>(name: string, initializer: StateCreator<T>) {
  return create<T>()(devtools(initializer, { name }));
}

/**
 * devtools + localStorage persist가 적용된 store를 생성합니다.
 *
 * @example
 * const useAuthStore = createPersistedStore('auth', (set) => ({
 *   token: null as string | null,
 *   setToken: (token: string) => set({ token }),
 *   clear: () => set({ token: null }),
 * }));
 */
export function createPersistedStore<T>(
  name: string,
  initializer: StateCreator<T>
) {
  return create<T>()(devtools(persist(initializer, { name }), { name }));
}
