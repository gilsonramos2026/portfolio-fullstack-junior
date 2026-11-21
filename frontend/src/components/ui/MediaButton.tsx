import type { ButtonHTMLAttributes } from "react";
import { cn } from "@/lib/utils";

/** Botão que envolve uma imagem/capa clicável (cards de projeto, galeria). */
export function MediaButton({ className, type = "button", ...props }: ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      type={type}
      className={cn(
        "group block w-full overflow-hidden text-left focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-inset",
        className,
      )}
      {...props}
    />
  );
}
