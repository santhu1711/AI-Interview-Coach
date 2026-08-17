"use client";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { AuthShell } from "@/components/auth/auth-shell";
import { PasswordField } from "@/components/auth/password-field";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { useAuth } from "@/contexts/auth-context";
import { getApiError } from "@/lib/api-error";
import { registerSchema, type RegisterFormValues } from "@/validations/auth";
export default function RegisterPage() {
  const { register: createAccount } = useAuth(); const router = useRouter(); const [serverError, setServerError] = useState("");
  const { register, handleSubmit, setError, formState: { errors, isSubmitting } } = useForm<RegisterFormValues>({ resolver: zodResolver(registerSchema), defaultValues: { fullName: "", email: "", password: "", confirmPassword: "" } });
  const submit = async (values: RegisterFormValues) => { setServerError(""); try { await createAccount(values); router.replace("/dashboard"); } catch (error) { const response = getApiError(error, "Unable to connect to the server. Please try again."); if (response.fieldErrors) Object.entries(response.fieldErrors).forEach(([field, message]) => { if (field in values) setError(field as keyof RegisterFormValues, { message }); }); setServerError(response.message); } };
  return <AuthShell alternateHref="/login" alternateLabel="Log in" alternateText="Already have an account?" description="Create your private workspace and begin practising." title="Create account"><form className="mt-8 grid gap-5" noValidate onSubmit={handleSubmit(submit)}>{serverError && <p className="rounded-md border border-red-900 bg-red-950/40 p-3 text-sm text-red-300" role="alert">{serverError}</p>}<FormField autoComplete="name" error={errors.fullName?.message} id="fullName" label="Full name" {...register("fullName")}/><FormField autoComplete="email" error={errors.email?.message} id="email" label="Email" placeholder="you@example.com" type="email" {...register("email")}/><PasswordField autoComplete="new-password" error={errors.password?.message} id="password" label="Password" {...register("password")}/><PasswordField autoComplete="new-password" error={errors.confirmPassword?.message} id="confirmPassword" label="Confirm password" {...register("confirmPassword")}/><p className="text-xs leading-5 text-zinc-500">Use 8–72 characters with uppercase, lowercase, number, and special character.</p><Button disabled={isSubmitting} type="submit">{isSubmitting ? "Creating account…" : "Create account"}</Button></form></AuthShell>;
}
