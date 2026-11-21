export const SKILL_CATEGORIES = ["BACKEND", "FRONTEND", "DATABASE", "DEVOPS"] as const;
export type SkillCategory = (typeof SKILL_CATEGORIES)[number];

export const SKILL_CATEGORY_LABELS: Record<SkillCategory, string> = {
  BACKEND: "Backend",
  FRONTEND: "Frontend",
  DATABASE: "Banco de Dados",
  DEVOPS: "DevOps",
};

export interface Skill {
  id: number;
  name: string;
  category: SkillCategory;
  level: number;
  iconUrl: string | null;
}

export interface SkillRequest {
  name: string;
  category: SkillCategory;
  level: number;
  iconUrl: string | null;
}