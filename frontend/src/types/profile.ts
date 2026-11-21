export interface Profile {
  fullName: string;
  role: string;
  headline: string;
  bio: string;
  about: string;
  location: string | null;
  email: string;
  githubUrl: string | null;
  linkedinUrl: string | null;
  resumeUrl: string | null;
  avatarUrl: string | null;
  openToWork: boolean;
}

export type ProfileRequest = Profile;