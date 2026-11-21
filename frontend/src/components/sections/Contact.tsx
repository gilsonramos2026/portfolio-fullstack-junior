"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CheckCircle2, Github, Linkedin, Mail, Send } from "lucide-react";
import { Alert } from "@/components/ui/Alert";
import { Button } from "@/components/ui/Button";
import { CopyButton } from "@/components/ui/CopyButton";
import { Section } from "@/components/ui/Section";
import { Card } from "@/components/ui/Card";
import { InputField, TextareaField } from "@/components/ui/FormField";
import { Reveal } from "@/components/ui/Reveal";
import { getErrorMessage, publicApi } from "@/lib/api-client";
import { contactSchema, type ContactFormValues } from "@/lib/schemas";
import type { MessageRequest, Profile } from "@/types";

export function Contact({ profile }: { profile: Profile }) {
  const [sent, setSent] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ContactFormValues>({
    resolver: zodResolver(contactSchema),
    defaultValues: { name: "", email: "", message: "", website: "" },
  });

  const channels = [
    { label: "E-mail", value: profile.email, href: `mailto:${profile.email}`, icon: Mail },
    profile.linkedinUrl && { label: "LinkedIn", value: "Conectar no LinkedIn", href: profile.linkedinUrl, icon: Linkedin },
    profile.githubUrl && { label: "GitHub", value: "Ver repositórios", href: profile.githubUrl, icon: Github },
  ].filter((item): item is { label: string; value: string; href: string; icon: typeof Mail } => Boolean(item));

  async function onSubmit(values: ContactFormValues) {
    setServerError(null);
    try {
      const payload: MessageRequest = { ...values, source: "FORM" };
      await publicApi.post("/contact", payload);
      setSent(true);
      reset();
    } catch (error) {
      setServerError(getErrorMessage(error));
    }
  }

  return (
    <Section
      id="contato"
      title="Vamos conversar?"
      description="Estou disponível para vagas de estágio e posições júnior. Envie uma mensagem e eu respondo por e-mail."
    >
      <div className="grid gap-10 md:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)] md:gap-12">
        <Reveal>
          <ul className="divide-y divide-border border-y border-border">
            {channels.map(({ label, value, href, icon: Icon }) => (
              <li key={label} className="flex items-center gap-2">
                <a
                  href={href}
                  target={href.startsWith("http") ? "_blank" : undefined}
                  rel="noopener noreferrer"
                  className="group flex min-w-0 flex-1 items-center gap-4 py-4 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
                >
                  <Icon className="h-5 w-5 shrink-0 text-primary" aria-hidden />
                  <span className="min-w-0">
                    <span className="block text-sm text-muted-foreground">{label}</span>
                    <span className="break-all font-medium underline-offset-4 group-hover:underline">{value}</span>
                  </span>
                </a>
                {label === "E-mail" && <CopyButton text={value} label="Copiar e-mail" />}
              </li>
            ))}
          </ul>
        </Reveal>

        <Reveal delay={100}>
          <Card className="p-6">
            {sent ? (
              <div role="status" className="flex flex-col items-center gap-3 py-10 text-center">
                <CheckCircle2 className="h-10 w-10 text-accent" aria-hidden />
                <p className="text-lg font-semibold">Mensagem enviada</p>
                <p className="text-sm text-muted-foreground">Obrigado pelo contato. Respondo em breve no e-mail informado.</p>
                <Button variant="outline" onClick={() => setSent(false)}>
                  Enviar outra mensagem
                </Button>
              </div>
            ) : (
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" noValidate>
                {serverError && <Alert tone="error">{serverError}</Alert>}
                <div className="grid gap-4 sm:grid-cols-2">
                  <InputField label="Seu nome" autoComplete="name" error={errors.name?.message} {...register("name")} />
                  <InputField label="Seu e-mail" type="email" autoComplete="email" error={errors.email?.message} {...register("email")} />
                </div>
                <TextareaField label="Mensagem" error={errors.message?.message} {...register("message")} />
                {/* Honeypot: invisível para pessoas, bots costumam preencher. */}
                <div aria-hidden className="absolute -left-[9999px] h-0 w-0 overflow-hidden">
                  <label>
                    Não preencha
                    <input type="text" tabIndex={-1} autoComplete="off" {...register("website")} />
                  </label>
                </div>
                <Button type="submit" className="w-full" loading={isSubmitting}>
                  <Send className="h-4 w-4" aria-hidden /> Enviar mensagem
                </Button>
              </form>
            )}
          </Card>
        </Reveal>
      </div>
    </Section>
  );
}
