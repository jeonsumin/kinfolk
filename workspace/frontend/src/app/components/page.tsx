import {
  Avatar,
  AvatarFallback,
  AvatarGroup,
  AvatarGroupCount,
  AvatarImage,
  Badge,
  Button,
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
  Chip,
  EmptyState,
  ImageCard,
  ImageOverlayCard,
  Input,
  Label,
  ListGroup,
  ListItem,
  MonthCalendar,
  Progress,
  ProgressLabel,
  ProgressValue,
  SectionHeader,
  Separator,
  Skeleton,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
  Textarea,
} from "@/shared/ui"
import { EmblaCarouselDemo, SwiperCarouselDemo } from "./carousel-demo"
import {
  Bell,
  Calendar,
  ChevronRight,
  Heart,
  Home,
  Images,
  Inbox,
  Plus,
  Settings,
  Star,
  Users,
} from "lucide-react"

// ────────────────────────────────────────────────────────────
// Helpers
// ────────────────────────────────────────────────────────────

function ShowcaseSection({
  id,
  title,
  children,
}: {
  id: string
  title: string
  children: React.ReactNode
}) {
  return (
    <section id={id} className="space-y-4">
      <h2 className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">
        {title}
      </h2>
      {children}
    </section>
  )
}

function DemoCard({
  label,
  children,
  className,
}: {
  label?: string
  children: React.ReactNode
  className?: string
}) {
  return (
    <div className={`space-y-2 ${className ?? ""}`}>
      {label && (
        <p className="text-[11px] font-medium text-muted-foreground/70 uppercase tracking-wider">
          {label}
        </p>
      )}
      <div className="rounded-xl border border-border bg-card p-5">{children}</div>
    </div>
  )
}

// ────────────────────────────────────────────────────────────
// Page
// ────────────────────────────────────────────────────────────

