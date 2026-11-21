"use client";

import { useCallback, useEffect, useRef, useState, type FormEvent } from "react";
import { Bot, MessageCircle, Send, X } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { FilterChip } from "@/components/ui/FilterChip";
import { IconButton } from "@/components/ui/IconButton";
import { getErrorMessage, publicApi } from "@/lib/api-client";
import { cn } from "@/lib/utils";
import {
  EDUCATION_TYPE_LABELS,
  SKILL_CATEGORIES,
  SKILL_CATEGORY_LABELS,
  type Education,
  type MessageRequest,
  type Profile,
  type Project,
  type Skill,
} from "@/types";

interface ChatWidgetProps {
  profile: Profile;
  skills: Skill[];
  projects: Project[];
  education: Education[];
}

interface ChatLink {
  label: string;
  href: string;
}

interface ChatMessage {
  id: number;
  from: "bot" | "user";
  text: string;
  links?: ChatLink[];
}

type Step = "menu" | "name" | "email" | "message" | "sending";

interface Draft {
  name: string;
  email: string;
  message: string;
}

type Intent = "skills" | "projects" | "education" | "courses" | "availability" | "resume" | "contact";

const QUICK_REPLIES: { intent: Intent; label: string }[] = [
  { intent: "skills", label: "Quais tecnologias você usa?" },
  { intent: "projects", label: "Mostre seus projetos" },
  { intent: "education", label: "Formação acadêmica" },
  { intent: "courses", label: "Cursos e certificações" },
  { intent: "availability", label: "Está disponível para vagas?" },
  { intent: "contact", label: "Deixar um recado" },
];

const KEYWORDS: [Intent, RegExp][] = [
  ["contact", /recado|mensagem|contato|contatar|falar com|e-?mail/i],
  ["resume", /curr[ií]culo|\bcv\b|resume/i],
  ["availability", /dispon[ií]vel|vaga|est[aá]gio|contrat|oportunidade/i],
  ["skills", /tecnolog|skill|habilidade|stack|linguagem|ferramenta|sabe|usa/i],
  ["projects", /projeto|portf[oó]lio|github|c[oó]digo/i],
  ["courses", /curso|certific/i],
  ["education", /forma[cç][aã]o|faculdade|gradua|estud|trajet[oó]ria/i],
];

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Assistente de conversa do portfólio. As respostas são geradas localmente a partir dos dados
 * reais vindos da API (não é uma IA generativa) e o visitante pode deixar um recado que é gravado no backend.
 */
