import { ArrowRight, BarChart3, BriefcaseBusiness, Code2, FileCheck2 } from "lucide-react";

const features = [
  { icon: Code2, title: "IT Field", text: "Technical, coding, architecture, debugging, and system design interviews." },
  { icon: BriefcaseBusiness, title: "Non-IT Field", text: "Role-specific, behavioural, customer, sales, operations, and leadership practice." },
  { icon: FileCheck2, title: "Focused reports", text: "Understand strengths, missed concepts, revision areas, and question-level feedback." },
  { icon: BarChart3, title: "Performance analytics", text: "Follow scores over time and compare performance across fields and domains." }
];

export default function Home() {
  return <main>
    <header className="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
      <strong className="text-lg">AI Interview Coach</strong>
      <nav className="flex items-center gap-3"><a className="px-4 py-2 text-sm text-zinc-300" href="/login">Login</a><a className="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold" href="/register">Start practising</a></nav>
    </header>
    <section className="border-y border-zinc-800 bg-zinc-950">
      <div className="mx-auto grid min-h-[66vh] max-w-6xl content-center gap-8 px-6 py-16">
        <p className="text-sm font-semibold text-blue-400">REALISTIC PRACTICE. USEFUL FEEDBACK.</p>
        <h1 className="max-w-4xl text-5xl font-bold leading-tight md:text-7xl">Practise smarter. Interview with confidence.</h1>
        <p className="max-w-2xl text-lg leading-8 text-zinc-300">Take realistic AI-powered interviews for IT and Non-IT careers, receive detailed feedback, and track your improvement over time.</p>
        <a className="flex w-fit items-center gap-2 rounded-md bg-blue-600 px-5 py-3 font-semibold" href="/register">Start practising <ArrowRight size={18}/></a>
      </div>
    </section>
    <section className="mx-auto grid max-w-6xl gap-px bg-zinc-800 md:grid-cols-2">
      {features.map(({icon: Icon,title,text}) => <article className="bg-zinc-950 p-8" key={title}><Icon className="mb-5 text-blue-400"/><h2 className="mb-2 text-xl font-semibold">{title}</h2><p className="leading-7 text-zinc-400">{text}</p></article>)}
    </section>
    <footer className="mx-auto max-w-6xl px-6 py-10 text-sm text-zinc-500">AI Interview Coach</footer>
  </main>;
}

