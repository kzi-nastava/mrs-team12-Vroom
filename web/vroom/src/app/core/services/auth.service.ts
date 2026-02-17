import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { BehaviorSubject, finalize, Observable } from "rxjs";
import { ResetPasswordRequestDTO } from "../models/auth/requests/reset-password-request.dto";
import { ForgotPasswordRequestDTO } from "../models/auth/requests/forgot-password-request.dto";
import { LoginRequestDTO } from "../models/auth/requests/login-request.dto";

@Injectable({
    providedIn: 'root'
})
export class AuthService{
    private api = 'http://localhost:8080/api/auth'
    private loggedInStatus = new BehaviorSubject<boolean>(!!localStorage.getItem('jwt'))
    isLoggedIn$ = this.loggedInStatus.asObservable();

    constructor(private http: HttpClient) {}

    createForgotPasswordRequest(data: ForgotPasswordRequestDTO): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(this.api+'/forgot-password', data);
    }

    createLoginRequest(data: LoginRequestDTO): Observable<LoginResponseDTO>{
        return this.http.post<LoginResponseDTO>(this.api+'/login', data);
    }

    createResetPasswordRequest(data: ResetPasswordRequestDTO): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(this.api+'/reset-password', data)
    }

    createRegisterRequest(data: FormData): Observable<MessageResponseDTO> {
        return this.http.post<MessageResponseDTO>(this.api+'/register', data)
    }

    createLogoutRequest(type: string): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(`${this.api}/logout`, { type });
    }

    isPasswordValid(password: string): String | null{
        if(password.length < 8) return 'Password must be over 8 characters long'
        if(!/[0-9]/.test(password)) return 'Password must contain a number';
        if(!/[a-z]/.test(password)) return 'Password must contain a lowercase letter'
        if (!/[A-Z]/.test(password)) return 'Password must contain an uppercase letter';

        return null
    }


    
    get getCurrentUserType(): string | null{
        return localStorage.getItem('user_type')
    }

    updateStatus() {
        this.loggedInStatus.next(!!localStorage.getItem('jwt'));
    }

    get isLoggedIn(): boolean{
        const token = localStorage.getItem('jwt');
        if (!token) return false;

        return true;
    }

    logout(): Observable<void>{
        const type = localStorage.getItem('user_type');

       if (type) {
        return new Observable(observer => {
            this.createLogoutRequest(type)
                .pipe(
                    finalize(() => {
                        this.completeLocalLogout();
                        observer.next();
                        observer.complete();
                    })
                )
                .subscribe();
        });
    } else {
        this.completeLocalLogout();
        return new Observable(observer => {
            observer.next();
            observer.complete();
        });
    }
    }

    private completeLocalLogout() {
        localStorage.removeItem('jwt');
        localStorage.removeItem('expires')
        localStorage.removeItem('user_type');
    }
}