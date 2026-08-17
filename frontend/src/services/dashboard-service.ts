import { api } from "@/services/api";
import type { DashboardPerformance, DashboardSummary, HistoryFilters, InterviewHistory } from "@/types/dashboard";
export const dashboardService = {
  summary: async () => (await api.get<DashboardSummary>("/api/dashboard/summary")).data,
  performance: async () => (await api.get<DashboardPerformance>("/api/dashboard/performance")).data,
  history: async (filters: HistoryFilters) => (await api.get<InterviewHistory>("/api/interviews", { params: filters })).data,
};
