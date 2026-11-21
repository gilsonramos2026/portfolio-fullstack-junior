import { Github, Linkedin, Mail } from "lucide-react";
import { IconLink, type IconButtonVariant } from "@/components/ui/IconButton";
import type { Profile } from "@/types";

interface SocialLinksProps {
  profile: Pick<Profile, "githubUrl" | "linkedinUrl" | "email">;
  variant?: IconButtonVariant;
  className?: string;
}

/** Ícones de GitHub, LinkedIn e e-mail; só exibe os links que existem no perfil. */
export function SocialLinks({ profile, variant = "ghost", className }: SocialLinksProps) {
  const items = [
    profile.githubUrl && { href: profile.githubUrl, label: "GitHub", icon: Github },
    profile.linkedinUrl && { href: profile.linkedinUrl, label: "LinkedIn", icon: Linkedin },
    { href: `mailto:${profile.email}`, label: "Enviar e-mail", icon: Mail },
  ].filter((item): item is { href: string; label: string; icon: typeof Github } => Boolean(item));

  return (
    <ul className={className ?? "flex items-center gap-1"}>
      {items.map(({ href, label, icon: Icon }) => (
        <li key={label}>
          <IconLink href={href} label={label} variant={variant} size="md" className="h-11 w-11">
            <Icon className="h-5 w-5" aria-hidden />
          </IconLink>
        </li>
      ))}
    </ul>
  );
}
