"use client";

import { useState } from "react";
import { EmptyState } from "@/components/ui/EmptyState";
import { Reveal } from "@/components/ui/Reveal";
import { Section } from "@/components/ui/Section";
import { ProjectCard } from "@/components/sections/ProjectCard";
import { ProjectModal } from "@/components/sections/ProjectModal";
import type { Project } from "@/types";

export function Projects({ projects }: { projects: Project[] }) {
  const [selected, setSelected] = useState<Project | null>(null);
  // A API já entrega os destaques primeiro. O primeiro projeto destacado e, quando sobraria um card sozinho
  // na última linha, o último projeto ocupam a largura toda (capa ao lado do texto).
  const leadId = projects[0]?.featured ? projects[0].id : null;
  const others = projects.filter((project) => project.id !== leadId);
  const wideId = others.length % 2 === 1 ? others[others.length - 1]?.id : null;
  const isWide = (project: Project) => project.id === leadId || project.id === wideId;

  return (
    <Section id="projetos" title="Projetos" description="Aplicações que construí, com código aberto. Abra um projeto para ver detalhes e capturas de tela.">
      {projects.length === 0 ? (
        <EmptyState title="Nenhum projeto cadastrado ainda" description="Os projetos aparecerão aqui assim que forem publicados." />
      ) : (
        <ul className="grid gap-6 md:grid-cols-2">
          {projects.map((project, index) => (
            <li key={project.id} className={isWide(project) ? "md:col-span-2" : undefined}>
              <Reveal delay={index === 0 ? 0 : (index % 2) * 80} className="h-full">
                <ProjectCard project={project} onSelect={setSelected} layout={isWide(project) ? "feature" : "default"} />
              </Reveal>
            </li>
          ))}
        </ul>
      )}

      <ProjectModal project={selected} onClose={() => setSelected(null)} />
    </Section>
  );
}
