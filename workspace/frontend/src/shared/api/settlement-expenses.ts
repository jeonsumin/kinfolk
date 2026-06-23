/**
 * 정산 내역 API Mock.
 * 실제 백엔드 연결 시 이 facade의 함수와 DTO 계약을 그대로 사용한다.
 */
export type SettlementStatus = "IN_PROGRESS" | "PENDING" | "SETTLED"

export interface SettlementExpenseDTO {
  id: string
  workspaceId: string
  date: string // YYYY-MM-DD, 서버 등록일
  item: string
  payer: string
  amount: number
  status: SettlementStatus
}

export interface CreateSettlementExpenseRequest {
  workspaceId: string
  item: string
  payer: string
  amount: number
  status: SettlementStatus
}

const expensesByWorkspace = new Map<string, SettlementExpenseDTO[]>()

function copyExpense(expense: SettlementExpenseDTO): SettlementExpenseDTO {
  return {...expense}
}

function getExpenses(workspaceId: string) {
  const existing = expensesByWorkspace.get(workspaceId)
  if (existing) return existing

  const sample: SettlementExpenseDTO[] = [
   ]
  expensesByWorkspace.set(workspaceId, sample)
  return sample
}

/** GET /api/v1.0/settlement-expenses?workspaceId={workspaceId} */
export async function getSettlementExpenses(workspaceId: string): Promise<SettlementExpenseDTO[]> {
  return getExpenses(workspaceId).map(copyExpense)
}

/** POST /api/v1.0/settlement-expenses */
export async function createSettlementExpense(request: CreateSettlementExpenseRequest): Promise<SettlementExpenseDTO> {
  const expense: SettlementExpenseDTO = {
    id: `expense-${Date.now()}`,
    workspaceId: request.workspaceId,
    date: new Date().toISOString().slice(0, 10),
    item: request.item.trim(),
    payer: request.payer.trim(),
    amount: request.amount,
    status: request.status,
  }
  getExpenses(request.workspaceId).unshift(expense)
  return copyExpense(expense)
}
