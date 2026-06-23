"use client";

import { useRef, useState, type FormEvent } from "react";
import { Plus, Search, Leaf, SprayCan, Trash2, Users, ChefHat } from "lucide-react";
import { TopBar } from "@/shared/ui/top-bar";
import {
  Avatar,
  AvatarFallback,
  AvatarGroup,
  AvatarGroupCount,
  Button,
  Checkbox,
  Chip,
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  Input,
  Label,
  Progress,
  ProgressIndicator,
  ProgressTrack,
} from "@/shared/ui";
import { cn } from "@/shared/utils";

/* ── Mock data (UI 전용 — 비즈니스 로직은 frontend-agent 위임) ── */
type MemberName = "아빠" | "엄마" | "민지";

const MEMBER_CHIP: Record<MemberName, "blue" | "slate" | "green"> = {
  아빠: "blue",
  엄마: "slate",
  민지: "green",
};

const PARTICIPANTS: { initial: string; bg: string }[] = [
  { initial: "엄", bg: "bg-[#d2e1f7] text-[#516072]" },
  { initial: "아", bg: "bg-[#dae3f0] text-[#3e4852]" },
  { initial: "민", bg: "bg-[#d1f5e4] text-[#2e7d5a]" },
];

type ShoppingItem = {
  id: string;
  label: string;
  member: MemberName;
  done: boolean;
};

type Category = {
  id: string;
  name: string;
  icon: typeof Leaf;
  items: ShoppingItem[];
};

const INITIAL_CATEGORIES: Category[] = [
  {
    id: "grocery",
    name: "식료품",
    icon: Leaf,
    items: [
      { id: "g1", label: "유기농 우유 1L", member: "아빠", done: true },
      { id: "g2", label: "아보카도 3구", member: "엄마", done: false },
      { id: "g3", label: "샤인머스캣", member: "아빠", done: false },
    ],
  },
  {
    id: "household",
    name: "생활용품",
    icon: SprayCan,
    items: [
      { id: "h1", label: "친환경 세탁세제", member: "민지", done: false },
      { id: "h2", label: "핸드워시 리필", member: "엄마", done: true },
    ],
  },
];

const WEEKLY_PROGRESS = 75;

const CATEGORY_OPTIONS = [
  { id: "grocery", name: "식료품" },
  { id: "household", name: "생활용품" },
] as const;

const MEMBER_OPTIONS: { name: MemberName; initial: string; bg: string }[] = [
  { name: "아빠", initial: "아", bg: "bg-[#dae3f0] text-[#3e4852]" },
  { name: "엄마", initial: "엄", bg: "bg-[#d2e1f7] text-[#516072]" },
  { name: "민지", initial: "민", bg: "bg-[#d1f5e4] text-[#2e7d5a]" },
];

