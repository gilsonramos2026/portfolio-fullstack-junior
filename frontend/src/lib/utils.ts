import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}

const monthFormatter = new Intl.DateTimeFormat("pt-BR", { month: "short", year: "numeric", timeZone: "UTC" });

/** Formata uma data ISO (yyyy-MM-dd) como "mar. de 2024". */
export function formatMonthYear(isoDate: string): string {
  return monthFormatter.format(new Date(`${isoDate}T00:00:00Z`));
}

/** Converte string de formulário vazia em null, como a API espera. */
export function emptyToNull(value: string | null | undefined): string | null {
  const trimmed = value?.trim();
  return trimmed ? trimmed : null;
}
