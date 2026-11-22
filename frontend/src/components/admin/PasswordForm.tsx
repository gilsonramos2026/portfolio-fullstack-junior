"use client";

import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { ImageField } from "@/components/admin/ImageField";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { CheckboxField, InputField, TextareaField } from "@/components/ui/FormField";
import { EmptyState } from "@/components/ui/EmptyState";
import { Spinner } from "@/components/ui/Spinner";
import { adminApi, getErrorMessage } from "@/lib/api-client";
import { profileSchema, type ProfileFormValues } from "@/lib/schemas";
import { emptyToNull } from "@/lib/utils";
import type { Profile, ProfileRequest } from "@/types";

const QUERY_KEY = ["admin", "profile"] as const;

function toForm(profile: Profile): ProfileFormValues {
  return {
    fullName: profile.fullName,
    role: profile.role,
    headline: profile.headline,
    bio: profile.bio,
    about: profile.about,
    location: profile.location ?? "",
    email: profile.email,
    githubUrl: profile.githubUrl ?? "",
    linkedinUrl: profile.linkedinUrl ?? "",
    resumeUrl: profile.resumeUrl ?? "",
    avatarUrl: profile.avatarUrl ?? "",
    openToWork: profile.openToWork,
  };
}

function toRequest(values: ProfileFormValues): ProfileRequest {
  return {
    fullName: values.fullName.trim(),
    role: values.role.trim(),
    headline: values.headline.trim(),
    bio: values.bio.trim(),
    about: values.about.trim(),
    location: emptyToNull(values.location),
    email: values.email.trim(),
    githubUrl: emptyToNull(values.githubUrl),
    linkedinUrl: emptyToNull(values.linkedinUrl),
    resumeUrl: emptyToNull(values.resumeUrl),
    avatarUrl: emptyToNull(values.avatarUrl),
    openToWork: values.openToWork,
  };
}

export function ProfileForm() {
  const queryClient = useQueryClient();
  const query = useQuery({
    queryKey: QUERY_KEY,
    queryFn: async () => (await adminApi.get<Profile>("/profile")).data,
  });

  const save = useMutation({
    mutationFn: async (payload: ProfileRequest) => (await adminApi.put<Profile>("/profile", payload)).data,
    onSuccess: (profile) => {
      queryClient.setQueryData(QUERY_KEY, profile);
      toast.success("Perfil atualizado. O site público reflete a mudança em até 1 minuto.");
    },
    onError: (error) => toast.error(getErrorMessage(error)),
  });

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors, isDirty },
  } = useForm<ProfileFormValues>({ resolver: zodResolver(profileSchema) });

  useEffect(() => {
    if (query.data) reset(toForm(query.data));
  }, [query.data, reset]);

  if (query.isPending) return <Spinner className="py-10" />;
  if (query.isError) {
    return (
      <EmptyState
        title="Não foi possível carregar o perfil"
        description={getErrorMessage(query.error)}
        action={<Button variant="outline" onClick={() => query.refetch()}>Tentar novamente</Button>}
      />
    );
  }

  return (
    <form onSubmit={handleSubmit((values) => save.mutate(toRequest(values)))} noValidate className="space-y-6">
      <Card className="space-y-4 p-6">
        <h2 className="font-semibold">Identidade</h2>
        <div className="grid gap-4 sm:grid-cols-2">
          <InputField label="Nome completo" error={errors.fullName?.message} {...register("fullName")} />
          <InputField label="Cargo / título" error={errors.role?.message} {...register("role")} />
        </div>
        <Controller
          control={control}
          name="avatarUrl"
          render={({ field }) => (
            <ImageField
              label="Foto de perfil"
              hint="Imagem quadrada. Cole um link público ou envie um arquivo (PNG, JPEG, WebP ou GIF, até 5 MB)."
              value={field.value}
              onChange={field.onChange}
              error={errors.avatarUrl?.message}
            />
          )}
        />
        <InputField label="Resumo curto" hint="Frase de impacto exibida no Hero" error={errors.headline?.message} {...register("headline")} />
        <TextareaField label="Bio" error={errors.bio?.message} {...register("bio")} />
        <TextareaField
          label="Sobre mim"
          hint="Separe parágrafos com uma linha em branco"
          className="min-h-44"
          error={errors.about?.message}
          {...register("about")}
        />
      </Card>

      <Card className="space-y-4 p-6">
        <h2 className="font-semibold">Contato e links</h2>
        <div className="grid gap-4 sm:grid-cols-2">
          <InputField label="E-mail" type="email" error={errors.email?.message} {...register("email")} />
          <InputField label="Localização" error={errors.location?.message} {...register("location")} />
          <InputField label="GitHub" type="url" error={errors.githubUrl?.message} {...register("githubUrl")} />
          <InputField label="LinkedIn" type="url" error={errors.linkedinUrl?.message} {...register("linkedinUrl")} />
        </div>
        <InputField
          label="Link do currículo (PDF)"
          type="url"
          hint="Ex.: link público do Google Drive. Habilita o botão “Currículo” no site."
          error={errors.resumeUrl?.message}
          {...register("resumeUrl")}
        />
        <CheckboxField label="Exibir selo “Aberto a estágio e vagas júnior”" {...register("openToWork")} />
      </Card>

      <div className="flex justify-end">
        <Button type="submit" loading={save.isPending} disabled={!isDirty}>
          Salvar alterações
        </Button>
      </div>
    </form>
  );
}
