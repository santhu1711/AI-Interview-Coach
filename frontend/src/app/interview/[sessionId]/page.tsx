"use client";
import { useParams } from "next/navigation";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { LiveInterview } from "@/components/interview/live-interview";
import { Header } from "@/components/layout/header";
export default function InterviewPage() { const { sessionId } = useParams<{ sessionId: string }>(); return <ProtectedRoute><Header/><LiveInterview sessionId={sessionId}/></ProtectedRoute>; }
