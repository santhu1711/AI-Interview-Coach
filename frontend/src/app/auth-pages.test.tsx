import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import LoginPage from "@/app/login/page";
import RegisterPage from "@/app/register/page";
import { AuthProvider } from "@/contexts/auth-context";
import { authService } from "@/services/auth-service";
const { replace } = vi.hoisted(() => ({ replace: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => ({ replace }), useSearchParams: () => new URLSearchParams() }));
vi.mock("@/services/auth-service", () => ({ authService: { currentUser: vi.fn(), login: vi.fn(), register: vi.fn() } }));
const authResponse = { accessToken: "token", tokenType: "Bearer", expiresIn: 3600, user: { id: 1, fullName: "Test User", email: "user@example.com" } };
describe("authentication pages", () => {
  it("submits valid login credentials and navigates", async () => { vi.mocked(authService.login).mockResolvedValue(authResponse); render(<AuthProvider><LoginPage/></AuthProvider>); fireEvent.change(screen.getByLabelText("Email"), { target: { value: "user@example.com" } }); fireEvent.change(screen.getByLabelText("Password"), { target: { value: "Secret1!" } }); fireEvent.click(screen.getByRole("button", { name: "Log in" })); await waitFor(() => expect(authService.login).toHaveBeenCalled()); expect(replace).toHaveBeenCalledWith("/dashboard"); });
  it("shows invalid-login feedback from the API", async () => { vi.mocked(authService.login).mockRejectedValue({ isAxiosError: true, response: { data: { status: 401, error: "Unauthorized", message: "Invalid email or password." } } }); render(<AuthProvider><LoginPage/></AuthProvider>); fireEvent.change(screen.getByLabelText("Email"), { target: { value: "user@example.com" } }); fireEvent.change(screen.getByLabelText("Password"), { target: { value: "wrong" } }); fireEvent.click(screen.getByRole("button", { name: "Log in" })); expect(await screen.findByText("Invalid email or password.")).toBeInTheDocument(); });
  it("shows validation errors for invalid registration", async () => { render(<AuthProvider><RegisterPage/></AuthProvider>); fireEvent.click(screen.getByRole("button", { name: "Create account" })); expect(await screen.findAllByRole("alert")).not.toHaveLength(0); expect(authService.register).not.toHaveBeenCalled(); });
  it("shows a duplicate-email server response", async () => { vi.mocked(authService.register).mockRejectedValue({ isAxiosError: true, response: { data: { status: 409, error: "Conflict", message: "An account with this email already exists." } } }); render(<AuthProvider><RegisterPage/></AuthProvider>); fireEvent.change(screen.getByLabelText("Full name"), { target: { value: "Test User" } }); fireEvent.change(screen.getByLabelText("Email"), { target: { value: "user@example.com" } }); fireEvent.change(screen.getByLabelText("Password"), { target: { value: "Secret1!" } }); fireEvent.change(screen.getByLabelText("Confirm password"), { target: { value: "Secret1!" } }); fireEvent.click(screen.getByRole("button", { name: "Create account" })); expect(await screen.findByText("An account with this email already exists.")).toBeInTheDocument(); });
});
