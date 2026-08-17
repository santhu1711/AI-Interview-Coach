import { render, screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { DashboardView } from "@/components/dashboard/dashboard-view";
import { dashboardService } from "@/services/dashboard-service";
import type { DashboardPerformance, DashboardSummary } from "@/types/dashboard";
vi.mock("@/contexts/auth-context", () => ({ useAuth: () => ({ user: { fullName: "Test User" } }) }));
vi.mock("@/components/dashboard/performance-charts", () => ({ PerformanceCharts: () => <div>Performance charts</div> }));
vi.mock("@/services/dashboard-service", () => ({ dashboardService: { summary: vi.fn(), performance: vi.fn() } }));
const performance: DashboardPerformance = { scoreTrend: [], domainPerformance: [], categoryComparison: [] };
const summary: DashboardSummary = { totalInterviews: 3, completedInterviews: 2, activeInterviews: 1, itInterviewCount: 2, nonItInterviewCount: 1, averageScore: 72.5, averageItScore: 75, averageNonItScore: 68, highestScore: 84, passPercentage: 66.7, strongestDomain: "Java", weakestDomain: "Sales", recentInterviews: [] };
describe("DashboardView", () => {
  beforeEach(() => vi.clearAllMocks());
  it("renders welcome, summary analytics, and performance", async () => { vi.mocked(dashboardService.summary).mockResolvedValue(summary); vi.mocked(dashboardService.performance).mockResolvedValue(performance); render(<DashboardView/>); expect(await screen.findByText("Welcome, Test")).toBeInTheDocument(); expect(screen.getByText("72.5")).toBeInTheDocument(); expect(screen.getByText("66.7%")).toBeInTheDocument(); expect(screen.getByText("Java")).toBeInTheDocument(); expect(screen.getByText("Performance charts")).toBeInTheDocument(); });
  it("renders a useful empty state", async () => { vi.mocked(dashboardService.summary).mockResolvedValue({ ...summary, totalInterviews: 0, recentInterviews: [] }); vi.mocked(dashboardService.performance).mockResolvedValue(performance); render(<DashboardView/>); expect(await screen.findByText("Your interview journey starts here")).toBeInTheDocument(); });
  it("renders API errors and retry", async () => { vi.mocked(dashboardService.summary).mockRejectedValue({ isAxiosError: true, response: { data: { status: 503, message: "Dashboard unavailable." } } }); vi.mocked(dashboardService.performance).mockResolvedValue(performance); render(<DashboardView/>); expect(await screen.findByText("Dashboard unavailable.")).toBeInTheDocument(); expect(screen.getByRole("button", { name: "Try again" })).toBeInTheDocument(); });
});
