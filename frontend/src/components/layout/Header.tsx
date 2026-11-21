"use client";

import { useEffect, useRef, useState } from "react";
import { Menu, X } from "lucide-react";
import { IconButton } from "@/components/ui/IconButton";
import { ThemeToggle } from "@/components/ui/ThemeToggle";
import { navLinks } from "@/lib/site";

/** `basePath` = "/" nas páginas internas, para que as âncoras levem de volta à home. */
export function Header({ name, basePath = "" }: { name: string; basePath?: string }) {
  const [open, setOpen] = useState(false);
  const toggleRef = useRef<HTMLButtonElement>(null);
  const [active, setActive] = useState<string | null>(null);

  // Destaca no menu a seção visível (só na home, onde as âncoras existem).
  useEffect(() => {
    if (basePath !== "") return;
    const sections = navLinks.map((l) => document.querySelector(l.href)).filter((el): el is Element => el !== null);
    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries.filter((e) => e.isIntersecting).sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];
        if (visible) setActive(`#${visible.target.id}`);
      },
      { rootMargin: "-30% 0px -55% 0px", threshold: [0, 0.25, 0.5, 1] },
    );
    sections.forEach((el) => observer.observe(el));
    return () => observer.disconnect();
  }, [basePath]);

  useEffect(() => {
    if (!open) return;
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setOpen(false);
        toggleRef.current?.focus();
      }
    };
    document.addEventListener("keydown", onKeyDown);
    return () => document.removeEventListener("keydown", onKeyDown);
  }, [open]);

  return (
    <header className="sticky top-0 z-40 border-b border-border/70 bg-background/85 backdrop-blur-md">
      <div className="container flex h-14 items-center justify-between">
        <a href={`${basePath}#inicio`} className="-mx-2 rounded-md px-2 py-2 font-mono text-sm font-semibold tracking-tight focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary">
          <span className="text-accent">&lt;</span>
          {name}
          <span className="text-accent"> /&gt;</span>
        </a>

        <nav aria-label="Principal" className="hidden items-center gap-1 md:flex">
          {navLinks.map((link) => (
            <a
              key={link.href}
              href={`${basePath}${link.href}`}
              aria-current={active === link.href ? "location" : undefined}
              className="relative rounded-md px-3 py-2 text-sm text-muted-foreground transition hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary aria-[current=location]:text-foreground after:absolute after:inset-x-3 after:bottom-0.5 after:h-0.5 after:origin-left after:scale-x-0 after:rounded-full after:bg-primary after:transition-transform aria-[current=location]:after:scale-x-100 motion-reduce:after:transition-none"
            >
              {link.label}
            </a>
          ))}
          <div className="ml-2">
            <ThemeToggle />
          </div>
        </nav>

        <div className="flex items-center gap-2 md:hidden">
          <ThemeToggle />
          <IconButton
            ref={toggleRef}
            label={open ? "Fechar menu" : "Abrir menu"}
            variant="outline"
            aria-expanded={open}
            aria-controls="mobile-menu"
            onClick={() => setOpen((value) => !value)}
          >
            {open ? <X className="h-4 w-4" aria-hidden /> : <Menu className="h-4 w-4" aria-hidden />}
          </IconButton>
        </div>
      </div>

      {open && (
        <nav id="mobile-menu" aria-label="Principal (mobile)" className="border-t border-border bg-background md:hidden">
          <ul className="container flex flex-col divide-y divide-border py-1">
            {navLinks.map((link) => (
              <li key={link.href}>
                <a href={`${basePath}${link.href}`} onClick={() => setOpen(false)} className="block py-3.5 font-display text-xl font-medium text-foreground transition hover:text-primary">
                  {link.label}
                </a>
              </li>
            ))}
          </ul>
        </nav>
      )}
    </header>
  );
}
