"use client";
import { LogOut } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
export function Header() {
  const { isAuthenticated, isLoading, logout, user } = useAuth(); const router = useRouter();
  const handleLogout = () => { logout(); router.replace("/"); };
  return <header className="border-b border-zinc-800 bg-zinc-950/90"><div className="mx-auto flex min-h-16 max-w-6xl items-center justify-between gap-4 px-6"><Link className="font-bold" href="/">AI Interview Coach</Link><nav aria-label="Main navigation" className="flex items-center gap-2">{!isLoading && (isAuthenticated ? <><Link className="rounded-md px-3 py-2 text-sm text-zinc-300 hover:bg-zinc-800" href="/dashboard">Dashboard</Link><span className="hidden text-sm text-zinc-500 sm:inline">{user?.fullName}</span><button className="inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm text-zinc-300 hover:bg-zinc-800" onClick={handleLogout} type="button"><LogOut aria-hidden size={16}/>Logout</button></> : <><Link className="px-3 py-2 text-sm text-zinc-300" href="/login">Login</Link><Link className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold hover:bg-blue-500" href="/register">Start practising</Link></>)}</nav></div></header>;
}
