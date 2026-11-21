import "server-only";

const IP_LITERAL = /^[0-9a-fA-F:.]{2,45}$/;

/**
 * IP do visitante, para os limites de tentativas da API (login e recados).
 *
 * Só confia em X-Forwarded-For quando há proxies reversos conhecidos na frente do Next.js
 * (TRUSTED_PROXY_HOPS = quantidade deles). Cada proxy acrescenta o IP que viu ao final da lista, então o
 * valor confiável é o N-ésimo contando da direita; o que vem antes pode ter sido forjado pelo cliente.
 * Com 0 (padrão), o cabeçalho é ignorado e a API usa o IP da própria conexão.
 */
export function getClientIp(request: Request): string | null {
  const hops = Number.parseInt(process.env.TRUSTED_PROXY_HOPS ?? "0", 10);
  if (!Number.isInteger(hops) || hops < 1) return null;

  const chain = (request.headers.get("x-forwarded-for") ?? "")
    .split(",")
    .map((part) => part.trim())
    .filter(Boolean);
  const ip = chain[chain.length - hops];
  return ip && IP_LITERAL.test(ip) ? ip : null;
}

/** Cabeçalhos a repassar à API para que ela identifique o visitante. */
export function clientIpHeaders(request: Request): Record<string, string> {
  const ip = getClientIp(request);
  return ip ? { "X-Forwarded-For": ip } : {};
}
