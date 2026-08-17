import type { InterviewOption } from "@/types/interview";
export function optionLabel(options: InterviewOption[], value: string) { return options.find(option => option.value === value)?.label ?? value.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, letter => letter.toUpperCase()); }
export function statusLabel(value: string) { return value.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, letter => letter.toUpperCase()); }
