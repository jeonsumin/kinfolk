import type {ShoppingCategoryDTO, ShoppingItemDTO} from "./shopping"

/**
 * 장보기 API Mock. UI는 실제 shopping API DTO와 동일한 형태를 사용한다.
 * 백엔드 연결 시 이 facade 호출만 getShoppingList/addShoppingItem/updateShoppingItem/deleteShoppingItem으로 교체한다.
 */
const MEMBER_NAMES: Record<string, string> = {
  dad: "아빠",
  mom: "엄마",
  minji: "민지",
}

const listsByWorkspace = new Map<string, ShoppingCategoryDTO[]>()

function copyItem(item: ShoppingItemDTO): ShoppingItemDTO {
  return {...item}
}

function copyCategories(categories: ShoppingCategoryDTO[]): ShoppingCategoryDTO[] {
  return categories.map((category) => ({
    ...category,
    items: category.items?.map(copyItem) ?? null,
  }))
}

function getList(workspaceId: string) {
  const existing = listsByWorkspace.get(workspaceId)
  if (existing) return existing

  const list: ShoppingCategoryDTO[] = [
    {
      categoryId: "grocery",
      categoryNm: "식료품",
      sortOrder: 1,
      items: [
        {itemId: "g1", wsId: workspaceId, categoryId: "grocery", itemNm: "유기농 우유 1L", quantity: 1, isChecked: true, assignedUserId: "dad", assignedUserName: "아빠"},
        {itemId: "g2", wsId: workspaceId, categoryId: "grocery", itemNm: "아보카도 3구", quantity: 1, isChecked: false, assignedUserId: "mom", assignedUserName: "엄마"},
        {itemId: "g3", wsId: workspaceId, categoryId: "grocery", itemNm: "샤인머스캣", quantity: 1, isChecked: false, assignedUserId: "dad", assignedUserName: "아빠"},
      ],
    },
    {
      categoryId: "household",
      categoryNm: "생활용품",
      sortOrder: 2,
      items: [
        {itemId: "h1", wsId: workspaceId, categoryId: "household", itemNm: "친환경 세탁세제", quantity: 1, isChecked: false, assignedUserId: "minji", assignedUserName: "민지"},
        {itemId: "h2", wsId: workspaceId, categoryId: "household", itemNm: "핸드워시 리필", quantity: 1, isChecked: true, assignedUserId: "mom", assignedUserName: "엄마"},
      ],
    },
  ]
  listsByWorkspace.set(workspaceId, list)
  return list
}

export async function getMockShoppingList(workspaceId: string): Promise<ShoppingCategoryDTO[]> {
  return copyCategories(getList(workspaceId))
}

export async function addMockShoppingItem(
  workspaceId: string,
  payload: {categoryId: string; itemNm: string; assignedUserId: string}
): Promise<ShoppingItemDTO> {
  const category = getList(workspaceId).find((item) => item.categoryId === payload.categoryId)
  if (!category) throw new Error("장보기 카테고리를 찾을 수 없습니다.")

  const item: ShoppingItemDTO = {
    itemId: `shopping-${Date.now()}`,
    wsId: workspaceId,
    categoryId: category.categoryId,
    itemNm: payload.itemNm.trim(),
    quantity: 1,
    isChecked: false,
    assignedUserId: payload.assignedUserId,
    assignedUserName: MEMBER_NAMES[payload.assignedUserId] ?? "담당자 미지정",
  }
  category.items = [...(category.items ?? []), item]
  return copyItem(item)
}

export async function toggleMockShoppingItem(itemId: string): Promise<ShoppingItemDTO> {
  for (const categories of listsByWorkspace.values()) {
    for (const category of categories) {
      const item = category.items?.find((entry) => entry.itemId === itemId)
      if (!item) continue
      item.isChecked = !item.isChecked
      return copyItem(item)
    }
  }
  throw new Error("장보기 항목을 찾을 수 없습니다.")
}

export async function deleteMockShoppingItem(itemId: string): Promise<void> {
  for (const categories of listsByWorkspace.values()) {
    for (const category of categories) {
      if (!category.items?.some((item) => item.itemId === itemId)) continue
      category.items = category.items.filter((item) => item.itemId !== itemId)
      return
    }
  }
  throw new Error("장보기 항목을 찾을 수 없습니다.")
}
