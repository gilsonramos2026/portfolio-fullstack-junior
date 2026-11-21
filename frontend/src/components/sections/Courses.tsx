import { Clock } from "lucide-react";
import { EntryRow } from "@/components/ui/EntryRow";
import { Section } from "@/components/ui/Section";
import { TextLink } from "@/components/ui/TextLink";
import { formatMonthYear } from "@/lib/utils";
import type { Education } from "@/types";

export function Courses({ education }: { education: Education[] }) {
  const courses = education.filter((item) => item.type === "COURSE");
  if (courses.length === 0) return null;

  return (
    <Section id="cursos" title="Cursos e certificações" description="Aprendizado contínuo além da graduação.">
      <ol className="divide-y divide-border border-y border-border">
        {courses.map((course) => (
          <EntryRow
            key={course.id}
            period={`${formatMonthYear(course.startDate)} — ${course.endDate ? formatMonthYear(course.endDate) : "em andamento"}`}
            title={course.title}
            subtitle={course.institution}
            description={course.description}
            aside={
              <>
                {course.workloadHours && (
                  <span className="inline-flex items-center gap-1.5 text-muted-foreground">
                    <Clock className="h-3.5 w-3.5" aria-hidden /> {course.workloadHours} h
                  </span>
                )}
                {course.credentialUrl && <TextLink href={course.credentialUrl}>Ver certificado</TextLink>}
              </>
            }
          />
        ))}
      </ol>
    </Section>
  );
}
