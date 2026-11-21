import { forwardRef, type AnchorHTMLAttributes, type ButtonHTMLAttributes, type ReactNode } from "react";
import { cn } from "@/lib/utils";

export type IconButtonVariant = "ghost" | "outline" | "solid" | "inverse";
export type IconButtonSize = "sm" | "md" | "lg";

const variants: Record<IconButtonVariant, string> = {
  ghost: "text-muted-foreground hover:bg-muted hover:text-foreground",
  outline: "border border-border hover:bg-muted",
  solid: "bg-primary text-primary-foreground shadow-lg hover:scale-105",
  inverse: "text-primary-foreground hover:bg-white/20",
};

const sizes: Record<IconButtonSize, string> = {
  sm: "h-8 w-8",
  md: "h-9 w-9",
  lg: "h-14 w-14",
};

export function iconButtonStyles({
  variant = "ghost",
  size = "md",
  className,
}: { variant?: IconButtonVariant; size?: IconButtonSize; className?: string } = {}): string {
  return cn(
    "inline-flex shrink-0 items-center justify-center rounded-lg transition",
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 focus-visible:ring-offset-background",
    "disabled:pointer-events-none disabled:opacity-50",
    size === "lg" && "rounded-full",
    variants[variant],
    sizes[size],
    className,
  );
}

interface IconButtonProps extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "aria-label"> {
  /** Texto acessível obrigatório: botões só com ícone precisam de rótulo. */
  label: string;
  variant?: IconButtonVariant;
  size?: IconButtonSize;
  children: ReactNode;
}

export const IconButton = forwardRef<HTMLButtonElement, IconButtonProps>(function IconButton(
  { label, variant, size, className, type = "button", children, ...props },
  ref,
) {
  return (
    <button ref={ref} type={type} aria-label={label} title={label} className={iconButtonStyles({ variant, size, className })} {...props}>
      {children}
    </button>
  );
});

interface IconLinkProps extends Omit<AnchorHTMLAttributes<HTMLAnchorElement>, "aria-label" | "href"> {
  href: string;
  label: string;
  variant?: IconButtonVariant;
  size?: IconButtonSize;
  children: ReactNode;
}

/** Link (externo ou mailto) com aparência de botão de ícone. */
export function IconLink({ href, label, variant, size, className, children, ...props }: IconLinkProps) {
  const external = /^https?:\/\//i.test(href);
  return (
    <a
      href={href}
      aria-label={label}
      title={label}
      className={iconButtonStyles({ variant, size, className })}
      {...(external ? { target: "_blank", rel: "noopener noreferrer" } : {})}
      {...props}
    >
      {children}
    </a>
  );
}
