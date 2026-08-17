import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { AuthProvider, useAuth } from "@/contexts/auth-context";
import { authService } from "@/services/auth-service";
vi.mock("@/services/auth-service", () => ({ authService: { currentUser: vi.fn(), login: vi.fn(), register: vi.fn() } }));
function Consumer() { const { isAuthenticated, login, logout, user } = useAuth(); return <><span>{isAuthenticated ? user?.fullName : "Signed out"}</span><button onClick={() => login({ email: "user@example.com", password: "Secret1!" })}>Login</button><button onClick={logout}>Logout</button></>; }
describe("AuthProvider", () => {
  it("stores authentication after login and clears it on logout", async () => { vi.mocked(authService.login).mockResolvedValue({ accessToken: "token", tokenType: "Bearer", expiresIn: 3600, user: { id: 1, fullName: "Test User", email: "user@example.com" } }); render(<AuthProvider><Consumer/></AuthProvider>); await screen.findByText("Signed out"); fireEvent.click(screen.getByText("Login")); await screen.findByText("Test User"); expect(window.localStorage.length).toBe(1); fireEvent.click(screen.getByText("Logout")); await waitFor(() => expect(screen.getByText("Signed out")).toBeInTheDocument()); expect(window.localStorage.length).toBe(0); });
});
