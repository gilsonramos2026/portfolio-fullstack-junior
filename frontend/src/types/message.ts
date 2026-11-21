export type MessageSource = "FORM" | "CHAT";

export interface ContactMessage {
  id: number;
  name: string;
  email: string;
  message: string;
  source: MessageSource;
  read: boolean;
  createdAt: string;
}

export interface MessageRequest {
  name: string;
  email: string;
  message: string;
  source: MessageSource;
  /** Campo isca anti-spam: deve permanecer vazio. */
  website: string;
}