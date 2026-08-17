"use client";
import { CheckCircle2, Clock3, Send, XCircle } from "lucide-react";
import Link from "next/link";
import { useEffect, useRef, useState } from "react";
import { Button } from "@/components/ui/button";
import { ConfirmationDialog } from "@/components/ui/confirmation-dialog";
import { ErrorState, LoadingState } from "@/components/ui/status";
import { getApiError } from "@/lib/api-error";
import { optionLabel, statusLabel } from "@/lib/interview-labels";
import { interviewService } from "@/services/interview-service";
import type { Interview } from "@/types/interview";

const ANSWER_MAX = 10_000;
function formatElapsed(startedAt: string | null, now: number) {
  if (!startedAt) return "00:00";
  const seconds = Math.max(0, Math.floor((now - new Date(startedAt).getTime()) / 1000));
  return `${String(Math.floor(seconds / 60)).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`;
}
function badge(text: string) { return <span className="rounded-full border border-zinc-700 bg-zinc-900 px-3 py-1 text-xs font-semibold text-zinc-300">{text}</span>; }

export function LiveInterview({ sessionId }: { sessionId: string }) {
  const [session, setSession] = useState<Interview | null>(null);
  const [answer, setAnswer] = useState("");
  const [error, setError] = useState("");
  const [answerError, setAnswerError] = useState("");
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [dialog, setDialog] = useState<"complete" | "abandon" | null>(null);
  const [now, setNow] = useState(() => Date.now());
  const transcriptEnd = useRef<HTMLDivElement>(null);

  const loadSession = () => {
    setLoading(true); setError("");
    interviewService.get(sessionId).then(setSession).catch(requestError => setError(getApiError(requestError, "Unable to load this interview. Check your connection and try again.").message)).finally(() => setLoading(false));
  };
  useEffect(() => {
    interviewService.get(sessionId).then(setSession).catch(requestError => setError(getApiError(requestError, "Unable to load this interview. Check your connection and try again.").message)).finally(() => setLoading(false));
  }, [sessionId]);
  useEffect(() => { const timer = window.setInterval(() => setNow(Date.now()), 1000); return () => window.clearInterval(timer); }, []);
  useEffect(() => { transcriptEnd.current?.scrollIntoView({ behavior: "smooth", block: "end" }); }, [session?.messages.length, processing]);

  const active = session?.status === "IN_PROGRESS" || session?.status === "CREATED";
  const submitAnswer = async () => {
    const clean = answer.trim();
    if (!session || !active || processing) return;
    if (!clean) { setAnswerError("Enter an answer before submitting."); return; }
    if (clean.length > ANSWER_MAX) { setAnswerError(`Answer must not exceed ${ANSWER_MAX.toLocaleString()} characters.`); return; }
    setProcessing(true); setAnswerError("");
    try { const updated = await interviewService.answer(session.id, clean); setSession(updated); setAnswer(""); }
    catch (requestError) { setAnswerError(getApiError(requestError, "Your answer was not submitted. It has been preserved; please try again.").message); }
    finally { setProcessing(false); }
  };
  const confirmTransition = async () => {
    if (!session || !dialog || processing) return;
    setProcessing(true); setAnswerError("");
    try { setSession(dialog === "complete" ? await interviewService.complete(session.id) : await interviewService.abandon(session.id)); setDialog(null); }
    catch (requestError) { setAnswerError(getApiError(requestError, `Unable to ${dialog} the interview. Please try again.`).message); setDialog(null); }
    finally { setProcessing(false); }
  };
  const handleKeyDown = (event: React.KeyboardEvent<HTMLTextAreaElement>) => { if (event.key === "Enter" && !event.shiftKey) { event.preventDefault(); void submitAnswer(); } };

  if (loading) return <LoadingState label="Restoring your interview…"/>;
  if (error || !session) return <ErrorState message={error || "Interview not found."} onRetry={loadSession}/>;
  const visibleMessages = session.messages.filter(message => message.role !== "SYSTEM");
  const currentQuestion = [...visibleMessages].reverse().find(message => message.role === "ASSISTANT");
  const domain = session.customDomain || optionLabel([], session.interviewDomain);
  return <>
    <section className="border-b border-zinc-800 bg-zinc-950"><div className="mx-auto max-w-5xl px-6 py-6"><div className="flex flex-wrap items-start justify-between gap-5"><div><div className="flex flex-wrap gap-2">{badge(session.fieldCategory === "IT" ? "IT Field" : "Non-IT Field")}{badge(domain)}{badge(optionLabel([], session.difficulty))}{badge(optionLabel([], session.interviewMode))}</div><h1 className="mt-4 text-2xl font-bold">{session.topic}</h1><p className="mt-1 text-sm text-zinc-400">Target role: {session.targetRole}</p></div><div className="flex flex-wrap items-center gap-3"><span className="inline-flex items-center gap-2 text-sm text-zinc-400"><Clock3 aria-hidden size={17}/>{formatElapsed(session.startedAt, now)}</span>{active && <><button className="rounded-md border border-zinc-700 px-3 py-2 text-sm font-semibold hover:bg-zinc-800" disabled={processing} onClick={() => setDialog("complete")} type="button">End interview</button><button className="rounded-md border border-red-900 px-3 py-2 text-sm font-semibold text-red-300 hover:bg-red-950" disabled={processing} onClick={() => setDialog("abandon")} type="button">Abandon</button></>}</div></div>
      <div className="mt-6 flex items-center justify-between text-sm"><span>Question {Math.max(session.currentQuestionNumber, currentQuestion?.questionNumber ?? 1)} of {session.totalQuestions}</span><span>{statusLabel(session.status)}</span></div><div aria-label={`${session.progressPercentage}% complete`} className="mt-2 h-2 overflow-hidden rounded-full bg-zinc-800" role="progressbar" aria-valuemax={100} aria-valuemin={0} aria-valuenow={session.progressPercentage}><div className="h-full rounded-full bg-blue-500 transition-all" style={{ width: `${session.progressPercentage}%` }}/></div></div></section>
    <main className="mx-auto grid max-w-5xl gap-6 px-4 py-8 sm:px-6">
      {!active && <div className={`rounded-xl border p-5 ${session.status === "ABANDONED" ? "border-red-900 bg-red-950/30" : "border-green-900 bg-green-950/30"}`}>{session.status === "ABANDONED" ? <XCircle aria-hidden className="text-red-400"/> : <CheckCircle2 aria-hidden className="text-green-400"/>}<h2 className="mt-3 text-xl font-bold">{session.status === "ABANDONED" ? "Interview abandoned" : "Interview complete"}</h2><p className="mt-2 text-zinc-400">{session.status === "ABANDONED" ? "This session is closed and cannot receive more answers." : "Your responses are saved and ready for a detailed evaluation report."}</p><Link className="mt-4 inline-block font-semibold text-blue-400" href={session.status === "ABANDONED" ? "/dashboard" : `/report/${session.id}`}>{session.status === "ABANDONED" ? "Return to dashboard" : "View results"}</Link></div>}
      <section aria-label="Interview transcript" className="grid max-h-[55vh] gap-4 overflow-y-auto rounded-xl border border-zinc-800 bg-zinc-950 p-4 sm:p-6">{visibleMessages.length === 0 && <p className="py-10 text-center text-zinc-500">The interviewer is preparing the first question.</p>}{visibleMessages.map(message => <article className={`max-w-[88%] rounded-xl px-4 py-3 ${message.role === "USER" ? "ml-auto bg-blue-600 text-white" : "mr-auto border border-zinc-700 bg-zinc-900"}`} key={message.id}><div className="mb-2 flex items-center justify-between gap-4 text-xs opacity-70"><span>{message.role === "USER" ? "You" : "Interviewer"}</span>{message.questionNumber && <span>Question {message.questionNumber}</span>}</div><p className="whitespace-pre-wrap leading-7">{message.content}</p><time className="mt-2 block text-right text-xs opacity-60" dateTime={message.createdAt}>{new Date(message.createdAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}</time></article>)}{processing && <div aria-live="polite" className="mr-auto flex items-center gap-2 rounded-xl border border-zinc-700 bg-zinc-900 px-4 py-3 text-sm text-zinc-400" role="status"><span className="flex gap-1" aria-hidden><i className="h-2 w-2 animate-pulse rounded-full bg-blue-400"/><i className="h-2 w-2 animate-pulse rounded-full bg-blue-400 [animation-delay:150ms]"/><i className="h-2 w-2 animate-pulse rounded-full bg-blue-400 [animation-delay:300ms]"/></span>AI is evaluating your answer…</div>}<div ref={transcriptEnd}/></section>
      {active && <section><label className="font-semibold" htmlFor="answer">Your answer</label><textarea aria-describedby="answer-help" aria-invalid={Boolean(answerError)} className="mt-2 min-h-36 w-full resize-y rounded-xl border border-zinc-700 bg-zinc-950 p-4 leading-7 placeholder:text-zinc-600" disabled={processing} id="answer" maxLength={ANSWER_MAX} onChange={event => { setAnswer(event.target.value); setAnswerError(""); }} onKeyDown={handleKeyDown} placeholder="Write your answer. Press Enter to submit or Shift+Enter for a new line." value={answer}/><div className="mt-2 flex flex-wrap items-start justify-between gap-3"><div id="answer-help">{answerError ? <p className="text-sm text-red-400" role="alert">{answerError}</p> : <p className="text-xs text-zinc-500">Your answer is evaluated privately after submission.</p>}</div><span className="text-xs text-zinc-500">{answer.length.toLocaleString()} / {ANSWER_MAX.toLocaleString()}</span></div><Button className="mt-4 w-full gap-2 sm:w-auto" disabled={processing || !answer.trim()} onClick={() => void submitAnswer()} type="button"><Send aria-hidden size={18}/>{processing ? "AI is working…" : "Submit answer"}</Button></section>}
    </main>
    <ConfirmationDialog busy={processing} confirmLabel="Complete interview" description="This closes the active session. You will not be able to submit more answers." onCancel={() => setDialog(null)} onConfirm={() => void confirmTransition()} open={dialog === "complete"} title="Complete this interview?"/>
    <ConfirmationDialog busy={processing} confirmLabel="Abandon interview" description="This marks the session as abandoned and permanently stops further answers." destructive onCancel={() => setDialog(null)} onConfirm={() => void confirmTransition()} open={dialog === "abandon"} title="Abandon this interview?"/>
  </>;
}
