"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { InputField, SelectField, TextareaField } from "@/components/ui/FormField";
import { educationSchema, type EducationFormValues } from "@/lib/schemas";
import { emptyToNull } from "@/lib/utils";
import { EDUCATION_TYPES, EDUCATION_TYPE_LABELS, type Education, type EducationRequest } from "@/types";
import { FormActions } from "./ProjectForm";
import { CrudFormProps } from "./SkillForm";

export function EducationForm({
  initial,
  submitting,
  onSubmit,
  onCancel,
}: CrudFormProps<Education, EducationRequest>) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EducationFormValues>({
    resolver: zodResolver(educationSchema),
    defaultValues: {
      title: initial?.title ?? "",
      institution: initial?.institution ?? "",
      type: initial?.type ?? "DEGREE",
      description: initial?.description ?? "",
      startDate: initial?.startDate ?? "",
      endDate: initial?.endDate ?? "",
      credentialUrl: initial?.credentialUrl ?? "",
      workloadHours: initial?.workloadHours?.toString() ?? "",
    },
  });

  return (
    <form
      onSubmit={handleSubmit((values) =>
        onSubmit({
          title: values.title.trim(),
          institution: values.institution.trim(),
          type: values.type,
          description: emptyToNull(values.description),
          startDate: values.startDate,
          endDate: emptyToNull(values.endDate),
          credentialUrl: emptyToNull(values.credentialUrl),
          workloadHours: values.workloadHours ? Number(values.workloadHours) : null,
        }),
      )}
      className="space-y-4"
      noValidate
    >
      <InputField label="Curso / graduação" error={errors.title?.message} {...register("title")} />
      <InputField label="Instituição" error={errors.institution?.message} {...register("institution")} />
      <SelectField label="Tipo" error={errors.type?.message} {...register("type")}>
        {EDUCATION_TYPES.map((type) => (
          <option key={type} value={type}>
            {EDUCATION_TYPE_LABELS[type]}
          </option>
        ))}
      </SelectField>
      <div className="grid gap-4 sm:grid-cols-2">
        <InputField label="Início" type="date" error={errors.startDate?.message} {...register("startDate")} />
        <InputField
          label="Término"
          type="date"
          hint="Deixe vazio se ainda estiver em andamento"
          error={errors.endDate?.message}
          {...register("endDate")}
        />
      </div>
      <div className="grid gap-4 sm:grid-cols-2">
        <InputField label="Link do certificado" type="url" hint="Opcional, para cursos" error={errors.credentialUrl?.message} {...register("credentialUrl")} />
        <InputField label="Carga horária (h)" inputMode="numeric" hint="Opcional, para cursos" error={errors.workloadHours?.message} {...register("workloadHours")} />
      </div>
      <TextareaField label="Descrição" error={errors.description?.message} {...register("description")} />
      <FormActions submitting={submitting} onCancel={onCancel} isEditing={initial !== null} />
    </form>
  );
}
