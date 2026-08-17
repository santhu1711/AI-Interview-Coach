"use client";
import { Button } from "@/components/ui/button";

export function ConfirmationDialog({ open, title, description, confirmLabel, destructive = false, busy = false, onCancel, onConfirm }: { open: boolean; title: string; description: string; confirmLabel: string; destructive?: boolean; busy?: boolean; onCancel: () => void; onConfirm: () => void }) {
  if (!open) return null;
  return <div aria-modal="true" className="fixed inset-0 z-50 grid place-items-center bg-black/75 p-6" role="dialog"><div className="w-full max-w-md rounded-xl border border-zinc-700 bg-zinc-950 p-6 shadow-2xl"><h2 className="text-xl font-bold">{title}</h2><p className="mt-3 leading-7 text-zinc-400">{description}</p><div className="mt-6 flex justify-end gap-3"><button className="rounded-md border border-zinc-700 px-4 py-2 font-semibold" disabled={busy} onClick={onCancel} type="button">Cancel</button><Button className={destructive ? "bg-red-600 hover:bg-red-500" : ""} disabled={busy} onClick={onConfirm} type="button">{busy ? "Working…" : confirmLabel}</Button></div></div></div>;
}
