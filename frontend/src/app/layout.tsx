import type { Metadata } from "next";
import "./globals.css";
export const metadata: Metadata = { title: "AI Interview Coach", description: "Realistic AI interviews for IT and non-IT careers" };
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body>{children}</body></html>;
}

