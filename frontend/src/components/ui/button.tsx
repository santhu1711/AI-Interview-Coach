import type { ButtonHTMLAttributes } from "react";
export function Button({ className = "", ...props }: ButtonHTMLAttributes<HTMLButtonElement>) { return <button className={`inline-flex min-h-11 items-center justify-center rounded-md bg-blue-600 px-4 py-2 font-semibold text-white transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60 ${className}`} {...props} />; }
