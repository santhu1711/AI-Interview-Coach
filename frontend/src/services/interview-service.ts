import { api } from "@/services/api";
import type { CreateInterviewRequest, Interview, InterviewOptions } from "@/types/interview";

export const interviewService = {
  options: async () => (await api.get<InterviewOptions>("/api/interview-options")).data,
  create: async (request: CreateInterviewRequest) => (await api.post<Interview>("/api/interviews", request)).data,
  get: async (sessionId: string) => (await api.get<Interview>(`/api/interviews/${sessionId}`)).data,
  answer: async (sessionId: string, answer: string) => (await api.post<Interview>(`/api/interviews/${sessionId}/answers`, { answer })).data,
  complete: async (sessionId: string) => (await api.post<Interview>(`/api/interviews/${sessionId}/complete`)).data,
  abandon: async (sessionId: string) => (await api.post<Interview>(`/api/interviews/${sessionId}/abandon`)).data,
};
