export interface Project {
  id: number;
  title: string;
  description: string;
  imageUrl: string | null;
  githubUrl: string | null;
  deployUrl: string | null;
  longDescription: string | null;
  featured: boolean;
  technologies: string[];
  features: string[];
  screenshots: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ProjectRequest {
  title: string;
  description: string;
  imageUrl: string | null;
  githubUrl: string | null;
  deployUrl: string | null;
  longDescription: string | null;
  featured: boolean;
  technologies: string[];
  features: string[];
  screenshots: string[];
}