export interface User { id: number; fullName: string; email: string; }
export interface AuthResponse { accessToken: string; tokenType: string; expiresIn: number; user: User; }
export interface LoginRequest { email: string; password: string; }
export interface RegisterRequest { fullName: string; email: string; password: string; confirmPassword: string; }
export interface ApiErrorResponse { timestamp?: string; status: number; error: string; message: string; path?: string; fieldErrors?: Record<string, string>; }
