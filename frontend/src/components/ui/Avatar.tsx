"use client";

import { useState } from "react";
import { ToolIcon } from "@/components/ui/ToolIcon";
import { cn } from "@/lib/utils";

interface AvatarProps {
  src: string | null;
  name: string;
  /** Ferramentas exibidas numa pequena etiqueta sobre a borda inferior da foto. */
  orbit?: string[];
  className?: string;
}

function initialsOf(name: string): string {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join("");
}

/**
 * Retrato em proporção 4:5 com uma moldura deslocada atrás, que dá profundidade sem sombra nem brilho.
 * Sem foto (ou se ela falhar ao carregar), exibe as iniciais. Entra com uma única animação suave.
 */
export function Avatar({ src, name, orbit = [], className }: AvatarProps) {
  const [failed, setFailed] = useState(false);
  const showImage = Boolean(src) && !failed;

  return (
    <div className={cn("relative w-40 sm:w-56 md:w-full md:max-w-[17rem] lg:max-w-[24rem]", className)}>
      <div className="motion-safe:animate-pop-in">
        <div aria-hidden className="absolute inset-0 translate-x-3 translate-y-3 rounded-2xl border border-primary/40 sm:translate-x-4 sm:translate-y-4" />
        <div className="relative aspect-[4/5] overflow-hidden rounded-2xl border border-border bg-muted">
          {showImage ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={src as string}
              alt={`Foto de ${name}`}
              width={352}
              height={440}
              fetchPriority="high"
              decoding="async"
              onError={() => setFailed(true)}
              className="h-full w-full object-cover"
            />
          ) : (
            <div
              role="img"
              aria-label={`Iniciais de ${name}`}
              className="flex h-full w-full items-center justify-center bg-muted font-display text-6xl font-semibold tracking-tight text-foreground/70 sm:text-8xl"
            >
              {initialsOf(name)}
            </div>
          )}
        </div>

        {orbit.length > 0 && (
          <ul
            aria-label="Tecnologias principais"
            className="absolute -bottom-3 left-4 flex items-center gap-1 rounded-full border border-border bg-card px-2.5 py-1.5 text-primary"
          >
            {orbit.map((tool) => (
              <li key={tool} title={tool}>
                <ToolIcon name={tool} className="h-4 w-4 sm:h-5 sm:w-5" />
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
