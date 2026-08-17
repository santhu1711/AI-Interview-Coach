import { ProtectedRoute } from "@/components/auth/protected-route";
import { Header } from "@/components/layout/header";
import { ProfileView } from "@/components/profile/profile-view";
export default function ProfilePage() { return <ProtectedRoute><Header/><main className="mx-auto max-w-6xl px-6 py-12"><ProfileView/></main></ProtectedRoute>; }
