import { Profile } from "@/types";


/** Perfil padrão exibido quando a API está indisponível. O conteúdo real vem de GET /api/profile. */
export const defaultProfile: Profile = {
  fullName: "Seu Nome",
  role: "Desenvolvedor Full-Stack",
  headline: "Construo aplicações web completas, do banco de dados à interface.",
  bio: "Estudante de tecnologia em busca de oportunidade de estágio ou posição júnior.",
  about: "Edite este texto no painel administrativo (/admin/profile).",
  location: "Brasil",
  email: "voce@email.com",
  githubUrl: null,
  linkedinUrl: null,
  resumeUrl: null,
  avatarUrl: null,
  openToWork: true,
};

export const navLinks = [
  { href: "#sobre", label: "Sobre" },
  { href: "#cursos", label: "Cursos" },
  { href: "#skills", label: "Tecnologias" },
  { href: "#projetos", label: "Projetos" },
  { href: "#contato", label: "Contato" },
] as const;

export const SITE_URL = process.env.SITE_URL ?? "http://localhost:3000";