export default function ComponentsPage() {
  return (
    <div className="flex flex-col flex-1 h-full overflow-hidden">
      <div className="flex-1 overflow-y-auto">
        <div className="max-w-4xl mx-auto px-4 lg:px-8 py-10 space-y-14">

          {/* ── Page Header ────────────────────────────────── */}
          <div className="space-y-1">
            <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
              Kinship &amp; Co
            </p>
            <h1 className="text-3xl font-bold tracking-tight text-foreground">
              Component Library
            </h1>
            <p className="text-sm text-muted-foreground max-w-lg leading-relaxed">
              디자인 시스템 기반의 공통 컴포넌트 모음입니다. Calm Sophistication 테마 — Muted Slate 팔레트, Plus Jakarta Sans 타이포그래피.
            </p>
          </div>

          {/* ── Colors ─────────────────────────────────────── */}
          <ShowcaseSection id="colors" title="Color Palette">
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
              {[
                { name: "Primary", value: "#475569", text: "white" },
                { name: "Primary Dark", value: "#303e51", text: "white" },
                { name: "Secondary", value: "#d2e1f7", text: "#475569" },
                { name: "Muted", value: "#eceef0", text: "#44474c" },
                { name: "Background", value: "#f7f9fb", text: "#191c1e", border: true },
                { name: "Card", value: "#ffffff", text: "#191c1e", border: true },
                { name: "Border", value: "#c4c6cd", text: "#44474c" },
                { name: "Foreground", value: "#191c1e", text: "white" },
                { name: "Muted FG", value: "#44474c", text: "white" },
                { name: "Destructive", value: "#ba1a1a", text: "white" },
                { name: "Chip Blue", value: "#d2e1f7", text: "#516072" },
                { name: "Chip Green", value: "#d1f5e4", text: "#2e7d5a" },
                { name: "Chip Mauve", value: "#ead6f0", text: "#7c4d8a" },
                { name: "Chip Amber", value: "#fdf0d0", text: "#8a6800" },
              ].map((c) => (
                <div
                  key={c.name}
                  className={`rounded-lg p-3 ${c.border ? "border border-border" : ""}`}
                  style={{ backgroundColor: c.value }}
                >
                  <p className="text-[11px] font-semibold" style={{ color: c.text }}>
                    {c.name}
                  </p>
                  <p className="text-[10px] font-mono opacity-70 mt-0.5" style={{ color: c.text }}>
                    {c.value}
                  </p>
                </div>
              ))}
            </div>
          </ShowcaseSection>

          {/* ── Typography ─────────────────────────────────── */}
          <ShowcaseSection id="typography" title="Typography">
            {/* Type Scale */}
            <DemoCard label="Type Scale">
              <div className="space-y-4">
                {[
                  { meta: "headline-xl · 40px / 700", className: "text-[40px] font-bold leading-[48px] tracking-[-0.02em]", text: "가족의 일상" },
                  { meta: "headline-lg · 32px / 600", className: "text-[32px] font-semibold leading-[40px] tracking-[-0.01em]", text: "오늘의 일정" },
                  { meta: "headline-md · 24px / 600", className: "text-2xl font-semibold leading-8", text: "이번 주 공유 사진" },
                  { meta: "body-lg · 18px / 400", className: "text-lg leading-7", text: "Kinfolk Table은 가족의 소중한 순간을 담는 공간입니다." },
                  { meta: "body-md · 16px / 400", className: "text-base leading-6", text: "일정, 사진, 구매 목록을 한 곳에서 관리하세요." },
                  { meta: "body-sm · 14px / 400", className: "text-sm leading-5 text-muted-foreground", text: "가족 구성원 5명이 함께 사용 중입니다." },
                  { meta: "caption · 12px / 500", className: "text-xs font-medium tracking-wide text-muted-foreground uppercase", text: "FAMILY SECTION" },
                ].map(({ meta, className, text }, i, arr) => (
                  <div key={meta}>
                    <p className="text-[10px] font-mono text-muted-foreground/60 mb-1">{meta}</p>
                    <p className={className}>{text}</p>
                    {i < arr.length - 1 && <Separator className="mt-4" />}
                  </div>
                ))}
              </div>
            </DemoCard>

            {/* Label — Variants */}
            <DemoCard label="Label — Variant">
              <div className="space-y-3">
                {(["default", "muted", "destructive", "required"] as const).map((variant) => (
                  <div key={variant} className="flex items-center gap-4">
                    <span className="text-[10px] font-mono text-muted-foreground/60 w-24 shrink-0">{variant}</span>
                    <Label variant={variant}>가족 일정 제목</Label>
                  </div>
                ))}
              </div>
            </DemoCard>

            {/* Label — Sizes */}
            <DemoCard label="Label — Size">
              <div className="space-y-3">
                {(["sm", "default", "lg"] as const).map((size) => (
                  <div key={size} className="flex items-center gap-4">
                    <span className="text-[10px] font-mono text-muted-foreground/60 w-24 shrink-0">{size}</span>
                    <Label size={size}>가족 일정 제목</Label>
                  </div>
                ))}
              </div>
            </DemoCard>

            {/* Label — 실제 활용: 폼 필드 */}
            <DemoCard label="Label — 활용 예시 (Form Field)">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                <div className="space-y-1.5">
                  <Label>이름</Label>
                  <Input placeholder="홍길동" />
                </div>
                <div className="space-y-1.5">
                  <Label variant="required">이메일</Label>
                  <Input placeholder="example@email.com" />
                </div>
                <div className="space-y-1.5">
                  <Label variant="muted">메모 (선택)</Label>
                  <Input placeholder="추가 정보를 입력하세요" />
                </div>
                <div className="space-y-1.5">
                  <Label variant="destructive">비밀번호</Label>
                  <Input placeholder="비밀번호" aria-invalid defaultValue="1234" />
                  <p className="text-xs text-destructive">8자 이상 입력해주세요.</p>
                </div>
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Buttons ────────────────────────────────────── */}
          <ShowcaseSection id="buttons" title="Buttons">
            <DemoCard label="Variants">
              <div className="flex flex-wrap gap-2">
                <Button variant="default">Default</Button>
                <Button variant="outline">Outline</Button>
                <Button variant="secondary">Secondary</Button>
                <Button variant="ghost">Ghost</Button>
                <Button variant="destructive">Destructive</Button>
                <Button variant="link">Link</Button>
              </div>
            </DemoCard>
            <DemoCard label="Sizes">
              <div className="flex flex-wrap items-center gap-2">
                <Button size="xs">Extra Small</Button>
                <Button size="sm">Small</Button>
                <Button size="default">Default</Button>
                <Button size="lg">Large</Button>
              </div>
            </DemoCard>
            <DemoCard label="With Icons">
              <div className="flex flex-wrap items-center gap-2">
                <Button variant="default">
                  <Plus />
                  일정 추가
                </Button>
                <Button variant="outline">
                  <Bell />
                  알림
                </Button>
                <Button size="icon" variant="ghost">
                  <Settings />
                </Button>
                <Button size="icon-sm" variant="outline">
                  <Plus />
                </Button>
              </div>
            </DemoCard>
            <DemoCard label="Disabled">
              <div className="flex flex-wrap items-center gap-2">
                <Button disabled>Disabled</Button>
                <Button variant="outline" disabled>Disabled</Button>
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Badges & Chips ──────────────────────────────── */}
          <ShowcaseSection id="badges" title="Badges &amp; Chips">
            <DemoCard label="Badge Variants">
              <div className="flex flex-wrap gap-2">
                <Badge variant="default">Default</Badge>
                <Badge variant="secondary">Secondary</Badge>
                <Badge variant="destructive">Destructive</Badge>
                <Badge variant="outline">Outline</Badge>
              </div>
            </DemoCard>
            <DemoCard label="Chip Colors">
              <div className="flex flex-wrap gap-2">
                <Chip color="default">기본</Chip>
                <Chip color="slate">일정</Chip>
                <Chip color="blue">교육</Chip>
                <Chip color="green">건강</Chip>
                <Chip color="mauve">가족</Chip>
                <Chip color="amber">알림</Chip>
              </div>
            </DemoCard>
            <DemoCard label="Chip Sizes">
              <div className="flex flex-wrap items-center gap-2">
                <Chip size="sm" color="blue">Small</Chip>
                <Chip size="md" color="green">Medium</Chip>
                <Chip size="lg" color="mauve">Large</Chip>
              </div>
            </DemoCard>
            <DemoCard label="Chip with Icon">
              <div className="flex flex-wrap gap-2">
                <Chip color="blue" icon={<Calendar size={12} />}>일정</Chip>
                <Chip color="green" icon={<Heart size={12} />}>건강</Chip>
                <Chip color="mauve" icon={<Star size={12} />}>즐겨찾기</Chip>
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Cards ──────────────────────────────────────── */}
          <ShowcaseSection id="cards" title="Cards">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <DemoCard label="Default Card">
                <Card>
                  <CardHeader>
                    <CardTitle>오늘의 일정</CardTitle>
                    <CardDescription>가족 일정을 확인하세요</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <p className="text-sm text-muted-foreground">일정 내용이 여기에 표시됩니다.</p>
                  </CardContent>
                  <CardFooter>
                    <Button variant="ghost" size="sm">
                      자세히 보기
                      <ChevronRight />
                    </Button>
                  </CardFooter>
                </Card>
              </DemoCard>

              <DemoCard label="Image Card">
                <Card className="pt-0 overflow-hidden">
                  {/* 이미지 플레이스홀더 — 실제 사용 시 <img> 태그로 교체 */}
                  <div className="h-36 bg-gradient-to-br from-[#b9c7df] to-[#475569] flex items-end px-4 pb-3">
                    <div className="flex items-center gap-2">
                      <Avatar size="sm">
                        <AvatarFallback>이주</AvatarFallback>
                      </Avatar>
                      <span className="text-xs font-medium text-white">이주 · 오늘</span>
                    </div>
                  </div>
                  <CardHeader>
                    <CardTitle>제주도 여행 🌊</CardTitle>
                    <CardDescription>가족 모두가 함께한 소중한 여름 추억</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="flex gap-1.5 flex-wrap">
                      <Chip color="blue" size="sm">여행</Chip>
                      <Chip color="green" size="sm">가족</Chip>
                    </div>
                  </CardContent>
                </Card>
              </DemoCard>

              <DemoCard label="Small Card">
                <Card size="sm">
                  <CardHeader>
                    <CardTitle>쇼핑 리스트</CardTitle>
                    <CardDescription>3/5 완료</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="h-1.5 rounded-full bg-muted mb-3 overflow-hidden">
                      <div className="h-full w-3/5 rounded-full bg-primary" />
                    </div>
                    <p className="text-sm text-muted-foreground">달걀, 우유, 세제가 남았습니다.</p>
                  </CardContent>
                </Card>
              </DemoCard>
            </div>

            {/* Image-only cards */}
            <DemoCard label="Image Card (이미지 전용)">
              <div className="grid grid-cols-3 gap-3">
                <ImageCard aspectRatio="aspect-[4/3]">
                  <div className="h-full w-full bg-gradient-to-br from-[#b9c7df] to-[#475569]" />
                </ImageCard>
                <ImageCard aspectRatio="aspect-[4/3]">
                  <div className="h-full w-full bg-gradient-to-br from-[#d1f5e4] to-[#2e7d5a]" />
                </ImageCard>
                <ImageCard aspectRatio="aspect-[4/3]">
                  <div className="h-full w-full bg-gradient-to-br from-[#ead6f0] to-[#7c4d8a]" />
                </ImageCard>
              </div>
            </DemoCard>

            {/* Image overlay cards */}
            <DemoCard label="Image Overlay Card (하단 타이틀 오버레이)">
              <div className="grid grid-cols-3 gap-3">
                <ImageOverlayCard title="제주도 여름 여행" subtitle="2025.07.12 · 가족 5명">
                  <div className="h-full w-full bg-gradient-to-br from-[#b9c7df] to-[#475569]" />
                </ImageOverlayCard>
                <ImageOverlayCard title="할머니 생신 파티" subtitle="2025.06.01 · 이주 업로드">
                  <div className="h-full w-full bg-gradient-to-br from-[#d1f5e4] to-[#2e7d5a]" />
                </ImageOverlayCard>
                <ImageOverlayCard title="공원 피크닉" subtitle="2025.05.18 · 박선">
                  <div className="h-full w-full bg-gradient-to-br from-[#ead6f0] to-[#7c4d8a]" />
                </ImageOverlayCard>
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Form Elements ───────────────────────────────── */}
          <ShowcaseSection id="forms" title="Form Elements">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <DemoCard label="Input States">
                <div className="space-y-4">
                  <div className="space-y-1.5">
                    <Label htmlFor="name-default">이름</Label>
                    <Input id="name-default" placeholder="홍길동" />
                  </div>
                  <div className="space-y-1.5">
                    <Label htmlFor="name-error">이메일</Label>
                    <Input
                      id="name-error"
                      placeholder="example@email.com"
                      aria-invalid
                      defaultValue="잘못된@"
                    />
                    <p className="text-xs text-destructive">올바른 이메일 형식이 아닙니다.</p>
                  </div>
                  <div className="space-y-1.5">
                    <Label htmlFor="name-disabled">비밀번호</Label>
                    <Input id="name-disabled" placeholder="비밀번호" disabled />
                    <p className="text-xs text-muted-foreground">8자 이상 입력해주세요.</p>
                  </div>
                </div>
              </DemoCard>
              <DemoCard label="Textarea">
                <div className="space-y-1.5">
                  <Label htmlFor="message">메모</Label>
                  <Textarea
                    id="message"
                    placeholder="가족에게 남길 메시지를 입력하세요..."
                    rows={5}
                  />
                  <p className="text-xs text-muted-foreground">최대 500자</p>
                </div>
              </DemoCard>
            </div>
          </ShowcaseSection>

          {/* ── Avatars ─────────────────────────────────────── */}
          <ShowcaseSection id="avatars" title="Avatars">
            <DemoCard label="Sizes">
              <div className="flex items-end gap-3">
                <Avatar size="sm">
                  <AvatarFallback>이주</AvatarFallback>
                </Avatar>
                <Avatar>
                  <AvatarFallback>김민</AvatarFallback>
                </Avatar>
                <Avatar size="lg">
                  <AvatarFallback>박선</AvatarFallback>
                </Avatar>
              </div>
            </DemoCard>
            <DemoCard label="With Image &amp; Fallback">
              <div className="flex items-center gap-3">
                <Avatar>
                  <AvatarImage
                    src="https://i.pravatar.cc/40?img=1"
                    alt="이주"
                  />
                  <AvatarFallback>이주</AvatarFallback>
                </Avatar>
                <Avatar>
                  <AvatarImage src="/broken-image.jpg" alt="깨진 이미지" />
                  <AvatarFallback>fallback</AvatarFallback>
                </Avatar>
                <Avatar>
                  <AvatarFallback>KF</AvatarFallback>
                </Avatar>
              </div>
            </DemoCard>
            <DemoCard label="Avatar Group">
              <AvatarGroup>
                <Avatar>
                  <AvatarFallback>이주</AvatarFallback>
                </Avatar>
                <Avatar>
                  <AvatarFallback>김민</AvatarFallback>
                </Avatar>
                <Avatar>
                  <AvatarFallback>박선</AvatarFallback>
                </Avatar>
                <Avatar>
                  <AvatarFallback>최연</AvatarFallback>
                </Avatar>
                <AvatarGroupCount>+3</AvatarGroupCount>
              </AvatarGroup>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Lists ───────────────────────────────────────── */}
          <ShowcaseSection id="lists" title="List Items">
            <DemoCard label="List Group">
              <ListGroup>
                <ListItem
                  leading={<Home size={16} />}
                  title="대시보드"
                  subtitle="오늘의 가족 현황"
                  trailing={<ChevronRight size={16} />}
                />
                <ListItem
                  leading={<Users size={16} />}
                  title="멤버"
                  subtitle="5명의 가족 구성원"
                  trailing={<Badge variant="secondary">5</Badge>}
                />
                <ListItem
                  leading={<Calendar size={16} />}
                  title="일정"
                  subtitle="이번 주 3개 일정"
                  trailing={<Chip color="blue" size="sm">3</Chip>}
                />
                <ListItem
                  leading={<Images size={16} />}
                  title="사진첩"
                  subtitle="최근 업로드 12장"
                  trailing={<ChevronRight size={16} />}
                />
                <ListItem
                  leading={<Settings size={16} />}
                  title="설정"
                  trailing={<ChevronRight size={16} />}
                />
              </ListGroup>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Section Headers ──────────────────────────────── */}
          <ShowcaseSection id="section-headers" title="Section Headers">
            <div className="space-y-4">
              <DemoCard label="With Subtitle &amp; Action">
                <SectionHeader
                  title="오늘의 일정"
                  subtitle="3개의 일정이 예정되어 있습니다"
                  action={
                    <Button size="sm" variant="outline">
                      <Plus />
                      추가
                    </Button>
                  }
                />
              </DemoCard>
              <DemoCard label="Title Only">
                <SectionHeader title="가족 사진 공유" />
              </DemoCard>
              <DemoCard label="With Badge Action">
                <SectionHeader
                  title="알림"
                  subtitle="읽지 않은 알림"
                  action={<Badge variant="destructive">5</Badge>}
                />
              </DemoCard>
            </div>
          </ShowcaseSection>

          {/* ── Empty States ─────────────────────────────────── */}
          <ShowcaseSection id="empty-states" title="Empty States">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <DemoCard label="With Icon &amp; Action">
                <EmptyState
                  icon={<Inbox size={20} />}
                  title="일정이 없습니다"
                  description="새로운 일정을 추가해 가족과 공유해보세요."
                  action={
                    <Button size="sm">
                      <Plus />
                      일정 추가
                    </Button>
                  }
                />
              </DemoCard>
              <DemoCard label="Minimal">
                <EmptyState
                  icon={<Images size={20} />}
                  title="사진이 없습니다"
                  description="소중한 순간을 사진으로 남겨보세요."
                />
              </DemoCard>
            </div>
          </ShowcaseSection>

          {/* ── Separators ──────────────────────────────────── */}
          <ShowcaseSection id="separators" title="Separators">
            <DemoCard label="Horizontal">
              <div className="space-y-3">
                <p className="text-sm text-foreground">위 섹션</p>
                <Separator />
                <p className="text-sm text-muted-foreground">아래 섹션</p>
              </div>
            </DemoCard>
            <DemoCard label="Vertical">
              <div className="flex items-center gap-4 h-8">
                <p className="text-sm text-foreground">항목 A</p>
                <Separator orientation="vertical" />
                <p className="text-sm text-foreground">항목 B</p>
                <Separator orientation="vertical" />
                <p className="text-sm text-foreground">항목 C</p>
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Switch ──────────────────────────────────────── */}
          <ShowcaseSection id="switch" title="Switch">
            <DemoCard label="States">
              <div className="flex flex-wrap items-center gap-6">
                {[
                  { id: "sw-on", label: "켜짐", checked: true, disabled: false },
                  { id: "sw-off", label: "꺼짐", checked: false, disabled: false },
                  { id: "sw-dis-on", label: "비활성(켜짐)", checked: true, disabled: true },
                  { id: "sw-dis-off", label: "비활성(꺼짐)", checked: false, disabled: true },
                ].map(({ id, label, checked, disabled }) => (
                  <div key={id} className="flex items-center gap-2">
                    <Switch id={id} defaultChecked={checked} disabled={disabled} />
                    <Label htmlFor={id} className={`text-sm ${disabled ? "text-muted-foreground" : ""}`}>
                      {label}
                    </Label>
                  </div>
                ))}
              </div>
            </DemoCard>
            <DemoCard label="Size">
              <div className="flex items-center gap-6">
                <div className="flex items-center gap-2">
                  <Switch size="sm" defaultChecked />
                  <span className="text-sm text-muted-foreground">Small</span>
                </div>
                <div className="flex items-center gap-2">
                  <Switch defaultChecked />
                  <span className="text-sm text-muted-foreground">Default</span>
                </div>
              </div>
            </DemoCard>
            {/* 가족 보기 패널 — calendar.png */}
            <DemoCard label="가족 보기 (활용 예시)">
              <Card size="sm">
                <CardHeader>
                  <CardTitle>가족 보기</CardTitle>
                </CardHeader>
                <CardContent className="space-y-0.5 pb-2">
                  {[
                    { name: "엄마", role: "일정 3개", color: "bg-[#b9c7df]", checked: true },
                    { name: "아빠", role: "일정 2개", color: "bg-[#c4d4ec]", checked: true },
                    { name: "아이들", role: "일정 4개", color: "bg-[#d4e4fa]", checked: false },
                  ].map((member) => (
                    <div key={member.name} className="flex items-center gap-3 px-1 py-2.5 rounded-lg hover:bg-muted/40 transition-colors">
                      <div className={`size-9 rounded-full ${member.color} flex items-center justify-center shrink-0`}>
                        <span className="text-xs font-semibold text-foreground/70">{member.name[0]}</span>
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium">{member.name}</p>
                        <p className="text-xs text-muted-foreground">{member.role}</p>
                      </div>
                      <Switch defaultChecked={member.checked} />
                    </div>
                  ))}
                </CardContent>
              </Card>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Progress ─────────────────────────────────────── */}
          <ShowcaseSection id="progress" title="Progress">
            <DemoCard label="Values">
              <div className="space-y-3">
                {[25, 50, 75, 100].map((value) => (
                  <div key={value}>
                    <p className="text-xs text-muted-foreground mb-1.5">{value}%</p>
                    <Progress value={value} />
                  </div>
                ))}
              </div>
            </DemoCard>
            {/* 집안일 패널 — calendar.png */}
            <DemoCard label="이달의 집안일 (활용 예시)">
              <div className="space-y-3.5">
                {[
                  { task: "정원 청소", value: 80 },
                  { task: "다락방 정리", value: 30 },
                  { task: "냉장고 정리", value: 55 },
                  { task: "창문 청소", value: 10 },
                ].map(({ task, value }) => (
                  <div key={task}>
                    <div className="flex items-center justify-between mb-1.5">
                      <span className="text-sm text-foreground">{task}</span>
                      <span className="text-xs font-medium text-muted-foreground tabular-nums">{value}%</span>
                    </div>
                    <Progress value={value} />
                  </div>
                ))}
              </div>
            </DemoCard>
            {/* ProgressLabel + ProgressValue 조합 */}
            <DemoCard label="With Label &amp; Value">
              <div className="space-y-2">
                <Progress value={68}>
                  <ProgressLabel>가족 앨범 정리</ProgressLabel>
                  <ProgressValue />
                </Progress>
                <Progress value={42}>
                  <ProgressLabel>공동 일정 등록</ProgressLabel>
                  <ProgressValue />
                </Progress>
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Table ────────────────────────────────────────── */}
          <ShowcaseSection id="table" title="Table">
            <DemoCard label="정산 내역 (활용 예시)">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>날짜</TableHead>
                    <TableHead>항목</TableHead>
                    <TableHead>결제자</TableHead>
                    <TableHead className="text-right">금액</TableHead>
                    <TableHead className="text-right">상태</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {[
                    { date: "2024.10.01", item: "단체 항공권", payer: "이주", amount: "₩600,000", status: "정산중", variant: "secondary" as const },
                    { date: "2024.10.05", item: "숙소 예약", payer: "민지", amount: "₩540,000", status: "정산중", variant: "secondary" as const },
                    { date: "2024.10.10", item: "렌터카 선결제", payer: "준호", amount: "₩100,000", status: "미완료", variant: "outline" as const },
                  ].map((row) => (
                    <TableRow key={row.item}>
                      <TableCell className="text-muted-foreground">{row.date}</TableCell>
                      <TableCell className="font-medium">{row.item}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Avatar size="sm">
                            <AvatarFallback>{row.payer}</AvatarFallback>
                          </Avatar>
                          {row.payer}
                        </div>
                      </TableCell>
                      <TableCell className="text-right font-medium tabular-nums">{row.amount}</TableCell>
                      <TableCell className="text-right">
                        <Badge variant={row.variant}>{row.status}</Badge>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Calendar ─────────────────────────────────────── */}
          <ShowcaseSection id="calendar" title="Calendar">
            <DemoCard label="2024년 5월 — 가족 일정">
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-base font-semibold text-foreground">2024년 5월</p>
                    <p className="text-xs text-muted-foreground mt-0.5">이번 주에 6개의 가족 일정이 있습니다</p>
                  </div>
                  <div className="flex rounded-lg overflow-hidden ring-1 ring-border">
                    {(["월", "주", "일"] as const).map((v, i) => (
                      <div
                        key={v}
                        className={`px-3 py-1.5 text-xs font-medium select-none ${
                          i === 0
                            ? "bg-primary text-primary-foreground"
                            : "bg-card text-muted-foreground"
                        }`}
                      >
                        {v}
                      </div>
                    ))}
                  </div>
                </div>
                <MonthCalendar
                  year={2024}
                  month={5}
                  today={7}
                  events={[
                    { day: 1, label: "엄마: 요가", color: "mauve" },
                    { day: 3, label: "아빠: 헬스", color: "blue" },
                    { day: 6, label: "아이들: 피아노", color: "green" },
                    { day: 7, label: "엄마: 치과 예약", color: "mauve" },
                    { day: 7, label: "아이들: 축구", color: "green" },
                    { day: 10, label: "아빠: 음정", color: "blue" },
                    { day: 12, label: "가족 바베큐", color: "amber" },
                    { day: 15, label: "아이들: 수학 클럽", color: "green" },
                    { day: 22, label: "가족 외출", color: "primary" },
                  ]}
                />
              </div>
            </DemoCard>
          </ShowcaseSection>

          {/* ── Carousel (Embla / shadcn) ───────────────────── */}
          <ShowcaseSection id="carousel" title="Carousel — Embla (shadcn)">
            <EmblaCarouselDemo />
          </ShowcaseSection>

          {/* ── Carousel (Swiper) ───────────────────────────── */}
          <ShowcaseSection id="swiper" title="Carousel — Swiper">
            <SwiperCarouselDemo />
          </ShowcaseSection>

          {/* ── Skeletons ───────────────────────────────────── */}
          <ShowcaseSection id="skeletons" title="Skeletons">
            <DemoCard label="Loading States">
              <div className="space-y-4">
                {/* Card skeleton */}
                <div className="space-y-2">
                  <Skeleton className="h-4 w-1/3" />
                  <Skeleton className="h-3 w-1/2" />
                </div>
                <Separator />
                {/* List skeleton */}
                {[1, 2, 3].map((i) => (
                  <div key={i} className="flex items-center gap-3">
                    <Skeleton className="size-8 rounded-full shrink-0" />
                    <div className="flex-1 space-y-1.5">
                      <Skeleton className="h-3 w-3/4" />
                      <Skeleton className="h-2.5 w-1/2" />
                    </div>
                    <Skeleton className="h-3 w-12 shrink-0" />
                  </div>
                ))}
                <Separator />
                {/* Image grid skeleton */}
                <div className="grid grid-cols-4 gap-2">
                  {[1, 2, 3, 4].map((i) => (
                    <Skeleton key={i} className="aspect-square rounded-lg" />
                  ))}
                </div>
              </div>
            </DemoCard>
          </ShowcaseSection>

        </div>
      </div>
    </div>
  )
}
