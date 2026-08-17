import type { SelectHTMLAttributes } from "react";
import type { InterviewOption } from "@/types/interview";

interface Props extends SelectHTMLAttributes<HTMLSelectElement> { label: string; error?: string; options: InterviewOption[]; placeholder?: string; }
export function SelectField({ label, error, options, placeholder = "Select an option", id, ...props }: Props) {
  return <div className="grid gap-2"><label className="text-sm font-medium" htmlFor={id}>{label}</label><select aria-describedby={error ? `${id}-error` : undefined} aria-invalid={Boolean(error)} className="min-h-11 rounded-md border border-zinc-700 bg-zinc-900 px-3 text-zinc-100" id={id} {...props}><option value="">{placeholder}</option>{options.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}</select>{error && <p className="text-sm text-red-400" id={`${id}-error`} role="alert">{error}</p>}</div>;
}
