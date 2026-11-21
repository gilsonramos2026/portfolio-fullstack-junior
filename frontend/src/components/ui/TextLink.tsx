import { forwardRef, type AnchorHTMLAttributes, type ButtonHTMLAttributes } from "react";
import Link from "next/link";
import { ExternalLink } from "lucide-react";
import { cn } from "@/lib/utils";

const styles =
  "inline-flex min-h-8 items-center gap-1 rounded text-sm font-medium text-primary transition hover:underline " +
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary";

interface TextLinkProps extends Omit<AnchorHTMLAttributes<HTMLAnchorElement>, "href"> {
  href: string;
  /** Mostra o ícone de link externo (por padrão, quando a URL começa com http). */
  showExternalIcon?: boolean;
}

/** Link textual reutilizável. Rotas internas usam next/link; URLs externas abrem em nova aba com rel seguro. */
export function TextLink({ href, showExternalIcon, className, children, ...props }: TextLinkProps) {
  const external = /^https?:\/\//i.test(href);
  const internal = href.startsWith("/") && !href.startsWith("//");

  if (internal) {
    return (
      <Link href={href} className={cn(styles, className)} {...props}>
        {children}
      </Link>
    );
  }
  return (
    <a
      href={href}
      className={cn(styles, className)}
      {...(external ? { target: "_blank", rel: "noopener noreferrer" } : {})}
      {...props}
    >
      {children}
      {(showExternalIcon ?? external) && <ExternalLink className="h-3.5 w-3.5" aria-hidden />}
    </a>
  );
}

/** Botão com aparência de link textual (ações que não navegam, ex.: abrir modal). */
export const TextButton = forwardRef<HTMLButtonElement, ButtonHTMLAttributes<HTMLButtonElement>>(function TextButton(
  { className, type = "button", ...props },
  ref,
) {
  return <button ref={ref} type={type} className={cn(styles, className)} {...props} />;
});
