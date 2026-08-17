"use client";
import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { authToken, isJwtExpired } from "@/lib/auth-token";
import { authService } from "@/services/auth-service";
import { setUnauthorizedHandler } from "@/services/api";
import type { LoginRequest, RegisterRequest, User } from "@/types/auth";
interface AuthContextValue { user: User | null; isAuthenticated: boolean; isLoading: boolean; login: (request: LoginRequest) => Promise<void>; register: (request: RegisterRequest) => Promise<void>; logout: () => void; }
const AuthContext = createContext<AuthContextValue | undefined>(undefined);
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const logout = useCallback(() => { authToken.clear(); setUser(null); }, []);
  useEffect(() => {
    setUnauthorizedHandler(logout);
    const token = authToken.get();
    if (!token || isJwtExpired(token)) {
      Promise.resolve().then(() => { logout(); setIsLoading(false); });
    } else {
      authService.currentUser().then(setUser).catch(logout).finally(() => setIsLoading(false));
    }
    return () => setUnauthorizedHandler();
  }, [logout]);
  const acceptAuth = (response: Awaited<ReturnType<typeof authService.login>>) => { authToken.set(response.accessToken); setUser(response.user); };
  const login = async (request: LoginRequest) => acceptAuth(await authService.login(request));
  const register = async (request: RegisterRequest) => acceptAuth(await authService.register(request));
  const value = { user, isAuthenticated: Boolean(user), isLoading, login, register, logout };
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export function useAuth() { const value = useContext(AuthContext); if (!value) throw new Error("useAuth must be used within AuthProvider"); return value; }
