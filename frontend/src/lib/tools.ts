import { TOOL_ICON_PATHS } from '@/lib/tool-icon-paths';

import type { Skill } from "@/types";

const ALIASES: Record<string, string> = {
  postgres: "postgresql",
  psql: "postgresql",
  js: "javascript",
  ts: "typescript",
  tailwind: "tailwindcss",
  html5: "html",
  css3: "css",
  node: "nodejs",
  reactjs: "react",
  next: "nextjs",
  vuejs: "vue",
  angularjs: "angular",
  springframework: "spring",
  jsonwebtoken: "jwt",
  jsonwebtokens: "jwt",
  junit5: "junit",
  amazonwebservices: "aws",
  k8s: "kubernetes",
  gitgithub: "git",
  intellij: "intellijidea",
  openapi: "swagger",
};

const normalize = (value: string) => value.toLowerCase().replace(/[^a-z0-9]/g, "");

/** Devolve o caminho SVG (viewBox 24) da ferramenta, ou null se não houver ícone conhecido. */
export function resolveToolIcon(name: string): string | null {
  const candidates = [name, ...name.split(/[&/,+]|\be\b/i)].map(normalize).filter(Boolean);
  for (const candidate of candidates) {
    const key = ALIASES[candidate] ?? candidate;
    const path = TOOL_ICON_PATHS[key];
    if (path) return path;
  }
  return null;
}

/** Frases rotativas do efeito de digitação, derivadas das skills cadastradas. */
export function buildTypingPhrases(role: string, skills: Skill[]): string[] {
  const top = (category: Skill["category"]) =>
    skills
      .filter((skill) => skill.category === category)
      .sort((a, b) => b.level - a.level)
      .slice(0, 2)
      .map((skill) => skill.name);

  const phrases = [role];
  const backend = top("BACKEND");
  const frontend = top("FRONTEND");
  const database = top("DATABASE");
  if (backend.length) phrases.push(`Backend com ${backend.join(" e ")}`);
  if (frontend.length) phrases.push(`Frontend com ${frontend.join(" e ")}`);
  if (database.length) phrases.push(`Dados com ${database.join(" e ")}`);
  return phrases;
}
