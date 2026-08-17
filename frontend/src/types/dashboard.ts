import type { FieldCategory, InterviewStatus } from "@/types/interview";

export interface InterviewSummary {
  id: string; fieldCategory: FieldCategory; interviewDomain: string; customDomain: string | null; topic: string;
  difficulty: string; interviewMode: string; targetRole: string; experienceLevel: string; totalQuestions: number;
  currentQuestionNumber: number; followUpCount: number; progressPercentage: number; status: InterviewStatus;
  overallScore: number | null; startedAt: string | null; completedAt: string | null; createdAt: string; updatedAt: string;
}
export interface DashboardSummary {
  totalInterviews: number; completedInterviews: number; activeInterviews: number; itInterviewCount: number; nonItInterviewCount: number;
  averageScore: number | null; averageItScore: number | null; averageNonItScore: number | null; highestScore: number | null;
  passPercentage: number; strongestDomain: string | null; weakestDomain: string | null; recentInterviews: InterviewSummary[];
}
export interface ScoreTrendPoint { sessionId: string; generatedAt: string; fieldCategory: FieldCategory; domain: string; score: number; }
export interface DomainPerformance { domain: string; interviewCount: number; averageScore: number; }
export interface CategoryPerformance { fieldCategory: FieldCategory; interviewCount: number; averageScore: number | null; }
export interface DashboardPerformance { scoreTrend: ScoreTrendPoint[]; domainPerformance: DomainPerformance[]; categoryComparison: CategoryPerformance[]; }
export interface InterviewHistory { content: InterviewSummary[]; page: number; size: number; totalElements: number; totalPages: number; first: boolean; last: boolean; }
export interface HistoryFilters { search?: string; fieldCategory?: string; domain?: string; mode?: string; difficulty?: string; status?: string; sort?: string; page?: number; size?: number; }
