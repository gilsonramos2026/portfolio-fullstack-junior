"use client";

import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CheckCheck, MessageSquare, Reply, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/Badge";
import { Button, buttonStyles } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { EmptyState } from "@/components/ui/EmptyState";
import { Spinner } from "@/components/ui/Spinner";
import { useCrud } from "@/hooks/useCrud";
import { adminApi, getErrorMessage } from "@/lib/api-client";
import { cn } from "@/lib/utils";
import type { ContactMessage } from "@/types";

const dateFormatter = new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short" });

export function MessagesManager() {
  const queryClient = useQueryClient();
  const { list, remove } = useCrud<ContactMessage, never>("messages");
  const [toDelete, setToDelete] = useState<ContactMessage | null>(null);

  const markRead = useMutation({
    mutationFn: async (id: number) => (await adminApi.patch<ContactMessage>(`/messages/${id}/read`)).data,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["admin", "messages"] });
      void queryClient.invalidateQueries({ queryKey: ["admin", "messages-unread"] });
    },
    onError: (error) => toast.error(getErrorMessage(error)),
  });

  function confirmDelete() {
    if (!toDelete) return;
    remove.mutate(toDelete.id, {
      onSuccess: () => {
        toast.success("Mensagem excluída.");
        setToDelete(null);
        void queryClient.invalidateQueries({ queryKey: ["admin", "messages-unread"] });
      },
      onError: (error) => toast.error(getErrorMessage(error)),
    });
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Mensagens</h1>
        <p className="text-sm text-muted-foreground">Recados enviados pelo formulário de contato e pelo chat do site.</p>
      </div>

      {list.isPending && <Spinner className="py-10" />}
      {list.isError && (
        <EmptyState
          title="Não foi possível carregar as mensagens"
          description={getErrorMessage(list.error)}
          action={<Button variant="outline" onClick={() => list.refetch()}>Tentar novamente</Button>}
        />
      )}
      {list.isSuccess && list.data.length === 0 && (
        <EmptyState title="Nenhuma mensagem ainda" description="Quando alguém entrar em contato, o recado aparecerá aqui." />
      )}

      {list.isSuccess && list.data.length > 0 && (
        <ul className="space-y-3">
          {list.data.map((item) => (
            <li key={item.id}>
              <Card className={cn("space-y-3 p-5", !item.read && "border-primary/50")}>
                <div className="flex flex-wrap items-start justify-between gap-2">
                  <div>
                    <p className="flex items-center gap-2 font-semibold">
                      {!item.read && <span className="h-2 w-2 rounded-full bg-primary"><span className="sr-only">Não lida</span></span>}
                      {item.name}
                    </p>
                    <p className="text-sm text-muted-foreground">{item.email}</p>
                  </div>
                  <div className="flex items-center gap-2 text-xs text-muted-foreground">
                    <Badge tone={item.source === "CHAT" ? "accent" : "primary"}>
                      <MessageSquare className="mr-1 h-3 w-3" aria-hidden /> {item.source === "CHAT" ? "Chat" : "Formulário"}
                    </Badge>
                    {dateFormatter.format(new Date(item.createdAt))}
                  </div>
                </div>
                <p className="whitespace-pre-line text-sm">{item.message}</p>
                <div className="flex flex-wrap justify-end gap-2">
                  <a
                    href={`mailto:${item.email}?subject=${encodeURIComponent("Re: seu contato pelo portfólio")}`}
                    onClick={() => !item.read && markRead.mutate(item.id)}
                    className={buttonStyles({ variant: "outline", size: "sm" })}
                  >
                    <Reply className="h-4 w-4" aria-hidden /> Responder
                  </a>
                  {!item.read && (
                    <Button variant="ghost" size="sm" onClick={() => markRead.mutate(item.id)} loading={markRead.isPending && markRead.variables === item.id}>
                      <CheckCheck className="h-4 w-4" aria-hidden /> Marcar como lida
                    </Button>
                  )}
                  <Button variant="ghost" size="sm" onClick={() => setToDelete(item)} aria-label={`Excluir mensagem de ${item.name}`}>
                    <Trash2 className="h-4 w-4 text-danger" aria-hidden />
                  </Button>
                </div>
              </Card>
            </li>
          ))}
        </ul>
      )}

      <ConfirmDialog
        open={toDelete !== null}
        title="Excluir mensagem"
        message={`Excluir a mensagem de "${toDelete?.name ?? ""}"? Esta ação não pode ser desfeita.`}
        loading={remove.isPending}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}
