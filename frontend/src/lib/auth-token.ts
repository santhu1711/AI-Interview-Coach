const TOKEN_KEY = "ai_interview_coach_access_token";
export const authToken = {
  get: () => typeof window === "undefined" ? null : window.localStorage.getItem(TOKEN_KEY),
  set: (token: string) => { if (typeof window !== "undefined") window.localStorage.setItem(TOKEN_KEY, token); },
  clear: () => { if (typeof window !== "undefined") window.localStorage.removeItem(TOKEN_KEY); },
};
export function isJwtExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))) as { exp?: number };
    return typeof payload.exp !== "number" || payload.exp * 1000 <= Date.now();
  } catch { return true; }
}
