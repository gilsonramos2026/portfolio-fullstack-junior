import type { ButtonHTMLAttributes } from "react";
import { cn } from "@/lib/utils";

interface FilterChipProps extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "aria-pressed"> {
  selected?: boolean;
  size?: "sm" | "md";
}

/** Botão de filtro/opção rápida (toggle). Usado em filtros de skills/projetos e respostas rápidas do chat. */
export function FilterChip({ selected = false, size = "md", className, type = "button", ...props }: FilterChipProps) {
  return (
    <button
      type={type}
      aria-pressed={selected}
      className={cn(
        "rounded-full border font-medium transition disabled:opacity-50",
        "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 focus-visible:ring-offset-background",
        size === "md" ? "px-4 py-1.5 text-sm" : "px-3 py-1 text-xs",
        selected
          ? "border-primary bg-primary text-primary-foreground"
          : "border-border bg-card text-muted-foreground hover:border-primary hover:text-foreground",
        className,
      )}
      {...props}
    />
  );
}
