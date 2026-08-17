"use client";
import { ArrowRight, BarChart3, BriefcaseBusiness, Code2, FileCheck2, ListChecks, MessageSquareText, Target } from "lucide-react";
import Link from "next/link";
import { Header } from "@/components/layout/header";
import { useAuth } from "@/contexts/auth-context";
const features = [
  { icon: Code2, title: "IT Field", text: "Practise Java, Spring Boot, DSA, system design, frontend, cloud, cybersecurity, and more." },
  { icon: BriefcaseBusiness, title: "Non-IT Field", text: "Prepare for customer support, sales, HR, finance, operations, management, and other careers." },
  { icon: FileCheck2, title: "Focused reports", text: "Understand strengths, missed concepts, revision areas, and question-level feedback." },
  { icon: BarChart3, title: "Performance analytics", text: "Follow scores over time and compare performance across fields and domains." },
];
const steps = [
  { icon: ListChecks, title: "Choose your interview", text: "Select a field, domain, role, difficulty, and interview style." },
  { icon: MessageSquareText, title: "Answer realistic questions", text: "Work through one focused, adaptive question at a time." },
  { icon: Target, title: "Turn feedback into progress", text: "Review a structured report and track improvement over time." },
];
export default function Home() {
  const { isAuthenticated, isLoading } = useAuth(); const primaryHref = isAuthenticated ? "/dashboard" : "/register";
  return <main><Header/><section className="border-y border-zinc-800 bg-zinc-950"><div className="mx-auto grid min-h-[66vh] max-w-6xl content-center gap-8 px-6 py-16"><p className="text-sm font-semibold tracking-widest text-blue-400">REALISTIC PRACTICE. USEFUL FEEDBACK.</p><h1 className="max-w-4xl text-5xl font-bold leading-tight md:text-7xl">Practise smarter. Interview with confidence.</h1><p className="max-w-2xl text-lg leading-8 text-zinc-300">Take realistic AI-powered interviews for IT and Non-IT careers, receive detailed feedback, and track your improvement over time.</p><Link className="flex w-fit items-center gap-2 rounded-md bg-blue-600 px-5 py-3 font-semibold transition hover:bg-blue-500" href={primaryHref}>{isLoading ? "Loading…" : isAuthenticated ? "Go to dashboard" : "Start practising"} <ArrowRight aria-hidden size={18}/></Link></div></section>
    <section aria-label="Interview features" className="mx-auto grid max-w-6xl gap-px bg-zinc-800 md:grid-cols-2">{features.map(({icon: Icon,title,text}) => <article className="bg-zinc-950 p-8" key={title}><Icon aria-hidden className="mb-5 text-blue-400"/><h2 className="mb-2 text-xl font-semibold">{title}</h2><p className="leading-7 text-zinc-400">{text}</p></article>)}</section>
    <section className="mx-auto max-w-6xl px-6 py-20"><p className="text-sm font-semibold tracking-widest text-blue-400">HOW IT WORKS</p><h2 className="mt-3 text-3xl font-bold">A clear path from practice to progress</h2><div className="mt-10 grid gap-6 md:grid-cols-3">{steps.map(({icon: Icon,title,text}) => <article className="rounded-xl border border-zinc-800 bg-zinc-950 p-6" key={title}><Icon aria-hidden className="mb-5 text-blue-400"/><h3 className="text-lg font-semibold">{title}</h3><p className="mt-2 leading-7 text-zinc-400">{text}</p></article>)}</div><p className="mt-10 text-sm text-zinc-500">Supported domains span software engineering, data, infrastructure, business, communication, operations, education, healthcare, and custom roles.</p></section>
    <footer className="border-t border-zinc-800"><div className="mx-auto max-w-6xl px-6 py-10 text-sm text-zinc-500">AI Interview Coach · Local interview practice with private, account-scoped progress.</div></footer></main>;
}
