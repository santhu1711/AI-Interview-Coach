import { ProtectedRoute } from "@/components/auth/protected-route";
import { InterviewSetup } from "@/components/interview/interview-setup";
import { Header } from "@/components/layout/header";
export default function InterviewSetupPage() { return <ProtectedRoute><Header/><main className="mx-auto max-w-4xl px-6 py-12"><p className="text-sm font-semibold tracking-widest text-blue-400">NEW INTERVIEW</p><h1 className="mt-3 text-4xl font-bold">Build your practice session</h1><p className="mt-3 mb-10 max-w-2xl leading-7 text-zinc-400">Choose a field first. The available domains and modes come directly from the interview service.</p><InterviewSetup/></main></ProtectedRoute>; }
