"use client";

import { useEffect, useState } from "react";

interface TypewriterProps {
  phrases: string[];
  typingMs?: number;
  deletingMs?: number;
  pauseMs?: number;
  className?: string;
}

/** Efeito de máquina de escrever que alterna entre frases. Sem animação com prefers-reduced-motion. */
export function Typewriter({ phrases, typingMs = 70, deletingMs = 35, pauseMs = 1600, className }: TypewriterProps) {
  const [index, setIndex] = useState(0);
  const [text, setText] = useState("");
  const [deleting, setDeleting] = useState(false);
  const [reduceMotion, setReduceMotion] = useState(false);

  useEffect(() => {
    setReduceMotion(window.matchMedia("(prefers-reduced-motion: reduce)").matches);
  }, []);

  useEffect(() => {
    if (reduceMotion || phrases.length === 0) return;
    const current = phrases[index % phrases.length] ?? "";

    let delay = deleting ? deletingMs : typingMs;
    let next: () => void;

    if (!deleting && text === current) {
      delay = pauseMs;
      next = () => (phrases.length > 1 ? setDeleting(true) : undefined);
    } else if (deleting && text === "") {
      delay = 300;
      next = () => {
        setDeleting(false);
        setIndex((value) => (value + 1) % phrases.length);
      };
    } else {
      next = () => setText(deleting ? current.slice(0, text.length - 1) : current.slice(0, text.length + 1));
    }

    const timer = window.setTimeout(next, delay);
    return () => window.clearTimeout(timer);
  }, [text, deleting, index, phrases, reduceMotion, typingMs, deletingMs, pauseMs]);

  const first = phrases[0] ?? "";

  return (
    <span className={className}>
      {/* Texto estático para leitores de tela e SEO; a animação é decorativa. */}
      <span className="sr-only">{first}</span>
      <span aria-hidden>
        {reduceMotion ? first : text}
        <span className="ml-0.5 inline-block h-[1em] w-[3px] translate-y-[0.15em] bg-accent motion-safe:animate-blink" />
      </span>
    </span>
  );
}
