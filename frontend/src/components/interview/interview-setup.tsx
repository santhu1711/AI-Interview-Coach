"use client";
import { BriefcaseBusiness, Code2 } from "lucide-react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { ErrorState, LoadingState } from "@/components/ui/status";
import { FormField } from "@/components/ui/form-field";
import { SelectField } from "@/components/ui/select-field";
import { getApiError } from "@/lib/api-error";
import { interviewService } from "@/services/interview-service";
import type { CreateInterviewRequest, FieldCategory, InterviewOptions } from "@/types/interview";

const DRAFT_KEY = "ai_interview_coach_setup_draft";
type Draft = Omit<CreateInterviewRequest, "fieldCategory" | "customDomain"> & { fieldCategory: FieldCategory | ""; customDomain: string };
const emptyDraft: Draft = { fieldCategory: "", interviewDomain: "", customDomain: "", topic: "", difficulty: "", interviewMode: "", targetRole: "", experienceLevel: "", totalQuestions: 0 };

function restoredDraft(options: InterviewOptions): Draft {
  const fallback = { ...emptyDraft, totalQuestions: options.defaultQuestions };
  try {
    const raw = window.sessionStorage.getItem(DRAFT_KEY);
    if (!raw) return fallback;
    const saved = JSON.parse(raw) as Partial<Draft>;
    return { ...fallback, ...saved, totalQuestions: Number(saved.totalQuestions) || options.defaultQuestions };
  } catch { return fallback; }
}

