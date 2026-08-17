import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ProfileView } from "@/components/profile/profile-view";
import { dashboardService } from "@/services/dashboard-service";
import { profileService } from "@/services/profile-service";
const { updateUser } = vi.hoisted(() => ({ updateUser: vi.fn() }));
vi.mock("@/contexts/auth-context", () => ({ useAuth: () => ({ updateUser }) }));
vi.mock("@/services/profile-service", () => ({ profileService: { get: vi.fn(), update: vi.fn() } }));
vi.mock("@/services/dashboard-service", () => ({ dashboardService: { summary: vi.fn() } }));
const profile = { id: 1, fullName: "Original Name", email: "profile@example.com", createdAt: "2025-01-02T00:00:00Z" };
const summary = { totalInterviews: 4, completedInterviews: 3, activeInterviews: 1, itInterviewCount: 3, nonItInterviewCount: 1, averageScore: 75, averageItScore: 76, averageNonItScore: 72, highestScore: 90, passPercentage: 75, strongestDomain: "Java", weakestDomain: "Sales", recentInterviews: [] };
describe("ProfileView", () => {
  beforeEach(() => { vi.clearAllMocks(); vi.mocked(profileService.get).mockResolvedValue(profile); vi.mocked(dashboardService.summary).mockResolvedValue(summary); });
  it("shows safe account details and performance statistics", async () => { render(<ProfileView/>); expect(await screen.findByText("Original Name")).toBeInTheDocument(); expect(screen.getByDisplayValue("profile@example.com")).toBeDisabled(); expect(screen.getByText("75.0")).toBeInTheDocument(); expect(screen.queryByText(/password/i)).not.toBeInTheDocument(); });
  it("validates and updates the full name and shared auth state", async () => { vi.mocked(profileService.update).mockResolvedValue({ ...profile, fullName: "Updated Name" }); render(<ProfileView/>); const input = await screen.findByLabelText("Full name"); fireEvent.change(input, { target: { value: "Updated Name" } }); fireEvent.click(screen.getByRole("button", { name: "Save changes" })); expect(await screen.findByText("Profile updated successfully.")).toBeInTheDocument(); expect(profileService.update).toHaveBeenCalledWith("Updated Name"); expect(updateUser).toHaveBeenCalledWith({ id: 1, fullName: "Updated Name", email: "profile@example.com" }); fireEvent.change(input, { target: { value: " " } }); fireEvent.submit(input.closest("form")!); expect(await screen.findByText("Full name is required.")).toBeInTheDocument(); });
  it("shows update failures", async () => { vi.mocked(profileService.update).mockRejectedValue({ isAxiosError: true, response: { data: { status: 500, message: "Profile update failed." } } }); render(<ProfileView/>); const input = await screen.findByLabelText("Full name"); fireEvent.change(input, { target: { value: "Another Name" } }); fireEvent.click(screen.getByRole("button", { name: "Save changes" })); await waitFor(() => expect(screen.getByText("Profile update failed.")).toBeInTheDocument()); });
});
