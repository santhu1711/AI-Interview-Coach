import axios from "axios";
import { authToken, isJwtExpired } from "@/lib/auth-token";
export const api = axios.create({ baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080", headers: { "Content-Type": "application/json" }, timeout: 20_000 });
let unauthorizedHandler: (() => void) | undefined;
export function setUnauthorizedHandler(handler?: () => void) { unauthorizedHandler = handler; }
api.interceptors.request.use((config) => {
  const token = authToken.get();
  if (token && !isJwtExpired(token)) config.headers.Authorization = `Bearer ${token}`;
  else if (token) { authToken.clear(); unauthorizedHandler?.(); }
  return config;
});
api.interceptors.response.use(response => response, error => {
  if (error.response?.status === 401 && authToken.get()) { authToken.clear(); unauthorizedHandler?.(); }
  return Promise.reject(error);
});
