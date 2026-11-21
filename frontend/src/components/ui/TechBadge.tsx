import { ToolIcon } from "@/components/ui/ToolIcon";

export function TechBadge({ name }: { name: string }) {
  return (
    <span className="inline-flex items-center gap-1.5 rounded-full bg-primary/10 px-2.5 py-1 text-xs font-medium text-primary">
      <ToolIcon name={name} className="h-3.5 w-3.5" />
      {name}
    </span>
  );
}
