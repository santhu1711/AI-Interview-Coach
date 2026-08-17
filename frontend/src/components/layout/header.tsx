"use client";
import { LogOut } from "lucide-react";
import Link from "next/link";
import { useAuth } from "@/contexts/auth-context";
export function Header() {
  const { isAuthenticated, isLoading, logout, user } = useAuth();
  const handleLogout = () => { logout(); window.location.replace("/"); };
  return <header className="border-b border-zinc-800 bg-zinc-950/90"><div className="mx-auto flex min-h-16 max-w-6xl flex-wrap items-center justify-between gap-x-4 px-4 py-3 sm:px-6"><Link className="font-bold" href="/">AI Interview Coach</Link><nav aria-label="Main navigation" className="flex w-full items-center justify-between gap-1 sm:w-auto sm:justify-end">{!isLoading && (isAuthenticated ? <><Link className="rounded-md px-1.5 py-2 text-xs text-zinc-300 hover:bg-zinc-800 sm:px-2 sm:text-sm" href="/dashboard">Dashboard</Link><Link className="rounded-md px-1.5 py-2 text-xs text-zinc-300 hover:bg-zinc-800 sm:px-2 sm:text-sm" href="/interview/setup">Interview</Link><Link className="rounded-md px-1.5 py-2 text-xs text-zinc-300 hover:bg-zinc-800 sm:px-2 sm:text-sm" href="/history">History</Link><Link className="rounded-md px-1.5 py-2 text-xs text-zinc-300 hover:bg-zinc-800 sm:px-2 sm:text-sm" href="/profile">Profile</Link><span className="hidden text-sm text-zinc-500 lg:inline">{user?.fullName}</span><button aria-label="Logout" className="inline-flex items-center rounded-md px-1.5 py-2 text-zinc-300 hover:bg-zinc-800 sm:px-2" onClick={handleLogout} type="button"><LogOut aria-hidden size={16}/></button></> : <><Link className="px-3 py-2 text-sm text-zinc-300" href="/login">Login</Link><Link className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold hover:bg-blue-500" href="/register">Start practising</Link></>)}</nav></div></header>;
}
