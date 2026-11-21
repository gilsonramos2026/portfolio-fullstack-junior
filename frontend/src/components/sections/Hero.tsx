import { ArrowDown, Download } from "lucide-react";
import { buildTypingPhrases } from "@/lib/tools";
import type { Profile, Skill } from "@/types";
import { Typewriter } from "../ui/Typewriter";
import { ButtonLink } from "../ui/ButtonLink";
import { SocialLinks } from "../ui/SocialLinks";
import { Avatar } from "../ui/Avatar";
import { ToolIcon } from "../ui/ToolIcon";

/**
 * Primeira dobra: o nome em escala grande é o elemento principal; cargo, apresentação e ações vêm em
 * ordem decrescente de peso. O retrato fica ao lado (acima, no celular) e a stack numa faixa própria.
 * A única animação de entrada é a sequência do texto; nada se move depois dela.
 */
export function Hero({ profile, skills }: { profile: Profile; skills: Skill[] }) {
  const stack = [...skills].sort((a, b) => b.level - a.level).slice(0, 8);

  return (
    <section id="inicio" aria-label="Apresentação">
      <div className="container grid items-center gap-8 py-10 sm:py-16 md:grid-cols-12 md:gap-8 lg:min-h-[min(46rem,calc(100svh-3.5rem-5rem))] lg:gap-10 lg:py-16">
        <div className="order-last md:order-first md:col-span-7">
          {profile.openToWork && (
            <p className="animate-fade-up mb-6 inline-flex w-fit items-center gap-2 rounded-full border border-border bg-card px-3 py-1 text-sm text-foreground">
              <span className="relative flex h-2 w-2" aria-hidden>
                <span className="absolute inline-flex h-full w-full rounded-full bg-accent opacity-60 motion-safe:animate-ping" />
                <span className="relative inline-flex h-2 w-2 rounded-full bg-accent" />
              </span>
              Aberto a estágio e vagas júnior
            </p>
          )}
          <h1 className="animate-fade-up text-[clamp(3rem,9vw,7rem)] font-semibold leading-[0.95] tracking-[-0.03em] [animation-delay:80ms]">
            {profile.fullName}
          </h1>
          <p className="animate-fade-up mt-4 min-h-[2.25rem] text-xl font-medium text-primary [animation-delay:160ms] sm:text-2xl">
            <Typewriter phrases={buildTypingPhrases(profile.role, skills)} />
          </p>
          <p className="animate-fade-up mt-6 max-w-[34rem] text-base leading-relaxed text-muted-foreground [animation-delay:240ms] sm:text-lg">
            {profile.headline} {profile.bio}
          </p>

          <div className="animate-fade-up mt-8 flex flex-wrap items-center gap-3 [animation-delay:320ms]">
            <ButtonLink href="#projetos" size="lg">
              Ver projetos <ArrowDown className="h-4 w-4" aria-hidden />
            </ButtonLink>
            {profile.resumeUrl && (
              <ButtonLink href={profile.resumeUrl} variant="outline" size="lg">
                <Download className="h-4 w-4" aria-hidden /> Currículo
              </ButtonLink>
            )}
            <SocialLinks profile={profile} variant="outline" className="flex items-center gap-2 xl:ml-1 xl:border-l xl:border-border xl:pl-4" />
          </div>
        </div>

        <div className="md:col-span-5 md:justify-self-end">
          <Avatar src={profile.avatarUrl} name={profile.fullName} orbit={stack.slice(0, 3).map((skill) => skill.name)} />
        </div>
      </div>

      {stack.length > 0 && (
        <div className="border-t border-border">
          <ul aria-label="Stack principal" className="container flex flex-wrap items-center gap-x-8 gap-y-3 py-5 text-sm text-muted-foreground">
            {stack.map((skill) => (
              <li key={skill.id} className="flex items-center gap-2">
                <ToolIcon name={skill.name} className="h-4 w-4" />
                <span>{skill.name}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
