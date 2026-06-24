import { apiFetch } from "./client";

export interface PlacePreviewDTO {
  sourceUrl: string;
  thumbnailUrl: string;
  title: string;
  description: string;
  category: string;
}

export interface PlaceSuggestionDTO extends PlacePreviewDTO {
  id: string;
  workspaceId: string;
  voteCount: number;
  votedByMe: boolean;
}

export interface CreatePlaceSuggestionRequest extends PlacePreviewDTO {
  workspaceId: string;
}

const BASE = "/api/v1.0/place-suggestions";

export async function resolvePlacePreview(sourceUrl: string): Promise<PlacePreviewDTO> {
  const res = await apiFetch<PlacePreviewDTO>(`${BASE}/preview?url=${encodeURIComponent(sourceUrl)}`);
  return res.data;
}

export async function getPlaceSuggestions(workspaceId: string): Promise<PlaceSuggestionDTO[]> {
  const res = await apiFetch<PlaceSuggestionDTO[]>(`${BASE}?workspaceId=${encodeURIComponent(workspaceId)}`);
  return res.data ?? [];
}

export async function createPlaceSuggestion(request: CreatePlaceSuggestionRequest): Promise<PlaceSuggestionDTO> {
  const res = await apiFetch<PlaceSuggestionDTO>(BASE, {
    method: "POST",
    body: JSON.stringify(request),
  });
  return res.data;
}

export async function togglePlaceSuggestionVote(placeId: string): Promise<PlaceSuggestionDTO> {
  const res = await apiFetch<PlaceSuggestionDTO>(`${BASE}/${placeId}/votes`, {
    method: "POST",
  });
  return res.data;
}
