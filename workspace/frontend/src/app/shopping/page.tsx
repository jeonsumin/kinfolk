"use client";

import {useEffect, useMemo, useState, type FormEvent} from "react";
import {Leaf, Plus, Search, SprayCan, Trash2, Users} from "lucide-react";
import {TopBar} from "@/shared/ui/top-bar";
import {
  Avatar,
  AvatarFallback,
  AvatarGroup,
  AvatarGroupCount,
  Button,
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  Chip,
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  EmptyState,
  Input,
  Label,
  ListGroup,
  Progress,
  ProgressIndicator,
  ProgressTrack,
  SectionHeader,
} from "@/shared/ui";
import {cn} from "@/shared/utils";
import {
  addMockShoppingItem,
  deleteMockShoppingItem,
  getMockShoppingList,
  toggleMockShoppingItem,
  type ShoppingCategoryDTO,
  type ShoppingItemDTO,
} from "@/shared/api";
import {useAuthStore} from "@/stores/auth-store";

const MEMBERS = [
  {id: "dad", name: "아빠", initial: "아", avatar: "bg-[#dae3f0] text-[#3e4852]", chip: "blue" as const},
  {id: "mom", name: "엄마", initial: "엄", avatar: "bg-[#d2e1f7] text-[#516072]", chip: "slate" as const},
  {id: "minji", name: "민지", initial: "민", avatar: "bg-[#d1f5e4] text-[#2e7d5a]", chip: "green" as const},
] as const;

const categoryIcon = (categoryId: string) => categoryId === "grocery" ? Leaf : SprayCan;

const memberStyle = (name: string | null) =>
  MEMBERS.find((member) => member.name === name) ?? MEMBERS[0];

