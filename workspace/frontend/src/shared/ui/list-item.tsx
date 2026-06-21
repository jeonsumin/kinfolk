import { cn } from "@/shared/utils"

interface ListItemProps extends React.HTMLAttributes<HTMLDivElement> {
  leading?: React.ReactNode
  title: string
  subtitle?: string
  trailing?: React.ReactNode
}

function ListItem({ leading, title, subtitle, trailing, className, ...props }: ListItemProps) {
  return (
    <div
      data-slot="list-item"
      className={cn(
        "flex items-center gap-3 px-4 py-3 transition-colors",
        props.onClick && "cursor-pointer hover:bg-muted/50",
        className
      )}
      {...props}
    >
      {leading && (
        <div className="shrink-0 text-muted-foreground [&>svg]:size-4">{leading}</div>
      )}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-foreground truncate leading-snug">{title}</p>
        {subtitle && (
          <p className="text-xs text-muted-foreground truncate mt-0.5">{subtitle}</p>
        )}
      </div>
      {trailing && (
        <div className="shrink-0 text-muted-foreground [&>svg]:size-4">{trailing}</div>
      )}
    </div>
  )
}

function ListGroup({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      data-slot="list-group"
      className={cn(
        "overflow-hidden rounded-xl border border-border bg-card divide-y divide-border",
        className
      )}
      {...props}
    />
  )
}

export { ListItem, ListGroup }
