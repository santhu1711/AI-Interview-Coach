import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { InterviewSetup } from "@/components/interview/interview-setup";
import { interviewService } from "@/services/interview-service";
import type { InterviewOptions } from "@/types/interview";

const { push } = vi.hoisted(() => ({ push: vi.fn() }));
vi.mock("next/navigation", () => ({ useRouter: () => ({ push }) }));
vi.mock("@/services/interview-service", () => ({ interviewService: { options: vi.fn(), create: vi.fn() } }));
const options: InterviewOptions = {
  fieldCategories: [{ value: "IT", label: "IT Field" }, { value: "NON_IT", label: "Non-IT Field" }],
  domainLabels: { IT: "Technical Domain", NON_IT: "Professional Domain" },
  domains: { IT: [{ value: "JAVA", label: "Java" }, { value: "CUSTOM", label: "Custom" }], NON_IT: [{ value: "CUSTOMER_SUPPORT", label: "Customer Support" }, { value: "CUSTOM", label: "Custom" }] },
  modes: { IT: [{ value: "TECHNICAL", label: "Technical" }], NON_IT: [{ value: "BEHAVIOURAL", label: "Behavioural" }] },
  difficulties: [{ value: "MEDIUM", label: "Medium" }], experienceLevels: [{ value: "JUNIOR", label: "Junior" }],
  minimumQuestions: 5, maximumQuestions: 6, defaultQuestions: 5, customDomain: { minimumLength: 2, maximumLength: 120 }, targetRole: { minimumLength: 2, maximumLength: 150 },
};
const session = { id: "session-123" };

async function renderSetup() { vi.mocked(interviewService.options).mockResolvedValue(options); render(<InterviewSetup/>); await screen.findByText("Choose your field"); }
function choose(label: string, value: string) { fireEvent.change(screen.getByLabelText(label), { target: { value } }); }
function completeItForm() { choose("Technical Domain", "JAVA"); fireEvent.change(screen.getByLabelText("Interview topic"), { target: { value: "Spring APIs" } }); choose("Interview mode", "TECHNICAL"); fireEvent.change(screen.getByLabelText("Target role"), { target: { value: "Java Developer" } }); choose("Experience level", "JUNIOR"); choose("Difficulty", "MEDIUM"); }

describe("InterviewSetup", () => {
  beforeEach(() => { vi.clearAllMocks(); window.sessionStorage.clear(); });
  it("loads options and switches between IT and Non-IT choices", async () => { await renderSetup(); fireEvent.click(screen.getByRole("button", { name: /^IT Field/ })); expect(screen.getByLabelText("Technical Domain")).toHaveTextContent("Java"); fireEvent.click(screen.getByRole("button", { name: /^Non-IT Field/ })); expect(screen.getByLabelText("Professional Domain")).toHaveTextContent("Customer Support"); expect(screen.queryByText("Java")).not.toBeInTheDocument(); });
  it("validates blank setup and requires backend-constrained custom domain", async () => { await renderSetup(); fireEvent.click(screen.getByRole("button", { name: /^IT Field/ })); fireEvent.click(screen.getByRole("button", { name: "Start interview" })); expect(await screen.findByText("Choose a valid domain.")).toBeInTheDocument(); choose("Technical Domain", "CUSTOM"); expect(screen.getByLabelText("Custom domain")).toBeInTheDocument(); fireEvent.click(screen.getByRole("button", { name: "Start interview" })); expect(await screen.findByText("Custom domain must be 2–120 characters.")).toBeInTheDocument(); expect(interviewService.create).not.toHaveBeenCalled(); });
  it("creates an interview, clears the draft, and navigates with only its session ID", async () => { vi.mocked(interviewService.create).mockResolvedValue(session as never); await renderSetup(); fireEvent.click(screen.getByRole("button", { name: /^IT Field/ })); completeItForm(); fireEvent.click(screen.getByRole("button", { name: "Start interview" })); await waitFor(() => expect(interviewService.create).toHaveBeenCalledWith(expect.objectContaining({ fieldCategory: "IT", interviewDomain: "JAVA", customDomain: null, totalQuestions: 5 }))); expect(push).toHaveBeenCalledWith("/interview/session-123"); expect(window.sessionStorage.getItem("ai_interview_coach_setup_draft")).toBeNull(); });
  it("shows option-loading and API failure states with retry", async () => { let reject!: (reason: unknown) => void; vi.mocked(interviewService.options).mockReturnValue(new Promise((_, rejectPromise) => { reject = rejectPromise; })); render(<InterviewSetup/>); expect(screen.getByText("Loading interview options…")).toBeInTheDocument(); reject({ isAxiosError: true, response: { data: { status: 503, error: "Unavailable", message: "Options are temporarily unavailable." } } }); expect(await screen.findByText("Options are temporarily unavailable.")).toBeInTheDocument(); expect(screen.getByRole("button", { name: "Try again" })).toBeInTheDocument(); });
});
