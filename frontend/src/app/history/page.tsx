import { ProtectedRoute } from "@/components/auth/protected-route";
import { HistoryView } from "@/components/history/history-view";
import { Header } from "@/components/layout/header";
export default function HistoryPage() { return <ProtectedRoute><Header/><main className="mx-auto max-w-6xl px-6 py-12"><HistoryView/></main></ProtectedRoute>; }
