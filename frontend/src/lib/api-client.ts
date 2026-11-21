import axios, { AxiosError } from "axios";
import type { ApiError } from "@/types";

/**
 * Cliente usado pelo painel admin. Aponta para o proxy do Next.js (/api/admin),
 * que lê o JWT do cookie httpOnly e o encaminha à API. O token nunca fica acessível ao JavaScript.
 */
export const adminApi = axios.create({
  baseURL: "/api/admin",
  headers: { "Content-Type": "application/json", Accept: "application/json" },
  timeout: 15_000,
});

adminApi.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401 && typeof window !== "undefined") {
      window.location.assign("/admin/login?reason=expired");
    }
    return Promise.reject(error);
  },
);

/** Cliente das rotas públicas do próprio Next.js (sem autenticação), ex.: envio de contato. */
export const publicApi = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json", Accept: "application/json" },
  timeout: 15_000,
});

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiError>(error)) {
    const data = error.response?.data;
    if (data?.fieldErrors?.length) {
      return data.fieldErrors.map((f) => f.message).join(" • ");
    }
    if (data?.message) return data.message;
    if (!error.response) return "Não foi possível conectar ao servidor.";
  }
  return "Ocorreu um erro inesperado. Tente novamente.";
}
