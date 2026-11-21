import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

interface SectionProps {
  id: string;
  /** Título curto da seção; fica numa coluna lateral fixa em telas largas. */
  title: string;
  /** Uma frase que diz o que o visitante encontra aqui. */
  description?: string;
  className?: string;
  children: ReactNode;
}

/**
 * Seção editorial da home: título e descrição à esquerda, conteúdo à direita (empilhados no celular).
 * Seções são separadas por um fio fino, sem faixas de fundo alternadas.
 */
export function Section({ id, title, description, className, children }: SectionProps) {
  return (
    <section id={id} aria-labelledby={`${id}-title`} className={cn("border-t border-border py-16 sm:py-24", className)}>
      <div className="container grid gap-8 lg:grid-cols-[minmax(0,15rem)_minmax(0,1fr)] lg:gap-14 xl:grid-cols-[minmax(0,17rem)_minmax(0,1fr)] xl:gap-20">
        <header className="lg:sticky lg:top-24 lg:self-start">
          <h2 id={`${id}-title`} className="text-3xl font-semibold sm:text-4xl">
            {title}
          </h2>
          {description && <p className="mt-3 max-w-md text-muted-foreground">{description}</p>}
        </header>
        <div className="min-w-0">{children}</div>
      </div>
    </section>
  );
}
