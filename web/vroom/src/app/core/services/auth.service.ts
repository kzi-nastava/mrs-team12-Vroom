import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AuthService{
    private api = 'http://localhost:8080/api/auth'

    constructor(private http: HttpClient) {}

    createForgotPasswordRequest(data: any): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(this.api+'/forgot-password', data);
    }

    createLoginRequest(data: any): Observable<LoginResponseDTO>{
        return this.http.post<LoginResponseDTO>(this.api+'/login', data);
    }

    createResetPasswordRequest(data: any): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(this.api+'/reset-password', data)
    }

    createRegisterRequest(data: any): Observable<MessageResponseDTO> {
        return this.http.post<MessageResponseDTO>(this.api+'/register', data)
    }

    createLogoutRequest(id: string, type: string): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(`${this.api}/logout`, { id, type });
    }

    isPasswordValid(password: string): String | null{
        if(password.length < 8) return 'Password must be over 8 characters long'
        if(!/[0-9]/.test(password)) return 'Password must contain a number';
        if(!/[a-z]/.test(password)) return 'Password must contain a lowercase letter'
        if (!/[A-Z]/.test(password)) return 'Password must contain an uppercase letter';

        return null
    }


    get isTokenExpired(): boolean{
        const expiresAt = localStorage.getItem('expires')
        if (!expiresAt) return true

        const expirationTime = Number(expiresAt)*1000
        const currentTime = Date.now()
        
        return currentTime > expirationTime
    }

    get getCurrentUserType(): string | null{
        return localStorage.getItem('user_type')
    }

    get isLoggedIn(): boolean{
        const token = localStorage.getItem('jwt');
        if (!token) return false;

        if (this.isTokenExpired) {
            this.logout(); 
            return false;
        }
        
        return true;
    }

    logout(){
        const id = localStorage.getItem('user_id');
        const type = localStorage.getItem('user_type');

        if (id && type) {
            this.createLogoutRequest(id, type).subscribe({
                next: () => this.completeLocalLogout(),
                error: () => this.completeLocalLogout() 
            });
        } else {
            this.completeLocalLogout();
        }
    }

    private completeLocalLogout() {
        localStorage.removeItem('jwt');
        localStorage.removeItem('expires')
        localStorage.removeItem('user_id');
        localStorage.removeItem('user_type');
    }
}