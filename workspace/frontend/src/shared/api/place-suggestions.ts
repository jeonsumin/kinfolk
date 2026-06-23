/**
 * 장소 제안 API Mock.
 * 실제 연동 시 이 facade를 서버 API 호출로 바꾸면 된다.
 * 링크 메타데이터 추출은 CORS 및 제공처 정책 때문에 서버에서 수행해야 한다.
 */
export interface PlacePreviewDTO {
  sourceUrl: string
  thumbnailUrl: string
  title: string
  description: string
  category: string
}

export interface PlaceSuggestionDTO extends PlacePreviewDTO {
  id: string
  workspaceId: string
  voteCount: number
  votedByMe: boolean
}

export interface CreatePlaceSuggestionRequest extends PlacePreviewDTO {
  workspaceId: string
}

const NAVER_MAP_THUMBNAIL = "https://ssl.pstatic.net/static/maps/assets/images/og-map-400x200.png"
const suggestionsByWorkspace = new Map<string, PlaceSuggestionDTO[]>()

function copySuggestion(suggestion: PlaceSuggestionDTO): PlaceSuggestionDTO {
  return {...suggestion}
}

function getSuggestions(workspaceId: string) {
  const existing = suggestionsByWorkspace.get(workspaceId)
  if (existing) return existing

  const sample: PlaceSuggestionDTO[] = [
    {
      id: "place-sample-1",
      workspaceId,
      sourceUrl: "https://naver.me/5uIYATBA",
      thumbnailUrl: NAVER_MAP_THUMBNAIL,
      title: "공유된 네이버 지도 장소",
      description: "네이버 지도에서 공유된 장소입니다.",
      category: "장소",
      voteCount: 4,
      votedByMe: false,
    },
  ]
  suggestionsByWorkspace.set(workspaceId, sample)
  return sample
}

/** 서버에서 Open Graph 또는 플랫폼별 메타데이터를 추출할 예정인 링크 미리보기 API Mock. */
export async function resolvePlacePreview(sourceUrl: string): Promise<PlacePreviewDTO> {
  return {
    sourceUrl,
    thumbnailUrl: NAVER_MAP_THUMBNAIL,
    title: "공유된 네이버 지도 장소",
    description: "네이버 지도에서 공유된 장소입니다.",
    category: "장소",
  }
}

/** GET /api/v1.0/place-suggestions?workspaceId={workspaceId} */
export async function getPlaceSuggestions(workspaceId: string): Promise<PlaceSuggestionDTO[]> {
  return getSuggestions(workspaceId).map(copySuggestion)
}

/** POST /api/v1.0/place-suggestions */
export async function createPlaceSuggestion(request: CreatePlaceSuggestionRequest): Promise<PlaceSuggestionDTO> {
  const suggestion: PlaceSuggestionDTO = {
    id: `place-${Date.now()}`,
    workspaceId: request.workspaceId,
    sourceUrl: request.sourceUrl,
    thumbnailUrl: request.thumbnailUrl,
    title: request.title.trim(),
    description: request.description.trim(),
    category: request.category.trim(),
    voteCount: 0,
    votedByMe: false,
  }
  getSuggestions(request.workspaceId).unshift(suggestion)
  return copySuggestion(suggestion)
}

/** POST /api/v1.0/place-suggestions/{placeId}/votes */
export async function togglePlaceSuggestionVote(placeId: string): Promise<PlaceSuggestionDTO> {
  for (const suggestions of suggestionsByWorkspace.values()) {
    const suggestion = suggestions.find((item) => item.id === placeId)
    if (!suggestion) continue

    suggestion.votedByMe = !suggestion.votedByMe
    suggestion.voteCount += suggestion.votedByMe ? 1 : -1
    return copySuggestion(suggestion)
  }
  throw new Error("장소 제안을 찾을 수 없습니다.")
}
