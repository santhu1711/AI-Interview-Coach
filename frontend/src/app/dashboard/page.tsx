import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { Header } from "@/components/layout/header";
export default function DashboardPage() { return <ProtectedRoute><Header/><main className="mx-auto max-w-6xl px-6 py-12"><p className="text-sm font-semibold tracking-widest text-blue-400">YOUR WORKSPACE</p><h1 className="mt-3 text-4xl font-bold">Dashboard</h1><div className="mt-8 rounded-xl border border-zinc-800 bg-zinc-950 p-8"><h2 className="text-xl font-semibold">Ready for your next interview?</h2><p className="mt-2 max-w-2xl leading-7 text-zinc-400">Choose an IT or Non-IT path and configure a focused practice session.</p><Link className="mt-6 inline-flex items-center gap-2 rounded-md bg-blue-600 px-4 py-3 font-semibold hover:bg-blue-500" href="/interview/setup">Start an interview <ArrowRight aria-hidden size={18}/></Link></div></main></ProtectedRoute>; }
