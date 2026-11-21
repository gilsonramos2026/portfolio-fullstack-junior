import "server-only";
import { cookies } from "next/headers";
import { API_URL } from "@/lib/api-server";

export const TOKEN_COOKIE = "portfolio_token";

export interface AdminSession {
  username: string;
}

/** Valida o token do cookie httpOnly diretamente na API (fonte da verdade). */
export async function getAdminSession(): Promise<AdminSession | null> {
  const token = (await cookies()).get(TOKEN_COOKIE)?.value;
  if (!token) return null;

  try {
    const response = await fetch(`${API_URL}/api/auth/me`, {
      headers: { Authorization: `Bearer ${token}`, Accept: "application/json" },
      cache: "no-store",
    });
    if (!response.ok) return null;
    return (await response.json()) as AdminSession;
  } catch {
    return null;
  }
}
