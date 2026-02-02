export interface AdminUser {
  id: number;
  email: string;
  blocked: boolean;
  blockedReason?: string;
}