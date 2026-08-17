export type FieldCategory = "IT" | "NON_IT";
export type InterviewStatus = "CREATED" | "IN_PROGRESS" | "COMPLETED" | "ABANDONED" | "REPORT_GENERATED";
export type MessageRole = "SYSTEM" | "ASSISTANT" | "USER";

export interface InterviewOption { value: string; label: string; }
export interface TextInputConstraint { minimumLength: number; maximumLength: number; }
export interface InterviewOptions {
  fieldCategories: InterviewOption[];
  domainLabels: Record<FieldCategory, string>;
  domains: Record<FieldCategory, InterviewOption[]>;
  modes: Record<FieldCategory, InterviewOption[]>;
  difficulties: InterviewOption[];
  experienceLevels: InterviewOption[];
  minimumQuestions: number;
  maximumQuestions: number;
  defaultQuestions: number;
  customDomain: TextInputConstraint;
  targetRole: TextInputConstraint;
}

export interface CreateInterviewRequest {
  fieldCategory: FieldCategory;
  interviewDomain: string;
  customDomain: string | null;
  topic: string;
  difficulty: string;
  interviewMode: string;
  targetRole: string;
  experienceLevel: string;
  totalQuestions: number;
}

export interface InterviewMessage {
  id: number;
  role: MessageRole;
  content: string;
  sequenceNumber: number;
  questionNumber: number | null;
  questionCategory: string | null;
  answerEvaluation?: string | null;
  createdAt: string;
}

export interface Interview {
  id: string;
  fieldCategory: FieldCategory;
  interviewDomain: string;
  customDomain: string | null;
  topic: string;
  difficulty: string;
  interviewMode: string;
  targetRole: string;
  experienceLevel: string;
  totalQuestions: number;
  currentQuestionNumber: number;
  followUpCount: number;
  progressPercentage: number;
  status: InterviewStatus;
  overallScore: number | null;
  startedAt: string | null;
  completedAt: string | null;
  createdAt: string;
  updatedAt: string;
  messages: InterviewMessage[];
}
