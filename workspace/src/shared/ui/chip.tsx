import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/shared/utils"

const chipVariants = cva(
  "inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium transition-colors",
  {
    variants: {
      color: {
        default: "bg-muted text-muted-foreground",
        slate: "bg-primary/10 text-primary",
        blue: "bg-[#d2e1f7]/80 text-[#516072]",
        green: "bg-[#d1f5e4]/80 text-[#2e7d5a]",
        mauve: "bg-[#ead6f0]/80 text-[#7c4d8a]",
        amber: "bg-[#fdf0d0]/80 text-[#8a6800]",
      },
      size: {
        sm: "px-2 py-px text-[11px]",
        md: "px-2.5 py-0.5 text-xs",
        lg: "px-3 py-1 text-sm",
      },
    },
    defaultVariants: {
      color: "default",
      size: "md",
    },
  }
)

interface ChipProps extends Omit<React.HTMLAttributes<HTMLSpanElement>, "color"> {
  color?: "default" | "slate" | "blue" | "green" | "mauve" | "amber"
  size?: "sm" | "md" | "lg"
  icon?: React.ReactNode
}

function Chip({ className, color, size, icon, children, ...props }: ChipProps) {
  return (
    <span className={cn(chipVariants({ color, size }), className)} {...props}>
      {icon && <span className="shrink-0 [&>svg]:size-3">{icon}</span>}
      {children}
    </span>
  )
}

export { Chip, chipVariants }
