import { apiFetch } from "./client";

export type SettlementStatus = "IN_PROGRESS" | "PENDING" | "SETTLED";

export interface SettlementExpenseDTO {
  id: string;
  workspaceId: string;
  date: string;
  item: string;
  payer: string;
  amount: number;
  status: SettlementStatus;
}

export interface CreateSettlementExpenseRequest {
  workspaceId: string;
  plannerId: string;
  item: string;
  payer: string;
  amount: number;
  status: SettlementStatus;
}

const BASE = "/api/v1.0/settlement-expenses";

export async function getSettlementExpenses(workspaceId: string, plannerId: string): Promise<SettlementExpenseDTO[]> {
  const res = await apiFetch<SettlementExpenseDTO[]>(`${BASE}?workspaceId=${encodeURIComponent(workspaceId)}&plannerId=${encodeURIComponent(plannerId)}`);
  return res.data ?? [];
}

export async function createSettlementExpense(request: CreateSettlementExpenseRequest): Promise<SettlementExpenseDTO> {
  const res = await apiFetch<SettlementExpenseDTO>(BASE, {
    method: "POST",
    body: JSON.stringify(request),
  });
  return res.data;
}
