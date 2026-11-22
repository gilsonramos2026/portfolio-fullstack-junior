"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Lock } from "lucide-react";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { InputField } from "@/components/ui/FormField";
import { getErrorMessage } from "@/lib/api-client";
import { loginSchema, type LoginFormValues } from "@/lib/schemas";

export function LoginForm({ reason }: { reason?: string }) {
  const router = useRouter();
  const [serverError, setServerError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema), defaultValues: { username: "", password: "" } });

  async function onSubmit(values: LoginFormValues) {
    setServerError(null);
    try {
      // Chamada ao Route Handler do Next.js, que grava o JWT em cookie httpOnly.
      await axios.post("/api/auth/login", values);
      router.replace("/admin");
      router.refresh();
    } catch (error) {
      setServerError(getErrorMessage(error));
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
      {reason === "expired" && !serverError && <Alert>Sua sessão expirou. Entre novamente.</Alert>}
      {reason === "password" && !serverError && <Alert>Senha alterada com sucesso. Entre novamente com a nova senha.</Alert>}
      {serverError && <Alert tone="error">{serverError}</Alert>}
      <InputField label="Usuário" autoComplete="username" error={errors.username?.message} {...register("username")} />
      <InputField
        label="Senha"
        type="password"
        autoComplete="current-password"
        error={errors.password?.message}
        {...register("password")}
      />
      <Button type="submit" className="w-full" loading={isSubmitting}>
        <Lock className="h-4 w-4" aria-hidden /> Entrar
      </Button>
    </form>
  );
}
