import { adminApi } from "@/lib/api-client";

export const IMAGE_ACCEPT = "image/png,image/jpeg,image/webp,image/gif";
export const MAX_IMAGE_BYTES = 5 * 1024 * 1024;
const ALLOWED_TYPES = new Set(IMAGE_ACCEPT.split(","));

/** Imagem enviada pelo painel: o backend devolve /api/files/<uuid>.<ext>. */
export const LOCAL_IMAGE_RE = /^\/api\/files\/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\.(png|jpg|gif|webp)$/;
export const isValidImageUrl = (value: string): boolean => /^https?:\/\/.+/i.test(value) || LOCAL_IMAGE_RE.test(value);

/** Validação rápida no navegador; o backend valida de novo pelo conteúdo real do arquivo. */
export function validateImageFile(file: File): string | null {
  if (!ALLOWED_TYPES.has(file.type)) return "Formato não suportado. Envie PNG, JPEG, WebP ou GIF.";
  if (file.size > MAX_IMAGE_BYTES) return "A imagem deve ter no máximo 5 MB.";
  return null;
}

export async function uploadImage(file: File): Promise<string> {
  const body = new FormData();
  body.append("file", file);
  const { data } = await adminApi.post<{ url: string }>("/uploads", body, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 60_000,
  });
  return data.url;
}
