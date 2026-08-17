import { ProtectedRoute } from "@/components/auth/protected-route";
import { DashboardView } from "@/components/dashboard/dashboard-view";
import { Header } from "@/components/layout/header";
export default function DashboardPage() { return <ProtectedRoute><Header/><main className="mx-auto max-w-6xl px-6 py-12"><DashboardView/></main></ProtectedRoute>; }
