export const EDUCATION_TYPES = ["DEGREE", "COURSE"] as const;
export type EducationType = (typeof EDUCATION_TYPES)[number];

export const EDUCATION_TYPE_LABELS: Record<EducationType, string> = {
  DEGREE: "Formação acadêmica",
  COURSE: "Curso / certificação",
};

export interface Education {
  id: number;
  title: string;
  institution: string;
  type: EducationType;
  description: string | null;
  startDate: string;
  endDate: string | null;
  credentialUrl: string | null;
  workloadHours: number | null;
  current: boolean;
}

export interface EducationRequest {
  title: string;
  institution: string;
  type: EducationType;
  description: string | null;
  startDate: string;
  endDate: string | null;
  credentialUrl: string | null;
  workloadHours: number | null;
}