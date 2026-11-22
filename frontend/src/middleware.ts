import { NextResponse, type NextRequest } from "next/server";

const TOKEN_COOKIE = "portfolio_token";

/** Decodifica o `exp` do JWT (sem verificar assinatura — a verificação real é feita pela API). */
function isExpired(token: string): boolean {
  try {
    const payload = token.split(".")[1];
    if (!payload) return true;
    const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
    return typeof json.exp !== "number" || json.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
}

/** Barreira rápida: bloqueia /admin/* sem token válido por prazo. A validação autoritativa ocorre no layout do admin. */
export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  if (pathname === "/admin/login") return NextResponse.next();

  const token = request.cookies.get(TOKEN_COOKIE)?.value;
  if (!token || isExpired(token)) {
    const url = request.nextUrl.clone();
    url.pathname = "/admin/login";
    url.search = "";
    return NextResponse.redirect(url);
  }
  return NextResponse.next();
}

export const config = {
  matcher: ["/admin/:path*"],
};