/* 항목 추가 모달 (UI 전용) */
function AddItemDialog({
  open,
  onOpenChange,
  onAdd,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onAdd: (payload: { name: string; categoryId: string; member: MemberName }) => void;
}) {
  const [name, setName] = useState("");
  const [categoryId, setCategoryId] = useState<string>(CATEGORY_OPTIONS[0].id);
  const [member, setMember] = useState<MemberName | null>(null);

  const canSubmit = name.trim().length > 0 && member !== null;

  const reset = () => {
    setName("");
    setCategoryId(CATEGORY_OPTIONS[0].id);
    setMember(null);
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!canSubmit || member === null) return;
    onAdd({ name: name.trim(), categoryId, member });
    reset();
    onOpenChange(false);
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        if (!next) reset();
        onOpenChange(next);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>항목 추가</DialogTitle>
          <DialogDescription>장바구니에 새 항목을 추가하세요.</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* 항목명 */}
          <div className="space-y-2">
            <Label htmlFor="item-name">항목명</Label>
            <Input
              id="item-name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="예: 유기농 우유 1L"
              autoFocus
            />
          </div>

          {/* 카테고리 */}
          <div className="space-y-2">
            <Label>카테고리</Label>
            <div className="flex flex-wrap gap-2">
              {CATEGORY_OPTIONS.map((c) => (
                <Button
                  key={c.id}
                  type="button"
                  variant={categoryId === c.id ? "default" : "outline"}
                  size="sm"
                  onClick={() => setCategoryId(c.id)}
                >
                  {c.name}
                </Button>
              ))}
            </div>
          </div>

          {/* 담당 멤버 */}
          <div className="space-y-2">
            <Label>담당 멤버</Label>
            <div className="flex flex-wrap gap-2">
              {MEMBER_OPTIONS.map((m) => (
                <Button
                  key={m.name}
                  type="button"
                  variant={member === m.name ? "secondary" : "outline"}
                  size="sm"
                  onClick={() => setMember(m.name)}
                  className="rounded-full gap-1.5"
                >
                  <span
                    className={cn(
                      "size-4 rounded-full flex items-center justify-center text-[10px] font-bold",
                      m.bg
                    )}
                  >
                    {m.initial}
                  </span>
                  {m.name}
                </Button>
              ))}
            </div>
          </div>

          <DialogFooter>
            <DialogClose render={<Button type="button" variant="outline">취소</Button>} />
            <Button type="submit" disabled={!canSubmit}>
              추가하기
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

