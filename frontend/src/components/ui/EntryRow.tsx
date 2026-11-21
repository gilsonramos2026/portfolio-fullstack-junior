import type { ReactNode } from "react";

interface EntryRowProps {
  /** Período, ex.: "mar. de 2024 — jun. de 2024". Na coluna da esquerda em telas médias ou maiores. */
  period: string;
  title: string;
  subtitle?: string;
  description?: string | null;
  /** Informações complementares (carga horária, link do certificado…), alinhadas à direita. */
  aside?: ReactNode;
}

/** Linha de uma lista editorial (formação, cursos): período | título + detalhes | complemento. */
export function EntryRow({ period, title, subtitle, description, aside }: EntryRowProps) {
  return (
    <li className="grid gap-x-8 gap-y-2 py-6 sm:grid-cols-[9.5rem_minmax(0,1fr)] md:grid-cols-[9.5rem_minmax(0,1fr)_auto]">
      <p className="text-sm tabular-nums text-muted-foreground sm:pt-1">{period}</p>
      <div className="min-w-0">
        <h3 className="text-lg font-semibold leading-snug">{title}</h3>
        {subtitle && <p className="text-muted-foreground">{subtitle}</p>}
        {description && <p className="mt-2 max-w-prose text-sm text-muted-foreground">{description}</p>}
      </div>
      {aside && <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm sm:col-start-2 md:col-start-3 md:flex-col md:items-end md:pt-1">{aside}</div>}
    </li>
  );
}
