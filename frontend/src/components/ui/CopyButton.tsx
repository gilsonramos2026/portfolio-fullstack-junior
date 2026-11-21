"use client";

import { useEffect, useState } from "react";
import { Check, Copy } from "lucide-react";
import { IconButton } from "@/components/ui/IconButton";

export function CopyButton({ text, label = "Copiar" }: { text: string; label?: string }) {
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    if (!copied) return;
    const timer = window.setTimeout(() => setCopied(false), 2000);
    return () => window.clearTimeout(timer);
  }, [copied]);

  async function handleCopy() {
    try {
      await navigator.clipboard.writeText(text);
      setCopied(true);
    } catch {
      setCopied(false);
    }
  }

  return (
    <IconButton label={copied ? "Copiado!" : label} variant="outline" onClick={handleCopy}>
      {copied ? <Check className="h-4 w-4 text-accent" aria-hidden /> : <Copy className="h-4 w-4" aria-hidden />}
    </IconButton>
  );
}
