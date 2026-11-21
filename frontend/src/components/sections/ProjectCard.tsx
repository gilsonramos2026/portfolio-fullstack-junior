import { Github, ExternalLink, Star } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { ButtonLink } from "@/components/ui/ButtonLink";
import { MediaButton } from "@/components/ui/MediaButton";
import { ToolIcon } from "@/components/ui/ToolIcon";
import { cn } from "@/lib/utils";
import type { Project } from "@/types";


interface ProjectCoverProps {
  project: Project;
  className?: string;
  priority?: boolean;
}

/** Capa do projeto: imagem com dimensões reservadas (evita salto de layout) ou placeholder com a inicial. */
export function ProjectCover({ project, className, priority = false }: ProjectCoverProps) {
  return project.imageUrl ? (
    // eslint-disable-next-line @next/next/no-img-element
    <img
      src={project.imageUrl}
      alt={`Capa do projeto ${project.title}`}
      width={1280}
      height={720}
      loading={priority ? "eager" : "lazy"}
      fetchPriority={priority ? "high" : "auto"}
      decoding="async"
      className={className}
    />
  ) : (
    <div
      aria-hidden
      className={cn(
        "flex items-center justify-center bg-muted font-display text-6xl font-semibold text-muted-foreground/60",
        // grade de pontos discreta: o espaço sem imagem parece intencional, não quebrado
        "[background-image:radial-gradient(hsl(var(--border))_1.2px,transparent_1.2px)] [background-size:16px_16px]",
        className,
      )}
    >
      {project.title.charAt(0).toUpperCase()}
    </div>
  );
}

interface ProjectCardProps {
  project: Project;
  onSelect: (project: Project) => void;
  /** "feature" = capa à esquerda e texto à direita (largura total em telas médias ou maiores). */
  layout?: "default" | "feature";
}

export function ProjectCard({ project, onSelect, layout = "default" }: ProjectCardProps) {
  const feature = layout === "feature";

  return (
    <article
      className={cn(
        "group flex h-full flex-col overflow-hidden rounded-xl border border-border bg-card transition-colors hover:border-primary/50",
        feature && "md:grid md:grid-cols-2",
      )}
    >
      <MediaButton onClick={() => onSelect(project)} aria-label={`Ver detalhes de ${project.title}`} className={feature ? "md:h-full" : undefined}>
        <ProjectCover
          project={project}
          priority={feature}
          className={cn(
            "aspect-[16/10] w-full object-cover object-left-top transition-transform duration-500 group-hover:scale-[1.02] motion-reduce:transition-none motion-reduce:group-hover:scale-100",
            feature && "md:h-full md:aspect-auto",
          )}
        />
      </MediaButton>

      <div className={cn("flex flex-1 flex-col gap-4 p-5 sm:p-6", feature && "md:justify-center md:p-6")}>
        <div>
          {project.featured && (
            <p className="mb-2 inline-flex items-center gap-1.5 text-sm font-medium text-accent">
              <Star className="h-3.5 w-3.5" aria-hidden /> Em destaque
            </p>
          )}
          <h3 className={cn("font-semibold", feature ? "text-2xl sm:text-3xl" : "text-xl")}>{project.title}</h3>
        </div>
        <p className={cn("text-muted-foreground", feature ? "line-clamp-5" : "line-clamp-3")}>{project.description}</p>

        <ul aria-label="Tecnologias" className="flex flex-wrap gap-x-4 gap-y-1.5 text-sm text-muted-foreground">
          {project.technologies.map((tech) => (
            <li key={tech} className="inline-flex items-center gap-1.5">
              <ToolIcon name={tech} className="h-4 w-4" />
              {tech}
            </li>
          ))}
        </ul>

        <div className="mt-auto flex flex-wrap items-center gap-2 pt-2">
          <Button size="sm" onClick={() => onSelect(project)} aria-label={`Detalhes de ${project.title}`}>
            Ver detalhes
          </Button>
          {project.githubUrl && (
            <ButtonLink href={project.githubUrl} variant="outline" size="sm">
              <Github className="h-4 w-4" aria-hidden /> Código
            </ButtonLink>
          )}
          {project.deployUrl && (
            <ButtonLink href={project.deployUrl} variant="outline" size="sm">
              <ExternalLink className="h-4 w-4" aria-hidden /> Demo
            </ButtonLink>
          )}
        </div>
      </div>
    </article>
  );
}
