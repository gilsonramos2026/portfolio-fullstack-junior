"use client";

import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { InputField, SelectField } from "@/components/ui/FormField";
import { ImageField } from "@/components/admin/ImageField";
import { FormActions } from "@/components/admin/FormActions";
import type { CrudFormProps } from "@/components/admin/CrudManager";
import { skillSchema, type SkillFormValues } from "@/lib/schemas";
import { emptyToNull } from "@/lib/utils";
import { SKILL_CATEGORIES, SKILL_CATEGORY_LABELS, type Skill, type SkillRequest } from "@/types";

export function SkillForm({ initial, submitting, onSubmit, onCancel }: CrudFormProps<Skill, SkillRequest>) {
  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<SkillFormValues>({
    resolver: zodResolver(skillSchema),
    defaultValues: {
      name: initial?.name ?? "",
      category: initial?.category ?? "BACKEND",
      level: initial?.level ?? 3,
      iconUrl: initial?.iconUrl ?? "",
    },
  });

  return (
    <form
      onSubmit={handleSubmit((values) =>
        onSubmit({
          name: values.name.trim(),
          category: values.category,
          level: values.level,
          iconUrl: emptyToNull(values.iconUrl),
        }),
      )}
      className="space-y-4"
      noValidate
    >
      <InputField label="Nome" error={errors.name?.message} {...register("name")} />
      <div className="grid gap-4 sm:grid-cols-2">
        <SelectField label="Categoria" error={errors.category?.message} {...register("category")}>
          {SKILL_CATEGORIES.map((category) => (
            <option key={category} value={category}>
              {SKILL_CATEGORY_LABELS[category]}
            </option>
          ))}
        </SelectField>
        <SelectField label="Nível" error={errors.level?.message} {...register("level")}>
          {[1, 2, 3, 4, 5].map((level) => (
            <option key={level} value={level}>
              {level} de 5
            </option>
          ))}
        </SelectField>
      </div>
      <Controller
        control={control}
        name="iconUrl"
        render={({ field }) => <ImageField label="Ícone (opcional)" value={field.value} onChange={field.onChange} error={errors.iconUrl?.message} />}
      />
      <FormActions submitting={submitting} onCancel={onCancel} isEditing={initial !== null} />
    </form>
  );
}
