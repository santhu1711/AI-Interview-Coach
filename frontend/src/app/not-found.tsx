import Link from "next/link";

export default function NotFound() {
  return (
    <main className="grid min-h-screen place-content-center gap-4 text-center">
      <h1 className="text-4xl font-bold">Page not found</h1>
      <Link className="text-blue-400" href="/">
        Return home
      </Link>
    </main>
  );
}
