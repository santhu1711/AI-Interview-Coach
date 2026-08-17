"use client";
import { useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";
import { LoadingState } from "@/components/ui/status";
import { useAuth } from "@/contexts/auth-context";
export function ProtectedRoute({ children }: { children: React.ReactNode }) { const { isAuthenticated, isLoading } = useAuth(); const pathname = usePathname(); const router = useRouter(); useEffect(() => { if (!isLoading && !isAuthenticated) router.replace(`/login?next=${encodeURIComponent(pathname)}`); }, [isAuthenticated, isLoading, pathname, router]); if (isLoading || !isAuthenticated) return <main className="grid min-h-screen place-items-center"><LoadingState label="Checking your session…"/></main>; return children; }
