"use client";

import type { ReactNode } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { useQuery } from "@tanstack/react-query";
import { GraduationCap, ExternalLink, FolderGit2, KeyRound, LayoutDashboard, LogOut, MessageSquare, UserRound, Wrench } from "lucide-react";
import { SkipLink } from "@/components/ui/SkipLink";
import { ThemeToggle } from "@/components/ui/ThemeToggle";
import { Button } from "@/components/ui/Button";
import { adminApi } from "@/lib/api-client";
import { cn } from "@/lib/utils";

const items = [
  { href: "/admin", label: "Visão geral", icon: LayoutDashboard, exact: true },
  { href: "/admin/projects", label: "Projetos", icon: FolderGit2, exact: false },
  { href: "/admin/skills", label: "Skills", icon: Wrench, exact: false },
  { href: "/admin/education", label: "Formação e cursos", icon: GraduationCap, exact: false },
  { href: "/admin/profile", label: "Perfil", icon: UserRound, exact: false },
  { href: "/admin/messages", label: "Mensagens", icon: MessageSquare, exact: false },
  { href: "/admin/account", label: "Conta", icon: KeyRound, exact: false },
] as const;

export function AdminShell({ username, children }: { username: string; children: ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [loggingOut, setLoggingOut] = useState(false);
  const unread = useQuery({
    queryKey: ["admin", "messages-unread"],
    queryFn: async () => (await adminApi.get<{ count: number }>("/messages/unread-count")).data.count,
    refetchInterval: 60_000,
  });

  async function handleLogout() {
    setLoggingOut(true);
    try {
      await fetch("/api/auth/logout", { method: "POST" });
      router.replace("/admin/login");
      router.refresh();
    } catch {
      toast.error("Não foi possível encerrar a sessão.");
      setLoggingOut(false);
    }
  }

  return (
    <div className="min-h-screen md:grid md:grid-cols-[240px_1fr]">
      <SkipLink />
      <aside className="border-b border-border bg-card md:sticky md:top-0 md:h-screen md:border-b-0 md:border-r">
        <div className="flex items-center justify-between px-5 py-4 md:block">
          <p className="font-mono text-sm font-semibold">
            <span className="text-accent">&gt;</span> admin
          </p>
          <p className="hidden text-xs text-muted-foreground md:mt-1 md:block">Logado como {username}</p>
        </div>
        <nav aria-label="Administração" className="flex gap-1 overflow-x-auto px-3 pb-3 md:flex-col md:pb-0">
          {items.map(({ href, label, icon: Icon, exact }) => {
            const active = exact ? pathname === href : pathname.startsWith(href);
            return (
              <Link
                key={href}
                href={href}
                aria-current={active ? "page" : undefined}
                className={cn(
                  "flex shrink-0 items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium transition",
                  active ? "bg-primary/10 text-primary" : "text-muted-foreground hover:bg-muted hover:text-foreground",
                )}
              >
                <Icon className="h-4 w-4" aria-hidden />
                {label}
                {href === "/admin/messages" && (unread.data ?? 0) > 0 && (
                  <span aria-label={`${unread.data} não lidas`} className="ml-auto rounded-full bg-primary px-2 py-0.5 text-xs font-semibold text-primary-foreground">
                    {unread.data}
                  </span>
                )}
              </Link>
            );
          })}
        </nav>
      </aside>

      <div className="flex min-w-0 flex-col">
        <header className="flex h-14 items-center justify-end gap-2 border-b border-border px-5">
          <Link href="/" target="_blank" className="mr-auto inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground">
            Ver site <ExternalLink className="h-3.5 w-3.5" aria-hidden />
          </Link>
          <ThemeToggle />
          <Button variant="outline" size="sm" onClick={handleLogout} loading={loggingOut}>
            <LogOut className="h-4 w-4" aria-hidden /> Sair
          </Button>
        </header>
        <main id="conteudo" tabIndex={-1} className="flex-1 px-5 py-8 outline-none md:px-8">{children}</main>
      </div>
    </div>
  );
}
