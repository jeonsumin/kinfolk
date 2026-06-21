import * as React from "react"
import { cn } from "@/shared/utils/index"

const DAY_LABELS = ["일", "월", "화", "수", "목", "금", "토"]

const eventColorMap = {
  primary: "bg-primary/10 text-primary",
  blue: "bg-[#d2e1f7]/80 text-[#516072]",
  green: "bg-[#d1f5e4]/80 text-[#2e7d5a]",
  mauve: "bg-[#ead6f0]/80 text-[#7c4d8a]",
  amber: "bg-[#fdf0d0]/80 text-[#8a6800]",
}

export interface CalendarEvent {
  day: number
  label: string
  color?: keyof typeof eventColorMap
}

interface MonthCalendarProps {
  year: number
  month: number  // 1-12
  events?: CalendarEvent[]
  today?: number
  className?: string
}

function buildCalendarCells(year: number, month: number) {
  const firstDayOfWeek = new Date(year, month - 1, 1).getDay()
  const daysInMonth = new Date(year, month, 0).getDate()
  const daysInPrevMonth = new Date(year, month - 1, 0).getDate()

  const cells: Array<{ day: number; currentMonth: boolean }> = []

  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    cells.push({ day: daysInPrevMonth - i, currentMonth: false })
  }
  for (let i = 1; i <= daysInMonth; i++) {
    cells.push({ day: i, currentMonth: true })
  }
  const totalCells = Math.ceil(cells.length / 7) * 7
  for (let i = 1; cells.length < totalCells; i++) {
    cells.push({ day: i, currentMonth: false })
  }

  return cells
}

function MonthCalendar({ year, month, events = [], today, className }: MonthCalendarProps) {
  const cells = buildCalendarCells(year, month)

  return (
    <div className={cn("w-full", className)}>
      {/* Day labels */}
      <div className="grid grid-cols-7 mb-1">
        {DAY_LABELS.map((label) => (
          <div key={label} className="py-2 text-center text-[11px] font-medium text-muted-foreground">
            {label}
          </div>
        ))}
      </div>

      {/* Calendar grid */}
      <div className="grid grid-cols-7 gap-px bg-border rounded-lg overflow-hidden ring-1 ring-border">
        {cells.map((cell, idx) => {
          const cellEvents = cell.currentMonth
            ? events.filter((e) => e.day === cell.day)
            : []
          const isToday = cell.currentMonth && today !== undefined && cell.day === today

          return (
            <div
              key={idx}
              className={cn(
                "bg-card min-h-[72px] p-1.5 flex flex-col gap-1",
                !cell.currentMonth && "bg-muted/20"
              )}
            >
              <span
                className={cn(
                  "text-xs font-medium w-6 h-6 flex items-center justify-center rounded-full shrink-0",
                  cell.currentMonth ? "text-foreground" : "text-muted-foreground/30",
                  isToday && "bg-primary text-primary-foreground"
                )}
              >
                {cell.day}
              </span>
              <div className="flex flex-col gap-0.5 overflow-hidden">
                {cellEvents.slice(0, 2).map((event, eIdx) => (
                  <span
                    key={eIdx}
                    className={cn(
                      "text-[10px] font-medium px-1.5 py-px rounded truncate leading-tight",
                      eventColorMap[event.color ?? "primary"]
                    )}
                  >
                    {event.label}
                  </span>
                ))}
                {cellEvents.length > 2 && (
                  <span className="text-[10px] text-muted-foreground px-1.5">
                    +{cellEvents.length - 2}
                  </span>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export { MonthCalendar }