export default function ShoppingPage() {
  const [categories, setCategories] = useState(INITIAL_CATEGORIES);
  const [addOpen, setAddOpen] = useState(false);
  const nextId = useRef(0);

  const handleAdd = ({
    name,
    categoryId,
    member,
  }: {
    name: string;
    categoryId: string;
    member: MemberName;
  }) => {
    setCategories((prev) =>
      prev.map((category) =>
        category.id === categoryId
          ? {
              ...category,
              items: [
                ...category.items,
                { id: `new-${nextId.current++}`, label: name, member, done: false },
              ],
            }
          : category
      )
    );
  };

  const toggleItem = (categoryId: string, itemId: string) => {
    setCategories((prev) =>
      prev.map((category) =>
        category.id === categoryId
          ? {
              ...category,
              items: category.items.map((item) =>
                item.id === itemId ? { ...item, done: !item.done } : item
              ),
            }
          : category
      )
    );
  };

  return (
    <div className="flex flex-col flex-1 h-full overflow-hidden">
      <TopBar />

      <div className="flex-1 overflow-y-auto">
        <div className="max-w-5xl mx-auto px-4 lg:px-6 py-5 lg:py-6 space-y-6">
          {/* ── Page header ── */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <h2 className="text-2xl font-bold text-foreground tracking-tight">장보기 리스트</h2>
            <div className="flex items-center gap-3">
              <div className="relative hidden sm:block">
                <Search
                  size={16}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground"
                />
                <Input placeholder="항목 검색..." className="pl-9 w-56 rounded-full" />
              </div>
              <Button className="rounded-full" onClick={() => setAddOpen(true)}>
                <Plus size={18} strokeWidth={2.4} />
                항목 추가
              </Button>
            </div>
          </div>

          {/* ── Bento grid ── */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-5">
            {/* Left column — summary */}
            <section className="md:col-span-4 space-y-5">
              {/* Weekly status */}
              <div className="bg-secondary/30 p-5 rounded-xl border border-secondary flex flex-col justify-between gap-4 min-h-48">
                <div>
                  <Chip color="blue" size="sm" className="font-bold">
                    이번 주 현황
                  </Chip>
                  <h3 className="text-2xl font-semibold text-primary mt-3">
                    {WEEKLY_PROGRESS}% 완료됨
                  </h3>
                </div>
                <Progress value={WEEKLY_PROGRESS}>
                  <ProgressTrack className="bg-card/60">
                    <ProgressIndicator />
                  </ProgressTrack>
                </Progress>
                <p className="text-xs text-muted-foreground italic">
                  아빠가 우유 외 2건을 방금 확인했습니다.
                </p>
              </div>

              {/* Participants */}
              <div className="bg-card p-5 rounded-xl border border-border space-y-4">
                <h4 className="text-sm font-bold text-primary flex items-center gap-2">
                  <Users size={18} strokeWidth={1.8} />
                  참여 중인 가족
                </h4>
                <AvatarGroup>
                  {PARTICIPANTS.map((p) => (
                    <Avatar key={p.initial} size="lg">
                      <AvatarFallback className={cn("font-semibold", p.bg)}>
                        {p.initial}
                      </AvatarFallback>
                    </Avatar>
                  ))}
                  <AvatarGroupCount>+2</AvatarGroupCount>
                </AvatarGroup>
              </div>
            </section>

            {/* Right column — lists */}
            <section className="md:col-span-8 space-y-5">
              {categories.map((category) => {
                const Icon = category.icon;
                const doneCount = category.items.filter((i) => i.done).length;

                return (
                  <div
                    key={category.id}
                    className="bg-card rounded-xl border border-border overflow-hidden"
                  >
                    {/* Category header */}
                    <div className="px-5 py-3 bg-muted/40 flex justify-between items-center border-b border-border">
                      <div className="flex items-center gap-2">
                        <Icon size={18} className="text-secondary-foreground" strokeWidth={1.8} />
                        <h3 className="text-sm font-bold text-primary">{category.name}</h3>
                      </div>
                      <span className="text-xs text-muted-foreground tabular-nums">
                        {doneCount}/{category.items.length} 항목
                      </span>
                    </div>

                    {/* Items */}
                    <ul className="divide-y divide-border">
                      {category.items.map((item) => (
                        <li
                          key={item.id}
                          className="flex items-center justify-between px-5 py-3.5 hover:bg-muted/30 transition-colors group"
                        >
                          <label className="flex items-center gap-4 cursor-pointer flex-1 min-w-0">
                            <Checkbox
                              checked={item.done}
                              onCheckedChange={() => toggleItem(category.id, item.id)}
                            />
                            <span
                              className={cn(
                                "text-sm transition-all truncate",
                                item.done
                                  ? "line-through text-muted-foreground"
                                  : "text-foreground"
                              )}
                            >
                              {item.label}
                            </span>
                          </label>
                          <div className="flex items-center gap-2 shrink-0">
                            <Chip color={MEMBER_CHIP[item.member]} size="sm">
                              {item.member}
                            </Chip>
                            <button
                              className="opacity-0 group-hover:opacity-100 p-1 text-muted-foreground hover:text-destructive transition-all"
                              aria-label={`${item.label} 삭제`}
                            >
                              <Trash2 size={16} strokeWidth={1.8} />
                            </button>
                          </div>
                        </li>
                      ))}
                    </ul>
                  </div>
                );
              })}
            </section>
          </div>

          {/* ── Decorative banner ── */}
          {/*<div className="relative w-full rounded-2xl overflow-hidden bg-muted px-6 py-10 flex flex-col items-center justify-center text-center">
            <p className="text-xl font-semibold text-primary/80">
              오늘 저녁은 다 같이 요리해볼까요?
            </p>
            <p className="text-sm text-muted-foreground mt-1">
              가족과 함께 식탁의 즐거움을 나눠보세요.
            </p>
            <Button variant="outline" className="mt-5 rounded-full">
              <ChefHat size={16} strokeWidth={1.8} />
              가족 레시피 보기
            </Button>
          </div>*/}

        </div>
      </div>

      {/* Mobile FAB */}
      <button
        className="md:hidden fixed bottom-20 right-5 w-14 h-14 rounded-full bg-foreground text-background flex items-center justify-center shadow-xl hover:bg-foreground/90 transition-colors z-40"
        aria-label="항목 추가"
        onClick={() => setAddOpen(true)}
      >
        <Plus size={24} strokeWidth={2.5} />
      </button>

      {/* Add item modal */}
      <AddItemDialog open={addOpen} onOpenChange={setAddOpen} onAdd={handleAdd} />
    </div>
  );
}
