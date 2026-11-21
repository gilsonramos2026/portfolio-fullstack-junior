import { z } from "zod";
import { EDUCATION_TYPES, SKILL_CATEGORIES } from "@/types";
import { isValidImageUrl } from "@/lib/upload";

const optionalUrl = z
  .string()
  .trim()
  .max(500, "Máximo de 500 caracteres")
  .refine((value) => value === "" || /^https?:\/\/.+/i.test(value), "Use uma URL iniciando com http:// ou https://");

/** Imagem: URL http(s) ou arquivo enviado pelo painel (/api/files/...). Vazio é permitido. */
const optionalImage = z
  .string()
  .trim()
  .max(500, "Máximo de 500 caracteres")
  .refine((value) => value === "" || isValidImageUrl(value), "Use uma URL http(s):// ou envie uma imagem");

/** Quebra um textarea em linhas não vazias (uma entrada por linha). */
export const splitLines = (value: string): string[] => value.split("\n").map((line) => line.trim()).filter(Boolean);

export const projectSchema = z.object({
  title: z.string().trim().min(1, "O título é obrigatório").max(120, "Máximo de 120 caracteres"),
  description: z.string().trim().min(1, "A descrição é obrigatória").max(2000, "Máximo de 2000 caracteres"),
  imageUrl: optionalImage,
  githubUrl: optionalUrl,
  deployUrl: optionalUrl,
  longDescription: z.string().trim().max(3000, "Máximo de 3000 caracteres"),
  featured: z.boolean(),
  features: z.string().refine((value) => splitLines(value).length <= 12, "Informe no máximo 12 funcionalidades")
    .refine((value) => splitLines(value).every((line) => line.length <= 200), "Cada funcionalidade deve ter até 200 caracteres"),
  screenshots: z.string()
    .refine((value) => splitLines(value).length <= 8, "Informe no máximo 8 capturas")
    .refine((value) => splitLines(value).every((line) => isValidImageUrl(line)), "Use uma URL por linha (http(s)://) ou envie imagens")
    .refine((value) => splitLines(value).every((line) => line.length <= 500), "Cada URL deve ter até 500 caracteres"),
  technologies: z.string()
    .refine((value) => value.split(",").map((t) => t.trim()).filter(Boolean).length <= 20, "Informe no máximo 20 tecnologias")
    .refine((value) => value.split(",").map((t) => t.trim()).every((t) => t.length <= 60), "Cada tecnologia deve ter até 60 caracteres"),
});
export type ProjectFormValues = z.infer<typeof projectSchema>;

export const skillSchema = z.object({
  name: z.string().trim().min(1, "O nome é obrigatório").max(80, "Máximo de 80 caracteres"),
  category: z.enum(SKILL_CATEGORIES, { errorMap: () => ({ message: "Selecione a categoria" }) }),
  level: z.coerce.number().int().min(1, "Mínimo 1").max(5, "Máximo 5"),
  iconUrl: optionalImage,
});
export type SkillFormValues = z.infer<typeof skillSchema>;

export const educationSchema = z
  .object({
    title: z.string().trim().min(1, "O título é obrigatório").max(120, "Máximo de 120 caracteres"),
    institution: z.string().trim().min(1, "A instituição é obrigatória").max(120, "Máximo de 120 caracteres"),
    type: z.enum(EDUCATION_TYPES, { errorMap: () => ({ message: "Selecione o tipo" }) }),
    description: z.string().trim().max(2000, "Máximo de 2000 caracteres"),
    startDate: z.string().min(1, "A data de início é obrigatória"),
    endDate: z.string(),
    credentialUrl: optionalUrl,
    workloadHours: z.string().trim().refine((value) => value === "" || (/^\d+$/.test(value) && Number(value) >= 1 && Number(value) <= 10000), "Informe horas entre 1 e 10000"),
  })
  .refine((data) => data.endDate === "" || data.endDate >= data.startDate, {
    path: ["endDate"],
    message: "A data de término não pode ser anterior ao início",
  });
export type EducationFormValues = z.infer<typeof educationSchema>;

export const loginSchema = z.object({
  username: z.string().trim().min(1, "Informe o usuário"),
  password: z.string().min(1, "Informe a senha"),
});
export type LoginFormValues = z.infer<typeof loginSchema>;

export const contactSchema = z.object({
  name: z.string().trim().min(1, "Informe seu nome").max(80, "Máximo de 80 caracteres"),
  email: z.string().trim().min(1, "Informe seu e-mail").email("E-mail inválido").max(160, "Máximo de 160 caracteres"),
  message: z.string().trim().min(10, "Escreva ao menos 10 caracteres").max(2000, "Máximo de 2000 caracteres"),
  website: z.string().max(200),
});
export type ContactFormValues = z.infer<typeof contactSchema>;

const requiredUrlOrEmpty = optionalUrl;

export const profileSchema = z.object({
  fullName: z.string().trim().min(1, "O nome é obrigatório").max(120, "Máximo de 120 caracteres"),
  role: z.string().trim().min(1, "O cargo é obrigatório").max(120, "Máximo de 120 caracteres"),
  headline: z.string().trim().min(1, "O resumo curto é obrigatório").max(300, "Máximo de 300 caracteres"),
  bio: z.string().trim().min(1, "A bio é obrigatória").max(1000, "Máximo de 1000 caracteres"),
  about: z.string().trim().min(1, "O texto sobre você é obrigatório").max(3000, "Máximo de 3000 caracteres"),
  location: z.string().trim().max(120, "Máximo de 120 caracteres"),
  email: z.string().trim().min(1, "O e-mail é obrigatório").email("E-mail inválido").max(160, "Máximo de 160 caracteres"),
  githubUrl: requiredUrlOrEmpty,
  linkedinUrl: requiredUrlOrEmpty,
  resumeUrl: requiredUrlOrEmpty,
  avatarUrl: optionalImage,
  openToWork: z.boolean(),
});
export type ProfileFormValues = z.infer<typeof profileSchema>;

export const passwordSchema = z
  .object({
    currentPassword: z.string().min(1, "Informe a senha atual"),
    newPassword: z.string().min(8, "Mínimo de 8 caracteres").max(100, "Máximo de 100 caracteres"),
    confirmPassword: z.string().min(1, "Confirme a nova senha"),
  })
  .refine((v) => v.newPassword === v.confirmPassword, { path: ["confirmPassword"], message: "As senhas não coincidem" })
  .refine((v) => v.newPassword !== v.currentPassword, { path: ["newPassword"], message: "A nova senha deve ser diferente da atual" });
export type PasswordFormValues = z.infer<typeof passwordSchema>;
