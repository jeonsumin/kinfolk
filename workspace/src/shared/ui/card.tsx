import * as React from "react"

import { cn } from "@/shared/utils/index"

function Card({
  className,
  size = "default",
  ...props
}: React.ComponentProps<"div"> & { size?: "default" | "sm" }) {
  return (
    <div
      data-slot="card"
      data-size={size}
      className={cn(
        "group/card flex flex-col gap-(--card-spacing) overflow-hidden rounded-xl bg-card py-(--card-spacing) text-sm text-card-foreground ring-1 ring-foreground/10 [--card-spacing:--spacing(4)] has-data-[slot=card-footer]:pb-0 has-[>img:first-child]:pt-0 data-[size=sm]:[--card-spacing:--spacing(3)] data-[size=sm]:has-data-[slot=card-footer]:pb-0 *:[img:first-child]:rounded-t-xl *:[img:last-child]:rounded-b-xl",
        className
      )}
      {...props}
    />
  )
}

function CardHeader({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="card-header"
      className={cn(
        "group/card-header @container/card-header grid auto-rows-min items-start gap-1 rounded-t-xl px-(--card-spacing) has-data-[slot=card-action]:grid-cols-[1fr_auto] has-data-[slot=card-description]:grid-rows-[auto_auto] [.border-b]:pb-(--card-spacing)",
        className
      )}
      {...props}
    />
  )
}

function CardTitle({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="card-title"
      className={cn(
        "font-heading text-base leading-snug font-medium group-data-[size=sm]/card:text-sm",
        className
      )}
      {...props}
    />
  )
}

function CardDescription({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="card-description"
      className={cn("text-sm text-muted-foreground", className)}
      {...props}
    />
  )
}

function CardAction({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="card-action"
      className={cn(
        "col-start-2 row-span-2 row-start-1 self-start justify-self-end",
        className
      )}
      {...props}
    />
  )
}

function CardContent({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="card-content"
      className={cn("px-(--card-spacing)", className)}
      {...props}
    />
  )
}

function CardFooter({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="card-footer"
      className={cn(
        "flex items-center rounded-b-xl border-t bg-muted/50 p-(--card-spacing)",
        className
      )}
      {...props}
    />
  )
}

// ─── ImageCard ───────────────────────────────────────────────────────────────

interface ImageCardProps {
  src?: string
  alt?: string
  aspectRatio?: string
  className?: string
  children?: React.ReactNode
}

function ImageCard({
  src,
  alt,
  aspectRatio = "aspect-[4/3]",
  className,
  children,
}: ImageCardProps) {
  return (
    <div
      data-slot="image-card"
      className={cn(
        "overflow-hidden rounded-xl ring-1 ring-foreground/10 bg-muted",
        aspectRatio,
        className
      )}
    >
      {src ? (
        <img src={src} alt={alt ?? ""} className="h-full w-full object-cover" />
      ) : (
        <div className="h-full w-full">{children}</div>
      )}
    </div>
  )
}

// ─── ImageOverlayCard ────────────────────────────────────────────────────────
// children은 src가 없을 때 배경으로 사용 (그라디언트 플레이스홀더 등)

interface ImageOverlayCardProps {
  src?: string
  alt?: string
  title: string
  subtitle?: string
  aspectRatio?: string
  className?: string
  children?: React.ReactNode
}

function ImageOverlayCard({
  src,
  alt,
  title,
  subtitle,
  aspectRatio = "aspect-[4/3]",
  className,
  children,
}: ImageOverlayCardProps) {
  return (
    <div
      data-slot="image-overlay-card"
      className={cn(
        "relative overflow-hidden rounded-xl ring-1 ring-foreground/10",
        aspectRatio,
        className
      )}
    >
      <div className="absolute inset-0">
        {src ? (
          <img
            src={src}
            alt={alt ?? ""}
            className="h-full w-full object-cover"
          />
        ) : (
          children ?? (
            <div className="h-full w-full bg-gradient-to-br from-[#b9c7df] to-[#475569]" />
          )
        )}
      </div>
      {/* 텍스트 가독성을 위한 하단 그라디언트 오버레이 */}
      <div className="absolute inset-0 bg-gradient-to-t from-black/65 via-black/10 to-transparent" />
      <div className="absolute bottom-0 left-0 p-4">
        <p className="text-sm font-semibold text-white leading-snug">{title}</p>
        {subtitle && (
          <p className="text-xs text-white/70 mt-0.5">{subtitle}</p>
        )}
      </div>
    </div>
  )
}

export {
  Card,
  CardHeader,
  CardFooter,
  CardTitle,
  CardAction,
  CardDescription,
  CardContent,
  ImageCard,
  ImageOverlayCard,
}
