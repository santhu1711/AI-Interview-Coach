import { describe, expect, it } from "vitest";
import { loginSchema, registerSchema } from "@/validations/auth";
import { isJwtExpired } from "@/lib/auth-token";
describe("authentication validation", () => {
  it("accepts valid login and registration values", () => { expect(loginSchema.safeParse({ email: "user@example.com", password: "Secret1!" }).success).toBe(true); expect(registerSchema.safeParse({ fullName: "Test User", email: "user@example.com", password: "Secret1!", confirmPassword: "Secret1!" }).success).toBe(true); });
  it("rejects invalid registration and mismatched passwords", () => { const result = registerSchema.safeParse({ fullName: "", email: "invalid", password: "weak", confirmPassword: "different" }); expect(result.success).toBe(false); if (!result.success) expect(result.error.issues.map(issue => issue.path[0])).toEqual(expect.arrayContaining(["fullName", "email", "password", "confirmPassword"])); });
  it("recognizes valid, expired, and malformed JWT expiry claims", () => { const token = (exp: number) => `header.${btoa(JSON.stringify({ exp }))}.signature`; expect(isJwtExpired(token(Math.floor(Date.now() / 1000) + 60))).toBe(false); expect(isJwtExpired(token(Math.floor(Date.now() / 1000) - 60))).toBe(true); expect(isJwtExpired("invalid")).toBe(true); });
});
