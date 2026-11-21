/**
 * Cabeçalhos de segurança do site (aplicados a todas as rotas por next.config.mjs).
 *
 * CSP — decisões:
 * - O navegador só fala com a própria origem (/api/*, que o Next encaminha à API): connect-src 'self'.
 * - Imagens podem vir de qualquer http(s) porque o admin cadastra URLs de imagem (capas, fotos, ícones).
 * - script-src mantém 'unsafe-inline': o Next injeta scripts inline de hidratação. Trocar por nonce exigiria
 *   renderizar todas as páginas dinamicamente (perde o cache estático); fica como evolução possível.
 *   Mesmo assim, objetos, <base>, formulários externos e iframes ficam bloqueados.
 * - 'unsafe-eval' e WebSocket só em desenvolvimento (React Refresh).
 */
export function buildSecurityHeaders({ isDev = false } = {}) {
  const csp = [
    "default-src 'self'",
    `script-src 'self' 'unsafe-inline'${isDev ? " 'unsafe-eval'" : ""}`,
    "style-src 'self' 'unsafe-inline'",
    "img-src 'self' data: blob: https: http:",
    "font-src 'self' data:",
    `connect-src 'self'${isDev ? " ws:" : ""}`,
    "media-src 'self'",
    "object-src 'none'",
    "base-uri 'self'",
    "form-action 'self'",
    "frame-ancestors 'none'",
    "manifest-src 'self'",
  ].join("; ");

  const headers = [
    { key: "Content-Security-Policy", value: csp },
    { key: "X-Content-Type-Options", value: "nosniff" },
    { key: "X-Frame-Options", value: "DENY" },
    { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
    { key: "Permissions-Policy", value: "camera=(), microphone=(), geolocation=(), payment=(), usb=(), interest-cohort=()" },
    { key: "Cross-Origin-Opener-Policy", value: "same-origin" },
    { key: "X-DNS-Prefetch-Control", value: "off" },
  ];

  // HSTS só em produção. Sem includeSubDomains/preload de propósito: são compromissos difíceis de desfazer.
  if (!isDev) {
    headers.push({ key: "Strict-Transport-Security", value: "max-age=31536000" });
  }
  return headers;
}
