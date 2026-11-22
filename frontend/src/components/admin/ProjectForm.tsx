import { Button } from "@/components/ui/Button";

interface FormActionsProps {
  submitting: boolean;
  onCancel: () => void;
  isEditing: boolean;
}

export function FormActions({ submitting, onCancel, isEditing }: FormActionsProps) {
  return (
    <div className="flex justify-end gap-2 pt-2">
      <Button variant="outline" onClick={onCancel} disabled={submitting}>
        Cancelar
      </Button>
      <Button type="submit" loading={submitting}>
        {isEditing ? "Salvar alterações" : "Criar"}
      </Button>
    </div>
  );
}
