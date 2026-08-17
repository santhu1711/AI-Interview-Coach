"use client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter, useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
import { useForm } from "react-hook-form";
import { AuthShell } from "@/components/auth/auth-shell";
import { PasswordField } from "@/components/auth/password-field";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { useAuth } from "@/contexts/auth-context";
import { getApiError } from "@/lib/api-error";
import { loginSchema, type LoginFormValues } from "@/validations/auth";
function LoginForm() {
  const { login } = useAuth(); const router = useRouter(); const params = useSearchParams(); const [serverError, setServerError] = useState("");
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema), defaultValues: { email: "", password: "" } });
  const submit = async (values: LoginFormValues) => { setServerError(""); try { await login(values); const next = params.get("next"); router.replace(next?.startsWith("/") && !next.startsWith("//") ? next : "/dashboard"); } catch (error) { setServerError(getApiError(error, "Unable to connect to the server. Please try again.").message); } };
  return <AuthShell alternateHref="/register" alternateLabel="Create an account" alternateText="New here?" description="Welcome back. Continue building your interview confidence." title="Log in"><form className="mt-8 grid gap-5" noValidate onSubmit={handleSubmit(submit)}>{serverError && <p className="rounded-md border border-red-900 bg-red-950/40 p-3 text-sm text-red-300" role="alert">{serverError}</p>}<FormField autoComplete="email" error={errors.email?.message} id="email" label="Email" placeholder="you@example.com" type="email" {...register("email")}/><PasswordField autoComplete="current-password" error={errors.password?.message} id="password" label="Password" {...register("password")}/><Button disabled={isSubmitting} type="submit">{isSubmitting ? "Logging in…" : "Log in"}</Button></form></AuthShell>;
}
export default function LoginPage() { return <Suspense fallback={<main className="grid min-h-screen place-items-center">Loading…</main>}><LoginForm/></Suspense>; }