export function InterviewSetup() {
  const router = useRouter();
  const [options, setOptions] = useState<InterviewOptions | null>(null);
  const [draft, setDraft] = useState<Draft>(emptyDraft);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loadError, setLoadError] = useState("");
  const [submitError, setSubmitError] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const loadOptions = () => {
    setLoading(true); setLoadError("");
    interviewService.options().then(data => { setOptions(data); setDraft(restoredDraft(data)); }).catch(error => setLoadError(getApiError(error, "Unable to load interview options. Check your connection and try again.").message)).finally(() => setLoading(false));
  };
  useEffect(() => {
    interviewService.options().then(data => { setOptions(data); setDraft(restoredDraft(data)); }).catch(error => setLoadError(getApiError(error, "Unable to load interview options. Check your connection and try again.").message)).finally(() => setLoading(false));
  }, []);
  useEffect(() => { if (options) window.sessionStorage.setItem(DRAFT_KEY, JSON.stringify(draft)); }, [draft, options]);

  const update = <K extends keyof Draft>(key: K, value: Draft[K]) => { setDraft(current => ({ ...current, [key]: value })); setErrors(current => ({ ...current, [key]: "" })); };
  const selectCategory = (category: FieldCategory) => setDraft(current => ({ ...current, fieldCategory: category, interviewDomain: "", interviewMode: "", customDomain: "" }));
  const validate = () => {
    if (!options) return false;
    const next: Record<string, string> = {};
    if (!draft.fieldCategory) next.fieldCategory = "Choose IT Field or Non-IT Field.";
    const domains = draft.fieldCategory ? options.domains[draft.fieldCategory] : [];
    const modes = draft.fieldCategory ? options.modes[draft.fieldCategory] : [];
    if (!domains.some(item => item.value === draft.interviewDomain)) next.interviewDomain = "Choose a valid domain.";
    if (draft.interviewDomain === "CUSTOM" && (draft.customDomain.trim().length < options.customDomain.minimumLength || draft.customDomain.trim().length > options.customDomain.maximumLength)) next.customDomain = `Custom domain must be ${options.customDomain.minimumLength}–${options.customDomain.maximumLength} characters.`;
    if (!draft.topic.trim()) next.topic = "Topic is required."; else if (draft.topic.trim().length > 200) next.topic = "Topic must not exceed 200 characters.";
    if (!modes.some(item => item.value === draft.interviewMode)) next.interviewMode = "Choose a valid interview mode.";
    if (draft.targetRole.trim().length < options.targetRole.minimumLength || draft.targetRole.trim().length > options.targetRole.maximumLength) next.targetRole = `Target role must be ${options.targetRole.minimumLength}–${options.targetRole.maximumLength} characters.`;
    if (!options.experienceLevels.some(item => item.value === draft.experienceLevel)) next.experienceLevel = "Choose your experience level.";
    if (!options.difficulties.some(item => item.value === draft.difficulty)) next.difficulty = "Choose a difficulty.";
    if (draft.totalQuestions < options.minimumQuestions || draft.totalQuestions > options.maximumQuestions) next.totalQuestions = `Choose between ${options.minimumQuestions} and ${options.maximumQuestions} questions.`;
    setErrors(next); return Object.keys(next).length === 0;
  };
  const submit = async (event: React.FormEvent) => {
    event.preventDefault(); if (submitting || !validate() || !draft.fieldCategory) return;
    setSubmitting(true); setSubmitError("");
    try {
      const session = await interviewService.create({ ...draft, fieldCategory: draft.fieldCategory, customDomain: draft.interviewDomain === "CUSTOM" ? draft.customDomain.trim() : null, topic: draft.topic.trim(), targetRole: draft.targetRole.trim() });
      window.sessionStorage.removeItem(DRAFT_KEY); router.push(`/interview/${session.id}`);
    } catch (error) {
      const response = getApiError(error, "Unable to start the interview. Your choices have been saved; please try again.");
      if (response.fieldErrors) setErrors(current => ({ ...current, ...response.fieldErrors }));
      setSubmitError(response.message);
    } finally { setSubmitting(false); }
  };

  if (loading) return <LoadingState label="Loading interview options…"/>;
  if (loadError || !options) return <ErrorState message={loadError || "No interview options are available."} onRetry={loadOptions}/>;
  const category = draft.fieldCategory;
  const questionOptions = Array.from({ length: options.maximumQuestions - options.minimumQuestions + 1 }, (_, index) => ({ value: String(options.minimumQuestions + index), label: String(options.minimumQuestions + index) }));
  return <form className="grid gap-8" noValidate onSubmit={submit}>
    <fieldset><legend className="text-lg font-semibold">Choose your field</legend><div className="mt-4 grid gap-4 sm:grid-cols-2"><button aria-pressed={category === "IT"} className={`rounded-xl border p-6 text-left transition ${category === "IT" ? "border-blue-500 bg-blue-950/40" : "border-zinc-800 bg-zinc-950 hover:border-zinc-600"}`} onClick={() => selectCategory("IT")} type="button"><Code2 aria-hidden className="mb-4 text-blue-400"/><span className="block text-xl font-bold">IT Field</span><span className="mt-2 block text-sm text-zinc-400">Technical, coding, systems, data, and software roles.</span></button><button aria-pressed={category === "NON_IT"} className={`rounded-xl border p-6 text-left transition ${category === "NON_IT" ? "border-blue-500 bg-blue-950/40" : "border-zinc-800 bg-zinc-950 hover:border-zinc-600"}`} onClick={() => selectCategory("NON_IT")} type="button"><BriefcaseBusiness aria-hidden className="mb-4 text-blue-400"/><span className="block text-xl font-bold">Non-IT Field</span><span className="mt-2 block text-sm text-zinc-400">Business, service, operations, people, and professional roles.</span></button></div>{errors.fieldCategory && <p className="mt-2 text-sm text-red-400" role="alert">{errors.fieldCategory}</p>}</fieldset>
    {category && <div className="grid gap-5 rounded-xl border border-zinc-800 bg-zinc-950 p-6 sm:grid-cols-2">
      <SelectField error={errors.interviewDomain} id="interviewDomain" label={options.domainLabels[category] ?? "Domain"} onChange={event => update("interviewDomain", event.target.value)} options={options.domains[category]} value={draft.interviewDomain}/>
      {draft.interviewDomain === "CUSTOM" && <FormField error={errors.customDomain} id="customDomain" label="Custom domain" maxLength={options.customDomain.maximumLength} onChange={event => update("customDomain", event.target.value)} value={draft.customDomain}/>}
      <FormField error={errors.topic} id="topic" label="Interview topic" maxLength={200} onChange={event => update("topic", event.target.value)} placeholder="e.g. REST API design" value={draft.topic}/>
      <SelectField error={errors.interviewMode} id="interviewMode" label="Interview mode" onChange={event => update("interviewMode", event.target.value)} options={options.modes[category]} value={draft.interviewMode}/>
      <FormField error={errors.targetRole} id="targetRole" label="Target role" maxLength={options.targetRole.maximumLength} onChange={event => update("targetRole", event.target.value)} placeholder="e.g. Java Developer" value={draft.targetRole}/>
      <SelectField error={errors.experienceLevel} id="experienceLevel" label="Experience level" onChange={event => update("experienceLevel", event.target.value)} options={options.experienceLevels} value={draft.experienceLevel}/>
      <SelectField error={errors.difficulty} id="difficulty" label="Difficulty" onChange={event => update("difficulty", event.target.value)} options={options.difficulties} value={draft.difficulty}/>
      <SelectField error={errors.totalQuestions} id="totalQuestions" label="Number of questions" onChange={event => update("totalQuestions", Number(event.target.value))} options={questionOptions} value={String(draft.totalQuestions)}/>
    </div>}
    {submitError && <ErrorState message={submitError}/>}<Button className="w-full sm:w-fit" disabled={submitting || !category} type="submit">{submitting ? "Starting interview…" : "Start interview"}</Button>
  </form>;
}
