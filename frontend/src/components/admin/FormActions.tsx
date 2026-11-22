"use client";

import { useRef, useState } from "react";
import { ImagePlus } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/Button";
import { getErrorMessage } from "@/lib/api-client";
import { IMAGE_ACCEPT, uploadImage, validateImageFile } from "@/lib/upload";

interface UploadButtonProps {
  onUploaded: (url: string) => void;
  label?: string;
  disabled?: boolean;
  /** Permite escolher vários arquivos de uma vez; onUploaded é chamado a cada imagem enviada. */
  multiple?: boolean;
}

/** Botão que abre o seletor de arquivos, envia a imagem ao backend e devolve a URL pública. */
export function UploadButton({ onUploaded, label = "Enviar imagem", disabled, multiple = false }: UploadButtonProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [uploading, setUploading] = useState(false);

  async function handleFiles(list: FileList | null) {
    const files = Array.from(list ?? []);
    if (files.length === 0) return;
    const problem = files.map(validateImageFile).find((message) => message !== null);
    if (problem) {
      toast.error(problem);
      if (inputRef.current) inputRef.current.value = "";
      return;
    }
    setUploading(true);
    let sent = 0;
    try {
      for (const file of files) {
        onUploaded(await uploadImage(file));
        sent += 1;
      }
      toast.success(sent === 1 ? "Imagem enviada." : `${sent} imagens enviadas.`);
    } catch (error) {
      toast.error(sent > 0 ? `${sent} enviada(s); a próxima falhou: ${getErrorMessage(error)}` : getErrorMessage(error));
    } finally {
      setUploading(false);
      if (inputRef.current) inputRef.current.value = "";
    }
  }

  return (
    <>
      <input
        ref={inputRef}
        type="file"
        multiple={multiple}
        accept={IMAGE_ACCEPT}
        className="sr-only"
        tabIndex={-1}
        aria-hidden
        onChange={(event) => void handleFiles(event.target.files)}
      />
      <Button type="button" variant="outline" size="sm" loading={uploading} disabled={disabled} onClick={() => inputRef.current?.click()}>
        <ImagePlus className="h-4 w-4" aria-hidden /> {label}
      </Button>
    </>
  );
}
