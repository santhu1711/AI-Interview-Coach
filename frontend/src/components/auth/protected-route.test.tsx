import { render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { AuthProvider } from "@/contexts/auth-context";
const { replace } = vi.hoisted(() => ({ replace: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => ({ replace }), usePathname: () => "/dashboard" }));
vi.mock("@/services/auth-service", () => ({ authService: { currentUser: vi.fn(), login: vi.fn(), register: vi.fn() } }));
describe("ProtectedRoute", () => {
  it("redirects an unauthenticated visitor to login", async () => { render(<AuthProvider><ProtectedRoute><p>Private page</p></ProtectedRoute></AuthProvider>); await waitFor(() => expect(replace).toHaveBeenCalledWith("/login?next=%2Fdashboard")); expect(screen.queryByText("Private page")).not.toBeInTheDocument(); });
});
