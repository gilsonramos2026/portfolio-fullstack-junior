import { Code2 } from "lucide-react";
import { resolveToolIcon } from "@/lib/tools";
import { cn } from "@/lib/utils";

interface ToolIconProps {
  name: string;
  className?: string;
}

/** Ícone da ferramenta (SVG local, herda a cor do texto). Usa um ícone genérico se não houver correspondência. */
export function ToolIcon({ name, className }: ToolIconProps) {
  const path = resolveToolIcon(name);
  if (!path) return <Code2 aria-hidden className={cn("h-5 w-5", className)} />;
  return (
    <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden className={cn("h-5 w-5", className)}>
      <path d={path} />
    </svg>
  );
}
