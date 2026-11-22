"use client";

import { useState, type ReactNode } from "react";
import { Pencil, Plus, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { DataTable, type Column } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { Modal } from "@/components/ui/Modal";
import { Spinner } from "@/components/ui/Spinner";
import { useCrud } from "@/hooks/useCrud";
import { getErrorMessage } from "@/lib/api-client";

export interface CrudFormProps<TEntity, TRequest> {
  initial: TEntity | null;
  submitting: boolean;
  onSubmit: (payload: TRequest) => void;
  onCancel: () => void;
}

interface CrudManagerProps<TEntity extends { id: number }, TRequest> {
  resource: string;
  title: string;
  singular: string;
  description: string;
  columns: Column<TEntity>[];
  rowLabel: (entity: TEntity) => string;
  renderForm: (props: CrudFormProps<TEntity, TRequest>) => ReactNode;
}

/** Tela de gerenciamento completa: tabela, criação/edição em modal e exclusão com confirmação. */
export function CrudManager<TEntity extends { id: number }, TRequest>({
  resource,
  title,
  singular,
  description,
  columns,
  rowLabel,
  renderForm,
}: CrudManagerProps<TEntity, TRequest>) {
  const { list, create, update, remove } = useCrud<TEntity, TRequest>(resource);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<TEntity | null>(null);
  const [toDelete, setToDelete] = useState<TEntity | null>(null);

  const submitting = create.isPending || update.isPending;

  function openCreate() {
    setEditing(null);
    setFormOpen(true);
  }

  function openEdit(entity: TEntity) {
    setEditing(entity);
    setFormOpen(true);
  }

  function closeForm() {
    setFormOpen(false);
  }

  function handleSubmit(payload: TRequest) {
    const onError = (error: unknown) => toast.error(getErrorMessage(error));
    if (editing) {
      update.mutate(
        { id: editing.id, payload },
        {
          onSuccess: () => {
            toast.success("Alterações salvas.");
            closeForm();
          },
          onError,
        },
      );
    } else {
      create.mutate(payload, {
        onSuccess: () => {
          toast.success("Salvo com sucesso.");
          closeForm();
        },
        onError,
      });
    }
  }

  function confirmDelete() {
    if (!toDelete) return;
    remove.mutate(toDelete.id, {
      onSuccess: () => {
        toast.success("Removido da lista.");
        setToDelete(null);
      },
      onError: (error) => toast.error(getErrorMessage(error)),
    });
  }

  const allColumns: Column<TEntity>[] = [
    ...columns,
    {
      header: "Ações",
      className: "w-28 text-right",
      cell: (row) => (
        <div className="flex justify-end gap-1">
          <Button variant="ghost" size="sm" onClick={() => openEdit(row)} aria-label={`Editar ${rowLabel(row)}`}>
            <Pencil className="h-4 w-4" aria-hidden />
          </Button>
          <Button variant="ghost" size="sm" onClick={() => setToDelete(row)} aria-label={`Excluir ${rowLabel(row)}`}>
            <Trash2 className="h-4 w-4 text-danger" aria-hidden />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="font-display text-2xl font-semibold tracking-tight">{title}</h1>
          <p className="text-sm text-muted-foreground">{description}</p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" aria-hidden /> Adicionar {singular.toLowerCase()}
        </Button>
      </div>

      {list.isPending && <Spinner className="py-10" />}
      {list.isError && (
        <EmptyState
          title="Não foi possível carregar os dados"
          description={getErrorMessage(list.error)}
          action={<Button variant="outline" onClick={() => list.refetch()}>Tentar novamente</Button>}
        />
      )}
      {list.isSuccess && list.data.length === 0 && (
        <EmptyState
          title={`Nada por aqui ainda`}
          description="Os itens que você adicionar aparecem nesta lista."
          action={<Button onClick={openCreate}>Adicionar {singular.toLowerCase()}</Button>}
        />
      )}
      {list.isSuccess && list.data.length > 0 && (
        <DataTable columns={allColumns} rows={list.data} rowKey={(row) => row.id} caption={title} />
      )}

      <Modal
        open={formOpen}
        onClose={closeForm}
        title={editing ? `Editar ${singular.toLowerCase()}` : `Adicionar ${singular.toLowerCase()}`}
        className="max-w-xl"
      >
        {renderForm({ initial: editing, submitting, onSubmit: handleSubmit, onCancel: closeForm })}
      </Modal>

      <ConfirmDialog
        open={toDelete !== null}
        title={`Excluir ${singular.toLowerCase()}`}
        message={`Tem certeza que deseja excluir "${toDelete ? rowLabel(toDelete) : ""}"? Esta ação não pode ser desfeita.`}
        loading={remove.isPending}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </div>
  );
}
