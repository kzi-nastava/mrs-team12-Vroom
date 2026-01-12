import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class AuthService{
    getCurrentUserType(): string | null{
        return localStorage.getItem('user_type')
    }
}