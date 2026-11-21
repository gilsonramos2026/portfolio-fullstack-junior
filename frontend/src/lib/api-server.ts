import "server-only";
import { defaultProfile } from "@/lib/site";
import type { Education, Profile, Project, Skill } from "@/types";

export const API_URL = process.env.API_URL ?? "http://localhost:8080";

/**
 * GET público (sem autenticação) executado no servidor Next.js, com cache ISR de 60s.
 * Em caso de falha devolve lista vazia para que a home continue renderizando.
 */
async function fetchList<T>(path: string): Promise<T[]> {
  try {
    const response = await fetch(`${API_URL}${path}`, {
      headers: { Accept: "application/json" },
      next: { revalidate: 60 },
    });
    if (!response.ok) {
      console.error(`[api] ${path} respondeu ${response.status}`);
      return [];
    }
    return (await response.json()) as T[];
  } catch (error) {
    console.error(`[api] falha ao consultar ${path}`, error);
    return [];
  }
}

export const getProjects = () => fetchList<Project>("/api/projects");
export const getSkills = () => fetchList<Skill>("/api/skills");
export const getEducation = () => fetchList<Education>("/api/education");

export async function getProfile(): Promise<Profile> {
  try {
    const response = await fetch(`${API_URL}/api/profile`, {
      headers: { Accept: "application/json" },
      next: { revalidate: 60 },
    });
    if (!response.ok) {
      console.error(`[api] /api/profile respondeu ${response.status}`);
      return defaultProfile;
    }
    return (await response.json()) as Profile;
  } catch (error) {
    console.error("[api] falha ao consultar /api/profile", error);
    return defaultProfile;
  }
}

/** Busca um projeto pelo id; devolve null se não existir (ou se a API estiver indisponível). */
export async function getProject(id: string): Promise<Project | null> {
  if (!/^\d+$/.test(id)) return null;
  try {
    const response = await fetch(`${API_URL}/api/projects/${id}`, {
      headers: { Accept: "application/json" },
      next: { revalidate: 60 },
    });
    if (!response.ok) return null;
    return (await response.json()) as Project;
  } catch (error) {
    console.error(`[api] falha ao consultar /api/projects/${id}`, error);
    return null;
  }
}
