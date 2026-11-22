"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { adminApi } from "@/lib/api-client";

/**
 * Hook genérico de CRUD sobre um recurso do proxy admin (/api/admin/<resource>),
 * com cache e invalidação via TanStack Query.
 */
export function useCrud<TEntity extends { id: number }, TRequest>(resource: string) {
  const queryClient = useQueryClient();
  const queryKey = ["admin", resource] as const;
  const invalidate = () => queryClient.invalidateQueries({ queryKey });

  const list = useQuery({
    queryKey,
    queryFn: async () => (await adminApi.get<TEntity[]>(`/${resource}`)).data,
  });

  const create = useMutation({
    mutationFn: async (payload: TRequest) => (await adminApi.post<TEntity>(`/${resource}`, payload)).data,
    onSuccess: invalidate,
  });

  const update = useMutation({
    mutationFn: async ({ id, payload }: { id: number; payload: TRequest }) =>
      (await adminApi.put<TEntity>(`/${resource}/${id}`, payload)).data,
    onSuccess: invalidate,
  });

  const remove = useMutation({
    mutationFn: async (id: number) => {
      await adminApi.delete(`/${resource}/${id}`);
    },
    onSuccess: invalidate,
  });

  return { list, create, update, remove };
}
