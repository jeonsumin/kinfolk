import { apiFetch, type ApiResponse } from "./client";

/** 장보기 아이템 DTO */
export interface ShoppingItemDTO {
  itemId: string;
  wsId: string;
  categoryId: string | null;
  itemNm: string;
  quantity: number;
  isChecked: boolean;
  assignedUserId: string | null;
  assignedUserName: string | null;
}

/** 장보기 카테고리 DTO
 * - 목록 조회: items = ShoppingItemDTO[]
 * - 카테고리만 조회: items = null
 */
export interface ShoppingCategoryDTO {
  categoryId: string;
  categoryNm: string;
  sortOrder: number;
  items: ShoppingItemDTO[] | null;
}

/** POST /shopping/{workspaceId}/items 요청 바디 */
export interface AddShoppingItemPayload {
  categoryId?: string;
  itemNm: string;           // required, max 255자
  quantity?: number;        // default: 1
  assignedUserId?: string;
}

/** PATCH /shopping/items/{itemId} 요청 바디 (모든 필드 optional) */
export interface UpdateShoppingItemPayload {
  itemNm?: string;
  quantity?: number;
  isChecked?: boolean;
  assignedUserId?: string;
  categoryId?: string;
}

const BASE = "/api/v1.0/shopping";

/**
 * GET /api/v1.0/shopping/{workspaceId}
 * 카테고리 + 아이템 목록 조회
 */
export async function getShoppingList(
  workspaceId: string
): Promise<ApiResponse<ShoppingCategoryDTO[]>> {
  return apiFetch<ShoppingCategoryDTO[]>(`${BASE}/${workspaceId}`);
}

/**
 * GET /api/v1.0/shopping/{workspaceId}/categories
 * 카테고리 목록만 조회 (아이템 추가 picker용 lazy-seed)
 */
export async function getShoppingCategories(
  workspaceId: string
): Promise<ApiResponse<ShoppingCategoryDTO[]>> {
  return apiFetch<ShoppingCategoryDTO[]>(`${BASE}/${workspaceId}/categories`);
}

/**
 * POST /api/v1.0/shopping/{workspaceId}/items
 * 아이템 추가 (201 No Content)
 */
export async function addShoppingItem(
  workspaceId: string,
  payload: AddShoppingItemPayload
): Promise<ApiResponse<void>> {
  return apiFetch<void>(`${BASE}/${workspaceId}/items`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

/**
 * PATCH /api/v1.0/shopping/items/{itemId}
 * 아이템 수정 — done 토글, 이름, 담당자, 카테고리 이동 통합 (200 No Content)
 */
export async function updateShoppingItem(
  itemId: string,
  payload: UpdateShoppingItemPayload
): Promise<ApiResponse<void>> {
  return apiFetch<void>(`/api/v1.0/shopping/items/${itemId}`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

/**
 * DELETE /api/v1.0/shopping/items/{itemId}
 * 아이템 삭제 (204 No Content)
 */
export async function deleteShoppingItem(itemId: string): Promise<ApiResponse<void>> {
  return apiFetch<void>(`/api/v1.0/shopping/items/${itemId}`, {
    method: "DELETE",
  });
}
