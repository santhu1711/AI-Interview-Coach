import type { FieldCategory } from "@/types/interview";
export interface QuestionFeedback { question: string; answerSummary: string; evaluation: string; feedback: string; }
export interface InterviewReport {
  id: number; sessionId: string; fieldCategory: FieldCategory; overallScore: number; scoreInterpretation: string;
  technicalAccuracyScore: number | null; conceptualUnderstandingScore: number | null; problemSolvingScore: number | null;
  communicationScore: number | null; confidenceScore: number | null; situationalJudgementScore: number | null;
  roleUnderstandingScore: number | null; strengths: string[]; weaknesses: string[]; revisionAreas: string[];
  verdict: string; recommendation: "PASS" | "FAIL"; questionFeedback: QuestionFeedback[]; generatedAt: string;
}
