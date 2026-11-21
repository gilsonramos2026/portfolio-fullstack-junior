"use client";

import { useEffect, useState } from "react";
import { Moon, Sun } from "lucide-react";
import { useTheme } from "next-themes";
import { IconButton } from "@/components/ui/IconButton";

export function ThemeToggle() {
  const { resolvedTheme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  const isDark = mounted && resolvedTheme === "dark";

  return (
    <IconButton
      label={isDark ? "Ativar tema claro" : "Ativar tema escuro"}
      variant="outline"
      onClick={() => {
        const root = document.documentElement;
        root.classList.add("theme-switching");
        window.setTimeout(() => root.classList.remove("theme-switching"), 300);
        setTheme(isDark ? "light" : "dark");
      }}
    >
      {isDark ? <Sun className="h-4 w-4" aria-hidden /> : <Moon className="h-4 w-4" aria-hidden />}
    </IconButton>
  );
}
