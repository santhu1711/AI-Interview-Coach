import { api } from "@/services/api";
import type { Profile } from "@/types/profile";
export const profileService = {
  get: async () => (await api.get<Profile>("/api/profile")).data,
  update: async (fullName: string) => (await api.put<Profile>("/api/profile", { fullName })).data,
};
