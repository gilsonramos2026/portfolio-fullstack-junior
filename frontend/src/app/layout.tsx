import type { Metadata, Viewport } from "next";
import type { ReactNode } from "react";
import { getProfile } from "@/lib/api-server";
import "@fontsource-variable/bricolage-grotesque/opsz.css";
import "@fontsource-variable/instrument-sans/wght.css";
import { Providers } from "@/components/layout/Providers";
import { SITE_URL } from "@/lib/site";

export async function generateMetadata(): Promise<Metadata> {
  const profile = await getProfile();
  const title = `${profile.fullName} — ${profile.role}`;
  return {
    metadataBase: new URL(SITE_URL),
    title: { default: title, template: `%s | ${profile.fullName}` },
    description: `${profile.headline} ${profile.bio}`.slice(0, 300),
    openGraph: { title, description: profile.headline, type: "website", locale: "pt_BR", url: SITE_URL },
    twitter: { card: "summary_large_image", title, description: profile.headline },
  };
}

export const viewport: Viewport = {
  themeColor: [
    { media: "(prefers-color-scheme: light)", color: "#fbfaf7" },
    { media: "(prefers-color-scheme: dark)", color: "#0e1118" },
  ],
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="pt-BR" suppressHydrationWarning>
      <body>
        {/* Sem JavaScript, o conteúdo animado por scroll permanece visível. */}
        <noscript>
          <style>{`.reveal{opacity:1!important;transform:none!important}`}</style>
        </noscript>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