export function ChatWidget({ profile, skills, projects, education }: ChatWidgetProps) {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [typing, setTyping] = useState(false);
  const [step, setStep] = useState<Step>("menu");
  const [draft, setDraft] = useState<Draft>({ name: "", email: "", message: "" });
  const [input, setInput] = useState("");
  const nextId = useRef(1);
  const logRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const timers = useRef<number[]>([]);
  const launcherRef = useRef<HTMLButtonElement>(null);
  const wasOpen = useRef(false);

  const firstName = profile.fullName.split(" ")[0] ?? profile.fullName;

  const pushMessage = useCallback((message: Omit<ChatMessage, "id">) => {
    setMessages((current) => [...current, { ...message, id: nextId.current++ }]);
  }, []);

  /** Mostra o indicador de digitação e então a resposta do assistente. */
  const botSays = useCallback(
    (text: string, links?: ChatLink[], delay = 650) => {
      setTyping(true);
      const timer = window.setTimeout(() => {
        setTyping(false);
        pushMessage({ from: "bot", text, links });
      }, delay);
      timers.current.push(timer);
    },
    [pushMessage],
  );

  useEffect(() => {
    const pending = timers.current;
    return () => pending.forEach((timer) => window.clearTimeout(timer));
  }, []);

  useEffect(() => {
    if (open && messages.length === 0) {
      botSays(
        `Oi! Eu sou o assistente virtual de ${firstName}. Posso responder sobre tecnologias, projetos e disponibilidade, ou anotar um recado. Como posso ajudar?`,
        undefined,
        400,
      );
    }
    if (open) inputRef.current?.focus();
  }, [open, messages.length, firstName, botSays]);

  // Ao fechar, devolve o foco ao botão que abriu a conversa (evita perder a posição de navegação).
  useEffect(() => {
    if (!open && wasOpen.current) launcherRef.current?.focus();
    wasOpen.current = open;
  }, [open]);

  useEffect(() => {
    logRef.current?.scrollTo({ top: logRef.current.scrollHeight, behavior: "smooth" });
  }, [messages, typing]);

  function answer(intent: Intent) {
    switch (intent) {
      case "skills": {
        const lines = SKILL_CATEGORIES.map((category) => {
          const names = skills.filter((skill) => skill.category === category).map((skill) => skill.name);
          return names.length ? `• ${SKILL_CATEGORY_LABELS[category]}: ${names.join(", ")}` : null;
        }).filter(Boolean);
        botSays(lines.length ? `Estas são as tecnologias que ${firstName} usa:\n${lines.join("\n")}` : "As skills ainda estão sendo cadastradas.");
        break;
      }
      case "projects": {
        if (projects.length === 0) {
          botSays("Os projetos ainda estão sendo publicados. Volte em breve!");
          break;
        }
        const top = projects.slice(0, 4);
        botSays(
          `Alguns projetos:\n${top.map((p) => `• ${p.title} — ${p.technologies.slice(0, 3).join(", ")}`).join("\n")}`,
          top.filter((p) => p.githubUrl).map((p) => ({ label: `Código de ${p.title}`, href: p.githubUrl as string })),
        );
        break;
      }
      case "education": {
        if (education.length === 0) {
          botSays("O histórico acadêmico ainda não foi cadastrado.");
          break;
        }
        const lines = education.filter((e) => e.type === "DEGREE").slice(0, 4).map((e) => `• ${e.title} (${e.institution}) — ${EDUCATION_TYPE_LABELS[e.type]}${e.current ? ", em andamento" : ""}`);
        botSays(`Formação acadêmica:\n${lines.join("\n")}`);
        break;
      }
      case "courses": {
        const courses = education.filter((e) => e.type === "COURSE");
        if (courses.length === 0) {
          botSays("Os cursos e certificações ainda não foram cadastrados.");
          break;
        }
        botSays(
          `Cursos e certificações:\n${courses.slice(0, 5).map((c) => `• ${c.title} (${c.institution})${c.workloadHours ? ` — ${c.workloadHours}h` : ""}`).join("\n")}`,
          courses.filter((c) => c.credentialUrl).slice(0, 3).map((c) => ({ label: `Certificado: ${c.title}`, href: c.credentialUrl as string })),
        );
        break;
      }
      case "availability":
        botSays(
          profile.openToWork
            ? `Sim! ${firstName} está aberto(a) a estágio e vagas júnior${profile.location ? ` (${profile.location})` : ""}. Quer deixar um recado?`
            : `No momento ${firstName} não está buscando novas vagas, mas você pode deixar um recado.`,
        );
        break;
      case "resume":
        botSays(
          profile.resumeUrl ? "Aqui está o currículo:" : "O currículo ainda não está disponível online, mas você pode pedir por recado.",
          profile.resumeUrl ? [{ label: "Baixar currículo", href: profile.resumeUrl }] : undefined,
        );
        break;
      case "contact":
        setStep("name");
        botSays("Claro! Qual é o seu nome?");
        break;
    }
  }

  function handleQuickReply(intent: Intent, label: string) {
    if (step !== "menu") return;
    pushMessage({ from: "user", text: label });
    answer(intent);
  }

  async function submitMessage(finalDraft: Draft) {
    setStep("sending");
    try {
      const payload: MessageRequest = { ...finalDraft, source: "CHAT", website: "" };
      await publicApi.post("/contact", payload);
      botSays(`Recado enviado, ${finalDraft.name}! ${firstName} vai responder no e-mail ${finalDraft.email}. Obrigado!`);
      setDraft({ name: "", email: "", message: "" });
    } catch (error) {
      botSays(`Não consegui enviar o recado: ${getErrorMessage(error)}`);
    } finally {
      setStep("menu");
    }
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const text = input.trim();
    if (!text || typing || step === "sending") return;
    setInput("");
    pushMessage({ from: "user", text });

    if (step === "name") {
      setDraft((current) => ({ ...current, name: text.slice(0, 80) }));
      setStep("email");
      botSays(`Prazer, ${text.slice(0, 80)}! Qual é o seu e-mail para resposta?`);
    } else if (step === "email") {
      if (!EMAIL_PATTERN.test(text)) {
        botSays("Esse e-mail não parece válido. Pode conferir?");
        return;
      }
      setDraft((current) => ({ ...current, email: text }));
      setStep("message");
      botSays("Perfeito. Agora escreva seu recado (mínimo de 10 caracteres).");
    } else if (step === "message") {
      if (text.length < 10) {
        botSays("Seu recado está muito curto. Conte um pouco mais, por favor.");
        return;
      }
      void submitMessage({ ...draft, message: text.slice(0, 2000) });
    } else {
      const match = KEYWORDS.find(([, pattern]) => pattern.test(text));
      if (match) answer(match[0]);
      else botSays("Não entendi muito bem, mas posso ajudar com os assuntos abaixo. Escolha uma opção:");
    }
  }

  const placeholder =
    step === "name" ? "Seu nome" : step === "email" ? "Seu e-mail" : step === "message" ? "Escreva seu recado" : "Digite sua pergunta…";

  return (
    <>
      {!open && (
        <IconButton
          ref={launcherRef}
          label="Abrir conversa com o assistente"
          variant="solid"
          size="lg"
          onClick={() => setOpen(true)}
          className="fixed bottom-4 right-4 z-50 sm:bottom-5 sm:right-5"
        >
          <MessageCircle className="h-6 w-6" aria-hidden />
        </IconButton>
      )}

      {open && (
        <section
          role="dialog"
          aria-label="Conversa com o assistente do portfólio"
          aria-modal="false"
          onKeyDown={(event) => event.key === "Escape" && setOpen(false)}
          className="fixed inset-x-3 bottom-3 z-50 flex h-[32rem] max-h-[calc(100vh-1.5rem)] flex-col overflow-hidden rounded-2xl border border-border bg-card shadow-2xl sm:inset-x-auto sm:bottom-5 sm:right-5 sm:w-96"
        >
          <header className="flex items-center justify-between border-b border-border bg-primary px-4 py-3 text-primary-foreground">
            <div className="flex items-center gap-3">
              <span className="flex h-9 w-9 items-center justify-center rounded-full bg-white/20">
                <Bot className="h-5 w-5" aria-hidden />
              </span>
              <div>
                <p className="text-sm font-semibold leading-tight">Assistente de {firstName}</p>
                <p className="text-xs opacity-80">Respostas automáticas</p>
              </div>
            </div>
            <IconButton label="Fechar conversa" variant="inverse" size="sm" onClick={() => setOpen(false)}>
              <X className="h-5 w-5" aria-hidden />
            </IconButton>
          </header>

          <div ref={logRef} role="log" aria-live="polite" className="flex-1 space-y-3 overflow-y-auto px-4 py-4">
            {messages.map((message) => (
              <div key={message.id} className={cn("flex", message.from === "user" ? "justify-end" : "justify-start")}>
                <div
                  className={cn(
                    "max-w-[85%] whitespace-pre-line rounded-2xl px-3.5 py-2 text-sm",
                    message.from === "user" ? "rounded-br-sm bg-primary text-primary-foreground" : "rounded-bl-sm bg-muted",
                  )}
                >
                  {message.text}
                  {message.links && (
                    <span className="mt-2 flex flex-col gap-1">
                      {message.links.map((link) => (
                        <a key={link.href} href={link.href} target="_blank" rel="noopener noreferrer" className="font-medium text-primary underline">
                          {link.label}
                        </a>
                      ))}
                    </span>
                  )}
                </div>
              </div>
            ))}
            {typing && (
              <div className="flex justify-start" aria-label="Assistente digitando">
                <div className="flex gap-1 rounded-2xl rounded-bl-sm bg-muted px-4 py-3">
                  {[0, 1, 2].map((dot) => (
                    <span key={dot} className="h-1.5 w-1.5 rounded-full bg-muted-foreground motion-safe:animate-bounce" style={{ animationDelay: `${dot * 120}ms` }} />
                  ))}
                </div>
              </div>
            )}
          </div>

          {step === "menu" && (
            <div className="flex flex-wrap gap-2 border-t border-border px-4 py-3">
              {QUICK_REPLIES.map(({ intent, label }) => (
                <FilterChip key={intent} size="sm" disabled={typing} onClick={() => handleQuickReply(intent, label)}>
                  {label}
                </FilterChip>
              ))}
            </div>
          )}

          <form onSubmit={handleSubmit} className="flex items-center gap-2 border-t border-border p-3">
            <input
              ref={inputRef}
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder={placeholder}
              aria-label={placeholder}
              maxLength={2000}
              type={step === "email" ? "email" : "text"}
              className="h-10 flex-1 rounded-lg border border-border bg-background px-3 text-base sm:text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
            />
            <Button type="submit" size="sm" className="h-10 w-10 px-0" loading={step === "sending"} disabled={typing} aria-label="Enviar">
              {step !== "sending" && <Send className="h-4 w-4" aria-hidden />}
            </Button>
          </form>
        </section>
      )}
    </>
  );
}
