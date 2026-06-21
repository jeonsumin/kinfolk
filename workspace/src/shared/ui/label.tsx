"use client"

import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/shared/utils/index"

const labelVariants = cva(
  "leading-none select-none group-data-[disabled=true]:pointer-events-none group-data-[disabled=true]:opacity-50 peer-disabled:cursor-not-allowed peer-disabled:opacity-50",
  {
    variants: {
      variant: {
        default: "text-foreground font-medium",
        muted: "text-muted-foreground font-normal",
        destructive: "text-destructive font-medium",
        required:
          "text-foreground font-medium after:content-['*'] after:ml-0.5 after:text-destructive after:text-sm",
      },
      size: {
        sm: "text-xs",
        default: "text-sm",
        lg: "text-base",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

interface LabelProps
  extends React.ComponentProps<"label">,
    VariantProps<typeof labelVariants> {}

function Label({ className, variant, size, ...props }: LabelProps) {
  return (
    <label
      data-slot="label"
      className={cn("flex items-center gap-2", labelVariants({ variant, size }), className)}
      {...props}
    />
  )
}

export { Label, labelVariants }
