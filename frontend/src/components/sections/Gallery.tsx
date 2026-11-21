"use client";

import { useState } from "react";
import { MediaButton } from "@/components/ui/MediaButton";
import { Modal } from "@/components/ui/Modal";

export function Gallery({ images, title }: { images: string[]; title: string }) {
  const [selected, setSelected] = useState<string | null>(null);

  return (
    <>
      <ul className="grid gap-4 sm:grid-cols-2">
        {images.map((src, index) => (
          <li key={src}>
            <MediaButton
              onClick={() => setSelected(src)}
              aria-label={`Ampliar captura ${index + 1} de ${title}`}
              className="rounded-xl border border-border"
            >
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img
                src={src}
                alt={`Captura ${index + 1} de ${title}`}
                width={1280}
                height={720}
                loading="lazy"
                decoding="async"
                className="aspect-video w-full object-cover transition duration-500 group-hover:scale-105 motion-reduce:transition-none"
              />
            </MediaButton>
          </li>
        ))}
      </ul>

      <Modal open={selected !== null} onClose={() => setSelected(null)} title={title} className="max-w-4xl">
        {selected && (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={selected} alt={`Captura ampliada de ${title}`} className="w-full rounded-lg" />
        )}
      </Modal>
    </>
  );
}