function AddItemDialog({
  open,
  onOpenChange,
  workspaceId,
  categories,
  onCreated,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  workspaceId: string;
  categories: ShoppingCategoryDTO[];
  onCreated: (categoryId: string, item: ShoppingItemDTO) => void;
}) {
  const [itemName, setItemName] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [memberId, setMemberId] = useState<(typeof MEMBERS)[number]["id"]>("dad");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const canSubmit = Boolean(itemName.trim() && categoryId && !isSubmitting);

  const reset = () => {
    setItemName("");
    setCategoryId(categories[0]?.categoryId ?? "");
    setMemberId("dad");
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!canSubmit) return;
    setIsSubmitting(true);
    try {
      const item = await addMockShoppingItem(workspaceId, {
        categoryId,
        itemNm: itemName.trim(),
        assignedUserId: memberId,
      });
      onCreated(categoryId, item);
      reset();
      onOpenChange(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={(next) => {
      if (next && !categoryId) setCategoryId(categories[0]?.categoryId ?? "");
      if (!next) reset();
      onOpenChange(next);
    }}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>항목 추가</DialogTitle>
          <DialogDescription>장보기 목록에 새 항목을 추가하세요.</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="shopping-item">항목명</Label>
            <Input id="shopping-item" value={itemName} onChange={(event) => setItemName(event.target.value)} placeholder="예: 유기농 우유 1L" autoFocus />
          </div>
          <div className="space-y-2">
            <Label>카테고리</Label>
            <div className="flex flex-wrap gap-2">
              {categories.map((category) => (
                <Button key={category.categoryId} type="button" size="sm" variant={categoryId === category.categoryId ? "default" : "outline"} onClick={() => setCategoryId(category.categoryId)}>
                  {category.categoryNm}
                </Button>
              ))}
            </div>
          </div>
          <div className="space-y-2">
            <Label>담당 멤버</Label>
            <div className="flex flex-wrap gap-2">
              {MEMBERS.map((member) => (
                <Button key={member.id} type="button" size="sm" variant={memberId === member.id ? "secondary" : "outline"} onClick={() => setMemberId(member.id)} className="rounded-full gap-1.5">
                  <span className={cn("flex size-4 items-center justify-center rounded-full text-[10px] font-bold", member.avatar)}>{member.initial}</span>
                  {member.name}
                </Button>
              ))}
            </div>
          </div>
          <DialogFooter>
            <DialogClose render={<Button type="button" variant="outline" disabled={isSubmitting}>취소</Button>} />
            <Button type="submit" disabled={!canSubmit}>{isSubmitting ? "추가 중..." : "추가하기"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

export default function ShoppingPage() {
  const {currentWorkspace} = useAuthStore();
  const workspaceId = currentWorkspace?.id ?? "mock-workspace";
  const [categories, setCategories] = useState<ShoppingCategoryDTO[]>([]);
  const [search, setSearch] = useState("");
  const [addOpen, setAddOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      setIsLoading(true);
      try {
        const list = await getMockShoppingList(workspaceId);
        if (!cancelled) setCategories(list);
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [workspaceId]);

  const items = useMemo(() => categories.flatMap((category) => category.items ?? []), [categories]);
  const completedCount = items.filter((item) => item.isChecked).length;
  const progress = items.length ? Math.round(completedCount / items.length * 100) : 0;
  const visibleCategories = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return categories;
    return categories.map((category) => ({
      ...category,
      items: (category.items ?? []).filter((item) => item.itemNm.toLowerCase().includes(query)),
    }));
  }, [categories, search]);

  const updateItem = (updated: ShoppingItemDTO) => {
    setCategories((current) => current.map((category) => ({
      ...category,
      items: category.items?.map((item) => item.itemId === updated.itemId ? updated : item) ?? null,
    })));
  };

  const handleToggle = async (itemId: string) => updateItem(await toggleMockShoppingItem(itemId));

  const handleDelete = async (itemId: string) => {
    await deleteMockShoppingItem(itemId);
    setCategories((current) => current.map((category) => ({
      ...category,
      items: category.items?.filter((item) => item.itemId !== itemId) ?? null,
    })));
  };

  return (
    <div className="flex h-full flex-1 flex-col overflow-hidden">
      <TopBar />
      <main className="flex-1 overflow-y-auto">
        <div className="mx-auto max-w-5xl space-y-6 px-4 py-5 lg:px-6 lg:py-6">
          <SectionHeader
            title="장보기 리스트"
            action={<div className="flex items-center gap-3">
              <div className="relative hidden sm:block">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                <Input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="항목 검색..." className="w-56 rounded-full pl-9" />
              </div>
              <Button className="rounded-full" onClick={() => setAddOpen(true)}><Plus size={18} /> 항목 추가</Button>
            </div>}
            className="items-center [&_h2]:text-2xl [&_h2]:font-bold [&_h2]:tracking-tight"
          />

          <div className="grid grid-cols-1 gap-5 md:grid-cols-12">
            <aside className="space-y-5 md:col-span-4">
              <Card className="border-secondary bg-secondary/30">
                <CardContent className="space-y-4 pt-5">
                  <Chip color="blue" size="sm" className="font-bold">이번 주 현황</Chip>
                  <h3 className="text-2xl font-semibold text-primary">{progress}% 완료됨</h3>
                  <Progress value={progress}><ProgressTrack className="bg-card/60"><ProgressIndicator /></ProgressTrack></Progress>
                  <p className="text-xs text-muted-foreground">전체 {items.length}개 중 {completedCount}개를 확인했습니다.</p>
                </CardContent>
              </Card>
              <Card>
                <CardHeader className="pb-0"><h3 className="flex items-center gap-2 text-sm font-bold text-primary"><Users size={18} /> 참여 중인 가족</h3></CardHeader>
                <CardContent className="pt-4">
                  <AvatarGroup>
                    {MEMBERS.map((member) => <Avatar key={member.id} size="lg"><AvatarFallback className={cn("font-semibold", member.avatar)}>{member.initial}</AvatarFallback></Avatar>)}
                    <AvatarGroupCount>+2</AvatarGroupCount>
                  </AvatarGroup>
                </CardContent>
              </Card>
            </aside>

            <section className="space-y-5 md:col-span-8">
              {isLoading && <p className="py-12 text-center text-sm text-muted-foreground">장보기 목록을 불러오는 중...</p>}
              {!isLoading && visibleCategories.map((category) => {
                const Icon = categoryIcon(category.categoryId);
                const categoryItems = category.items ?? [];
                const doneCount = categoryItems.filter((item) => item.isChecked).length;
                return (
                  <Card key={category.categoryId} className="gap-0 py-0">
                    <CardHeader className="border-b bg-muted/40 py-3">
                      <div className="flex items-center justify-between">
                        <h3 className="flex items-center gap-2 text-sm font-bold text-primary"><Icon size={18} className="text-secondary-foreground" /> {category.categoryNm}</h3>
                        <span className="text-xs text-muted-foreground tabular-nums">{doneCount}/{categoryItems.length} 항목</span>
                      </div>
                    </CardHeader>
                    <CardContent className="px-0">
                      {categoryItems.length === 0 ? (
                        <EmptyState title="표시할 항목이 없습니다." className="py-7" />
                      ) : (
                        <ListGroup className="rounded-none border-0">
                          {categoryItems.map((item) => {
                            const member = memberStyle(item.assignedUserName);
                            return (
                              <div key={item.itemId} className="group flex items-center justify-between gap-3 px-4 py-3 transition-colors hover:bg-muted/30">
                                <label className="flex min-w-0 flex-1 cursor-pointer items-center gap-4">
                                  <Checkbox checked={item.isChecked} onCheckedChange={() => handleToggle(item.itemId)} />
                                  <span className={cn("truncate text-sm", item.isChecked ? "text-muted-foreground line-through" : "text-foreground")}>{item.itemNm}</span>
                                </label>
                                <div className="flex shrink-0 items-center gap-2">
                                  <Chip color={member.chip} size="sm">{item.assignedUserName ?? "미지정"}</Chip>
                                  <Button variant="ghost" size="icon-xs" className="opacity-0 text-muted-foreground group-hover:opacity-100 hover:text-destructive" onClick={() => handleDelete(item.itemId)} aria-label={`${item.itemNm} 삭제`}><Trash2 size={16} /></Button>
                                </div>
                              </div>
                            );
                          })}
                        </ListGroup>
                      )}
                    </CardContent>
                  </Card>
                );
              })}
            </section>
          </div>
        </div>
      </main>

      <Button size="icon-lg" className="fixed bottom-20 right-5 z-40 rounded-full md:hidden" onClick={() => setAddOpen(true)} aria-label="항목 추가"><Plus size={24} /></Button>
      <AddItemDialog
        open={addOpen}
        onOpenChange={setAddOpen}
        workspaceId={workspaceId}
        categories={categories}
        onCreated={(categoryId, item) => setCategories((current) => current.map((category) => category.categoryId === categoryId ? {...category, items: [...(category.items ?? []), item]} : category))}
      />
    </div>
  );
}
