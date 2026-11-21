import { EmptyState } from "@/components/ui/EmptyState";
import { Section } from "@/components/ui/Section";
import { SKILL_CATEGORIES, SKILL_CATEGORY_LABELS, type Skill } from "@/types";
import { ToolIcon } from "../ui/ToolIcon";

/** Tecnologias agrupadas por camada. O nível (1–5) segue no cadastro, mas não é exibido: barras de proficiência não dizem nada sem critério. */
export function Skills({ skills }: { skills: Skill[] }) {
  const groups = SKILL_CATEGORIES.map((category) => ({
    category,
    items: skills.filter((skill) => skill.category === category),
  })).filter((group) => group.items.length > 0);

  return (
    <Section id="skills" title="Tecnologias" description="O que uso em cada camada de uma aplicação.">
      {groups.length === 0 ? (
        <EmptyState title="Nenhuma tecnologia cadastrada ainda" description="Elas aparecerão aqui assim que forem publicadas." />
      ) : (
        <dl className="divide-y divide-border border-y border-border">
          {groups.map(({ category, items }) => (
            <div key={category} className="grid gap-3 py-6 sm:grid-cols-[9.5rem_minmax(0,1fr)] sm:gap-8">
              <dt className="font-display text-lg font-medium sm:pt-1.5">{SKILL_CATEGORY_LABELS[category]}</dt>
              <dd>
                <ul className="flex flex-wrap gap-2">
                  {items.map((skill) => (
                    <li
                      key={skill.id}
                      className="inline-flex items-center gap-2 rounded-lg border border-border bg-card px-3 py-2 text-sm font-medium"
                    >
                      {skill.iconUrl ? (
                        // eslint-disable-next-line @next/next/no-img-element
                        <img src={skill.iconUrl} alt="" width={20} height={20} loading="lazy" className="h-5 w-5 object-contain" />
                      ) : (
                        <ToolIcon name={skill.name} className="h-5 w-5 text-primary" />
                      )}
                      {skill.name}
                    </li>
                  ))}
                </ul>
              </dd>
            </div>
          ))}
        </dl>
      )}
    </Section>
  );
}
