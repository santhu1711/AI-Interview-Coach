"use client";
import { Eye, EyeOff } from "lucide-react";
import { useState, type InputHTMLAttributes } from "react";
import { FormField } from "@/components/ui/form-field";
interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, "type"> { label: string; error?: string; }
export function PasswordField({ label, error, id, ...props }: Props) { const [visible, setVisible] = useState(false); return <div className="relative"><FormField error={error} id={id} label={label} type={visible ? "text" : "password"} {...props}/><button aria-label={visible ? "Hide password" : "Show password"} className="absolute right-3 top-9 text-zinc-400 hover:text-zinc-100" onClick={() => setVisible(value => !value)} type="button">{visible ? <EyeOff aria-hidden size={19}/> : <Eye aria-hidden size={19}/>}</button></div>; }
