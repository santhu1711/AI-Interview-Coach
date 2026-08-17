import { api } from "@/services/api";
import type { AuthResponse, LoginRequest, RegisterRequest, User } from "@/types/auth";
export const authService = {
  login: async (request: LoginRequest) => (await api.post<AuthResponse>("/api/auth/login", request)).data,
  register: async (request: RegisterRequest) => (await api.post<AuthResponse>("/api/auth/register", request)).data,
  currentUser: async () => (await api.get<User>("/api/auth/me")).data,
};
