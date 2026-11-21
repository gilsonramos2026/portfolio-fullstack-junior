import type { ReactNode } from "react";
import { AlertCircle, CheckCircle2, Info } from "lucide-react";
import { cn } from "@/lib/utils";

type AlertTone = "error" | "success" | "info";

const tones: Record<AlertTone, { box: string; icon: typeof Info }> = {
  error: { box: "bg-danger/10 text-danger", icon: AlertCircle },
  success: { box: "bg-accent/10 text-accent", icon: CheckCircle2 },
  info: { box: "bg-muted text-muted-foreground", icon: Info },
};

interface AlertProps {
  tone?: AlertTone;
  children: ReactNode;
  className?: string;
}

export function Alert({ tone = "info", children, className }: AlertProps) {
  const { box, icon: Icon } = tones[tone];
  return (
    <p role={tone === "error" ? "alert" : "status"} className={cn("flex items-start gap-2 rounded-lg px-3 py-2 text-sm", box, className)}>
      <Icon className="mt-0.5 h-4 w-4 shrink-0" aria-hidden />
      <span>{children}</span>
    </p>
  );
}
