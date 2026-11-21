import { Alert } from "@/components/ui/Alert";
import { SkipLink } from "@/components/ui/SkipLink";
import { ChatWidget } from "@/components/chat/ChatWidget";
import { Footer } from "@/components/layout/Footer";
import { About } from "@/components/sections/About";
import { Contact } from "@/components/sections/Contact";
import { Courses } from "@/components/sections/Courses";
import { Hero } from "@/components/sections/Hero";
import { Projects } from "@/components/sections/Projects";
import { Skills } from "@/components/sections/Skills";
import { defaultProfile } from "@/lib/site";
import { getEducation, getProfile, getProjects, getSkills } from "@/lib/api-server";
import { Header } from "@/components/layout/Header";

export const dynamic = "force-dynamic";

export default async function HomePage() {
  const [profile, projects, skills, education] = await Promise.all([
    getProfile(),
    getProjects(),
    getSkills(),
    getEducation(),
  ]);

  const personJsonLd = {
    "@context": "https://schema.org",
    "@type": "Person",
    name: profile.fullName,
    jobTitle: profile.role,
    email: profile.email,
    sameAs: [profile.githubUrl, profile.linkedinUrl].filter(Boolean),
  };

  return (
    <>
      <script type="application/ld+json" dangerouslySetInnerHTML={{ __html: JSON.stringify(personJsonLd) }} />
      <SkipLink />
      <Header name={profile.fullName} />
      <main id="conteudo" tabIndex={-1} className="outline-none">
        {profile === defaultProfile && (
          <div className="container pt-4">
            <Alert tone="error">Não foi possível carregar os dados do portfólio agora. Tente novamente em instantes.</Alert>
          </div>
        )}
        <Hero profile={profile} skills={skills} />
        <About profile={profile} education={education} />
        <Courses education={education} />
        <Skills skills={skills} />
        <Projects projects={projects} />
        <Contact profile={profile} />
      </main>
      <Footer profile={profile} />
      <ChatWidget profile={profile} skills={skills} projects={projects} education={education} />
    </>
  );
}
