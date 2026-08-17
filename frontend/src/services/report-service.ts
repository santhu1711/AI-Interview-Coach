import { api } from "@/services/api";
import type { InterviewReport } from "@/types/report";
export const reportService = {
  get: async (sessionId: string) => (await api.get<InterviewReport>(`/api/interviews/${sessionId}/report`)).data,
  generate: async (sessionId: string) => (await api.post<InterviewReport>(`/api/interviews/${sessionId}/report`)).data,
};
