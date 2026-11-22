"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { InputField } from "@/components/ui/FormField";
import { adminApi, getErrorMessage } from "@/lib/api-client";
import { passwordSchema, type PasswordFormValues } from "@/lib/schemas";

export function PasswordForm() {
  const [serverError, setServerError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<PasswordFormValues>({
    resolver: zodResolver(passwordSchema),
    defaultValues: { currentPassword: "", newPassword: "", confirmPassword: "" },
  });

  async function onSubmit({ currentPassword, newPassword }: PasswordFormValues) {
    setServerError(null);
    try {
      await adminApi.put("/auth/password", { currentPassword, newPassword });
      // O backend invalida os tokens antigos ao trocar a senha: encerra a sessão aqui e pede um novo login.
      reset();
      await fetch("/api/auth/logout", { method: "POST" }).catch(() => undefined);
      window.location.assign("/admin/login?reason=password");
      return;
    } catch (error) {
      setServerError(getErrorMessage(error));
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="max-w-md space-y-4" noValidate>
      {serverError && <Alert tone="error">{serverError}</Alert>}
      <InputField label="Senha atual" type="password" autoComplete="current-password" error={errors.currentPassword?.message} {...register("currentPassword")} />
      <InputField label="Nova senha" type="password" autoComplete="new-password" hint="Mínimo de 8 caracteres." error={errors.newPassword?.message} {...register("newPassword")} />
      <InputField label="Confirmar nova senha" type="password" autoComplete="new-password" error={errors.confirmPassword?.message} {...register("confirmPassword")} />
      <Button type="submit" loading={isSubmitting}>Alterar senha</Button>
    </form>
  );
}
