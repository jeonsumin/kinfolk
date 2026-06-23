import { cn } from "@/shared/utils"

const DAY_LABELS = ["일", "월", "화", "수", "목", "금", "토"]

const eventColorMap = {
  primary: "bg-primary/10 text-primary",
  blue: "bg-[#d2e1f7]/80 text-[#516072]",
  green: "bg-[#d1f5e4]/80 text-[#2e7d5a]",
  mauve: "bg-[#ead6f0]/80 text-[#7c4d8a]",
  amber: "bg-[#fdf0d0]/80 text-[#8a6800]",
}

export interface CommCalendarEvent {
  day: number
  label: string
  color?: keyof typeof eventColorMap
}

interface CommCalendarProps {
  year: number
  month: number
  events?: CommCalendarEvent[]
  today?: number
  selectedDays?: number[]
  className?: string
}

function buildCalendarCells(year: number, month: number) {
  const firstDayOfWeek = new Date(year, month - 1, 1).getDay()
  const daysInMonth = new Date(year, month, 0).getDate()
  const daysInPrevMonth = new Date(year, month - 1, 0).getDate()
  const cells: Array<{ day: number; currentMonth: boolean }> = []

  for (let i = firstDayOfWeek - 1; i >= 0; i--) cells.push({ day: daysInPrevMonth - i, currentMonth: false })
  for (let i = 1; i <= daysInMonth; i++) cells.push({ day: i, currentMonth: true })
  for (let i = 1; cells.length < Math.ceil(cells.length / 7) * 7; i++) cells.push({ day: i, currentMonth: false })

  return cells
}

function CommCalendar({ year, month, events = [], today, selectedDays = [], className }: CommCalendarProps) {
  const cells = buildCalendarCells(year, month)
  const selected = new Set(selectedDays)

  return (
    <div className={cn("w-full", className)}>
      <div className="mb-1 grid grid-cols-7">
        {DAY_LABELS.map((label) => (
          <div key={label} className="py-2 text-center text-[11px] font-medium text-muted-foreground">
            {label}
          </div>
        ))}
      </div>

      <div className="grid grid-cols-7 overflow-hidden rounded-lg ring-1 ring-border">
        {cells.map((cell, index) => {
          const cellEvents = cell.currentMonth ? events.filter((event) => event.day === cell.day) : []
          const isToday = cell.currentMonth && today === cell.day
          const isSelected = cell.currentMonth && selected.has(cell.day)

          return (
            <div
              key={`${cell.day}-${index}`}
              className={cn(
                "flex min-h-[72px] flex-col gap-1 border-r border-b border-border/40 p-1.5",
                !cell.currentMonth && "bg-muted/20",
                isSelected && "bg-primary text-primary-foreground"
              )}
            >
              <span className={cn(
                "flex size-6 shrink-0 items-center justify-center rounded-full text-xs font-medium",
                cell.currentMonth ? "text-foreground" : "text-muted-foreground/30",
                isToday && "bg-primary text-primary-foreground",
                isSelected && "text-primary-foreground"
              )}>
                {cell.day}
              </span>
              <div className="flex flex-col gap-0.5 overflow-hidden">
                {cellEvents.slice(0, 2).map((event, eventIndex) => (
                  <span
                    key={`${event.day}-${eventIndex}-${event.label}`}
                    className={cn(
                      "truncate rounded px-1.5 py-px text-[10px] font-medium leading-tight",
                      isSelected && (event.color ?? "primary") === "primary"
                        ? "bg-primary-foreground/20 text-primary-foreground"
                        : eventColorMap[event.color ?? "primary"]
                    )}
                  >
                    {event.label}
                  </span>
                ))}
                {cellEvents.length > 2 && <span className="px-1.5 text-[10px] text-muted-foreground">+{cellEvents.length - 2}</span>}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export { CommCalendar }
