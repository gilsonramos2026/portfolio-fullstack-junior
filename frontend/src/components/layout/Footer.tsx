import Link from "next/link";
import { ArrowUp, FileText } from "lucide-react";
import { CopyButton } from "@/components/ui/CopyButton";
import { SocialLinks } from "@/components/ui/SocialLinks";
import { TextLink } from "@/components/ui/TextLink";
import { navLinks } from "@/lib/site";
import type { Profile } from "@/types";

interface FooterProps {
  profile: Profile;
  /** Prefixo dos links âncora: "" na home, "/" nas demais páginas. */
  basePath?: string;
}

const linkStyles =
  "inline-flex min-h-8 items-center rounded text-sm text-muted-foreground transition hover:text-foreground " +
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary";

export function Footer({ profile, basePath = "" }: FooterProps) {
  return (
    <footer className="border-t border-border">
      <div className="container grid gap-10 py-12 md:grid-cols-[1.4fr_1fr_1.2fr]">
        <div className="space-y-3">
          <p className="font-mono text-sm font-semibold">
            <span className="text-accent">&gt;</span> {profile.fullName}
          </p>
          <p className="max-w-sm text-sm text-muted-foreground">{profile.role}</p>
          {profile.openToWork && (
            <p className="inline-flex items-center gap-2 rounded-full border border-border bg-card px-3 py-1 text-sm">
              <span aria-hidden className="h-2 w-2 rounded-full bg-accent" /> Aberto a estágio e vagas júnior
            </p>
          )}
        </div>

        <nav aria-label="Rodapé">
          <p className="mb-2 font-display text-base font-semibold">Navegação</p>
          <ul>
            {navLinks.map(({ href, label }) => (
              <li key={href}>
                <Link href={`${basePath}${href}`} className={linkStyles}>
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>

        <div className="space-y-3">
          <p className="font-display text-base font-semibold">Contato</p>
          <div className="flex items-center gap-2">
            <a href={`mailto:${profile.email}`} className={`${linkStyles} break-all`}>
              {profile.email}
            </a>
            <CopyButton text={profile.email} label="Copiar e-mail" />
          </div>
          {profile.resumeUrl && (
            <TextLink href={profile.resumeUrl}>
              <FileText className="h-4 w-4" aria-hidden /> Baixar currículo
            </TextLink>
          )}
          <SocialLinks profile={profile} />
        </div>
      </div>

      <div className="border-t border-border">
        <div className="container flex flex-col items-center justify-between gap-3 py-5 text-sm text-muted-foreground sm:flex-row">
          <p>
            © {new Date().getFullYear()} {profile.fullName}
          </p>
          <p className="font-mono text-xs">Next.js · Spring Boot · PostgreSQL</p>
          <div className="flex items-center gap-4">
            <Link href="/privacidade" className={linkStyles}>
              Privacidade
            </Link>
            <Link href={`${basePath}#inicio`} className={linkStyles}>
              Voltar ao topo <ArrowUp className="ml-1 h-4 w-4" aria-hidden />
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
