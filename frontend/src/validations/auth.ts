import { z } from "zod";
const email = z.email("Enter a valid email address.").max(254, "Email must not exceed 254 characters.");
const password = z.string().min(8, "Password must be at least 8 characters.").max(72, "Password must not exceed 72 characters.").regex(/[a-z]/, "Include a lowercase letter.").regex(/[A-Z]/, "Include an uppercase letter.").regex(/\d/, "Include a number.").regex(/[^A-Za-z0-9]/, "Include a special character.");
export const loginSchema = z.object({ email, password: z.string().min(1, "Password is required.") });
export const registerSchema = z.object({ fullName: z.string().trim().min(1, "Full name is required.").max(120, "Full name must not exceed 120 characters."), email, password, confirmPassword: z.string().min(1, "Confirm your password.") }).refine(data => data.password === data.confirmPassword, { message: "Passwords do not match.", path: ["confirmPassword"] });
export type LoginFormValues = z.infer<typeof loginSchema>;
export type RegisterFormValues = z.infer<typeof registerSchema>;
