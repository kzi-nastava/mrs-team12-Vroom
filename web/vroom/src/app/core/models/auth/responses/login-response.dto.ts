export interface LoginResponseDTO {
  userID: number;
  type: string;
  token: string;
  expires: number;
}