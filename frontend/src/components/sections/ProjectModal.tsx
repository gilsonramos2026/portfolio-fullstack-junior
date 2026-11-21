"use client";

import { ArrowRight, ExternalLink, Github } from "lucide-react";
import { ButtonLink } from "@/components/ui/ButtonLink";
import { Modal } from "@/components/ui/Modal";
import { TechBadge } from "@/components/ui/TechBadge";
import { ProjectCover } from "@/components/sections/ProjectCard";
import type { Project } from "@/types";

interface ProjectModalProps {
  project: Project | null;
  onClose: () => void;
}

export function ProjectModal({ project, onClose }: ProjectModalProps) {
  return (
    <Modal open={project !== null} onClose={onClose} title={project?.title ?? ""} className="max-w-2xl">
      {project && (
        <div className="space-y-5">
          <ProjectCover project={project} className="aspect-video w-full rounded-xl object-cover" />
          <p className="whitespace-pre-line text-sm leading-relaxed text-muted-foreground">{project.description}</p>
          <ul className="flex flex-wrap gap-1.5" aria-label="Tecnologias">
            {project.technologies.map((tech) => (
              <li key={tech}>
                <TechBadge name={tech} />
              </li>
            ))}
          </ul>
          <div className="flex flex-wrap gap-3">
            <ButtonLink href={`/projetos/${project.id}`} variant="secondary">
              Página completa <ArrowRight className="h-4 w-4" aria-hidden />
            </ButtonLink>
            {project.githubUrl && (
              <ButtonLink href={project.githubUrl} variant="outline">
                <Github className="h-4 w-4" aria-hidden /> Repositório
              </ButtonLink>
            )}
            {project.deployUrl && (
              <ButtonLink href={project.deployUrl}>
                <ExternalLink className="h-4 w-4" aria-hidden /> Ver online
              </ButtonLink>
            )}
          </div>
        </div>
      )}
    </Modal>
  );
}
