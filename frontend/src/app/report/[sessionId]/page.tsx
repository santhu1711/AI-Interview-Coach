"use client";
import { useParams } from "next/navigation";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { Header } from "@/components/layout/header";
import { ReportView } from "@/components/report/report-view";
export default function ReportPage() { const { sessionId } = useParams<{ sessionId: string }>(); return <ProtectedRoute><Header/><main className="mx-auto max-w-6xl px-6 py-12"><ReportView sessionId={sessionId}/></main></ProtectedRoute>; }
