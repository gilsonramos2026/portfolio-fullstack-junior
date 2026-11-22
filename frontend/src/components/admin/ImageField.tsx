"use client";

import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckboxField, InputField, TextareaField } from "@/components/ui/FormField";
import { ImageField } from "@/components/admin/ImageField";
import { UploadButton } from "@/components/admin/UploadButton";
import { FormActions } from "@/components/admin/FormActions";
import type { CrudFormProps } from "@/components/admin/CrudManager";
import { projectSchema, type ProjectFormValues } from "@/lib/schemas";
import { splitLines } from "@/lib/schemas";
import { emptyToNull } from "@/lib/utils";
import type { Project, ProjectRequest } from "@/types";

function toDefaults(project: Project | null): ProjectFormValues {
  return {
    title: project?.title ?? "",
    description: project?.description ?? "",
    imageUrl: project?.imageUrl ?? "",
    githubUrl: project?.githubUrl ?? "",
    deployUrl: project?.deployUrl ?? "",
    longDescription: project?.longDescription ?? "",
    featured: project?.featured ?? false,
    features: project?.features.join("\n") ?? "",
    screenshots: project?.screenshots.join("\n") ?? "",
    technologies: project?.technologies.join(", ") ?? "",
  };
}

function toRequest(values: ProjectFormValues): ProjectRequest {
  return {
    title: values.title.trim(),
    description: values.description.trim(),
    imageUrl: emptyToNull(values.imageUrl),
    githubUrl: emptyToNull(values.githubUrl),
    deployUrl: emptyToNull(values.deployUrl),
    longDescription: emptyToNull(values.longDescription),
    featured: values.featured,
    features: splitLines(values.features),
    screenshots: splitLines(values.screenshots),
    technologies: values.technologies
      .split(",")
      .map((tech) => tech.trim())
      .filter(Boolean),
  };
}

export function ProjectForm({ initial, submitting, onSubmit, onCancel }: CrudFormProps<Project, ProjectRequest>) {
  const {
    register,
    control,
    getValues,
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm<ProjectFormValues>({ resolver: zodResolver(projectSchema), defaultValues: toDefaults(initial) });

  return (
    <form onSubmit={handleSubmit((values) => onSubmit(toRequest(values)))} className="space-y-4" noValidate>
      <InputField label="Título" error={errors.title?.message} {...register("title")} />
      <TextareaField label="Descrição" error={errors.description?.message} {...register("description")} />
      <InputField
        label="Tecnologias"
        hint="Separe por vírgula. Ex.: Java, Spring Boot, PostgreSQL"
        error={errors.technologies?.message}
        {...register("technologies")}
      />
      <Controller
        control={control}
        name="imageUrl"
        render={({ field }) => <ImageField label="Imagem de capa" value={field.value} onChange={field.onChange} error={errors.imageUrl?.message} />}
      />
      <div className="grid gap-4 sm:grid-cols-2">
        <InputField label="Link do GitHub" type="url" error={errors.githubUrl?.message} {...register("githubUrl")} />
        <InputField label="Link do deploy" type="url" error={errors.deployUrl?.message} {...register("deployUrl")} />
      </div>
      <TextareaField
        label="Descrição detalhada"
        hint="Exibida na página do projeto. Separe parágrafos com uma linha em branco."
        className="min-h-36"
        error={errors.longDescription?.message}
        {...register("longDescription")}
      />
      <TextareaField
        label="Funcionalidades"
        hint="Uma por linha (até 12)"
        className="min-h-24"
        error={errors.features?.message}
        {...register("features")}
      />
      <div className="space-y-2">
        <TextareaField
          label="Capturas de tela"
          hint="Uma URL por linha (até 8). Cole links ou envie vários arquivos de uma vez; se passar de 8, o formulário avisa."
          className="min-h-24"
          error={errors.screenshots?.message}
          {...register("screenshots")}
        />
        <UploadButton
          label="Enviar capturas"
          multiple
          onUploaded={(url) => {
            const current = getValues("screenshots").trim();
            setValue("screenshots", current ? `${current}\n${url}` : url, { shouldDirty: true, shouldValidate: true });
          }}
        />
      </div>
      <CheckboxField label="Exibir em destaque" {...register("featured")} />
      <FormActions submitting={submitting} onCancel={onCancel} isEditing={initial !== null} />
    </form>
  );
}
