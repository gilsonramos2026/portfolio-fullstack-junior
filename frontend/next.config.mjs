import { buildSecurityHeaders } from "./security-headers.mjs";

/** @type {import('next').NextConfig} */
const nextConfig = {
  output: "standalone",
  reactStrictMode: true,
  poweredByHeader: false,
  async headers() {
    return [{ source: "/:path*", headers: buildSecurityHeaders({ isDev: process.env.NODE_ENV !== "production" }) }];
  },
};

export default nextConfig;
