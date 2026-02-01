export interface ResetPasswordRequestDTO{
    email: string;
    code: string;
    password: string;
    confirmPassword: string;
} 