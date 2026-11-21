import { MapPin } from "lucide-react";
import { EntryRow } from "@/components/ui/EntryRow";
import { Reveal } from "@/components/ui/Reveal";
import { Section } from "@/components/ui/Section";
import { formatMonthYear } from "@/lib/utils";
import type { Education, Profile } from "@/types";

export function About({ profile, education: all }: { profile: Profile; education: Education[] }) {
  const education = all.filter((item) => item.type === "DEGREE");
  const [lead, ...rest] = profile.about.split(/\n{2,}/).map((p) => p.trim()).filter(Boolean);

  return (
    <Section id="sobre" title="Sobre mim" description="Quem eu sou e onde estudo.">
      <Reveal className="space-y-5">
        {lead && <p className="max-w-[40rem] text-xl leading-snug text-foreground sm:text-2xl">{lead}</p>}
        {rest.map((paragraph) => (
          <p key={paragraph} className="max-w-[40rem] text-muted-foreground">
            {paragraph}
          </p>
        ))}
        {profile.location && (
          <p className="inline-flex items-center gap-2 text-sm text-foreground">
            <MapPin className="h-4 w-4 text-accent" aria-hidden /> {profile.location}
          </p>
        )}
      </Reveal>

      <div className="mt-14">
        <h3 className="text-xl font-semibold">Formação acadêmica</h3>
        {education.length === 0 ? (
          <p className="mt-4 text-muted-foreground">Formação acadêmica em breve.</p>
        ) : (
          <ol className="mt-2 divide-y divide-border border-b border-border">
            {education.map((item) => (
              <EntryRow
                key={item.id}
                period={`${formatMonthYear(item.startDate)} — ${item.endDate ? formatMonthYear(item.endDate) : "atual"}`}
                title={item.title}
                subtitle={item.institution}
                description={item.description}
              />
            ))}
          </ol>
        )}
      </div>
    </Section>
  );
}
