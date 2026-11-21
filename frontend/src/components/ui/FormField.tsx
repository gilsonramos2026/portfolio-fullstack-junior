import { forwardRef, useId, type InputHTMLAttributes, type ReactNode, type SelectHTMLAttributes, type TextareaHTMLAttributes } from "react";
import { cn } from "@/lib/utils";

const controlStyles =
  "w-full rounded-lg border border-input bg-background px-3 text-base transition sm:text-sm placeholder:text-muted-foreground " +
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary disabled:opacity-60 aria-[invalid=true]:border-danger";

interface FieldShellProps {
  label: string;
  error?: string;
  hint?: string;
  htmlFor: string;
  children: ReactNode;
}

function FieldShell({ label, error, hint, htmlFor, children }: FieldShellProps) {
  return (
    <div className="space-y-1.5">
      <label htmlFor={htmlFor} className="text-sm font-medium">
        {label}
      </label>
      {children}
      {hint && !error && <p className="text-xs text-muted-foreground">{hint}</p>}
      {error && (
        <p id={`${htmlFor}-error`} role="alert" className="text-xs text-danger">
          {error}
        </p>
      )}
    </div>
  );
}

interface CommonProps {
  label: string;
  error?: string;
  hint?: string;
}

export const InputField = forwardRef<HTMLInputElement, CommonProps & InputHTMLAttributes<HTMLInputElement>>(
  function InputField({ label, error, hint, className, id, ...props }, ref) {
    const generated = useId();
    const fieldId = id ?? generated;
    return (
      <FieldShell label={label} error={error} hint={hint} htmlFor={fieldId}>
        <input
          ref={ref}
          id={fieldId}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${fieldId}-error` : undefined}
          className={cn(controlStyles, "h-10", className)}
          {...props}
        />
      </FieldShell>
    );
  },
);

export const TextareaField = forwardRef<HTMLTextAreaElement, CommonProps & TextareaHTMLAttributes<HTMLTextAreaElement>>(
  function TextareaField({ label, error, hint, className, id, ...props }, ref) {
    const generated = useId();
    const fieldId = id ?? generated;
    return (
      <FieldShell label={label} error={error} hint={hint} htmlFor={fieldId}>
        <textarea
          ref={ref}
          id={fieldId}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${fieldId}-error` : undefined}
          className={cn(controlStyles, "min-h-28 resize-y py-2", className)}
          {...props}
        />
      </FieldShell>
    );
  },
);

export const SelectField = forwardRef<HTMLSelectElement, CommonProps & SelectHTMLAttributes<HTMLSelectElement>>(
  function SelectField({ label, error, hint, className, id, children, ...props }, ref) {
    const generated = useId();
    const fieldId = id ?? generated;
    return (
      <FieldShell label={label} error={error} hint={hint} htmlFor={fieldId}>
        <select
          ref={ref}
          id={fieldId}
          aria-invalid={Boolean(error)}
          aria-describedby={error ? `${fieldId}-error` : undefined}
          className={cn(controlStyles, "h-10", className)}
          {...props}
        >
          {children}
        </select>
      </FieldShell>
    );
  },
);

export const CheckboxField = forwardRef<HTMLInputElement, { label: string } & InputHTMLAttributes<HTMLInputElement>>(
  function CheckboxField({ label, className, id, ...props }, ref) {
    const generated = useId();
    const fieldId = id ?? generated;
    return (
      <label htmlFor={fieldId} className="flex cursor-pointer items-center gap-2 text-sm font-medium">
        <input
          ref={ref}
          id={fieldId}
          type="checkbox"
          className={cn("h-4 w-4 rounded border-input accent-[hsl(var(--primary))]", className)}
          {...props}
        />
        {label}
      </label>
    );
  },
);
