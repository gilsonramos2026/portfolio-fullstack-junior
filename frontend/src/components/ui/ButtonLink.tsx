import type { AnchorHTMLAttributes, ReactNode } from "react";
import Link from "next/link";
import { buttonStyles, type ButtonSize, type ButtonVariant } from "@/components/ui/Button";

interface ButtonLinkProps extends Omit<AnchorHTMLAttributes<HTMLAnchorElement>, "href"> {
  href: string;
  variant?: ButtonVariant;
  size?: ButtonSize;
  children: ReactNode;
}

const isExternal = (href: string) => /^https?:\/\//i.test(href);
const isInternalRoute = (href: string) => href.startsWith("/") && !href.startsWith("//");

/**
 * Link com aparência de botão. Escolhe sozinho o elemento correto:
 * rota interna → next/link; URL externa → <a> com target/rel seguros; âncora/mailto → <a>.
 */
export function ButtonLink({ href, variant, size, className, children, ...props }: ButtonLinkProps) {
  const classes = buttonStyles({ variant, size, className });

  if (isInternalRoute(href)) {
    return (
      <Link href={href} className={classes} {...props}>
        {children}
      </Link>
    );
  }
  return (
    <a
      href={href}
      className={classes}
      {...(isExternal(href) ? { target: "_blank", rel: "noopener noreferrer" } : {})}
      {...props}
    >
      {children}
    </a>
  );
}
