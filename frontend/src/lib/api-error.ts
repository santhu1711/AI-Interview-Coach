import axios from "axios";
import type { ApiErrorResponse } from "@/types/auth";
export function getApiError(error: unknown, fallback: string): ApiErrorResponse {
  if (axios.isAxiosError<ApiErrorResponse>(error) && error.response?.data) return error.response.data;
  return { status: 0, error: "Request failed", message: fallback };
}
