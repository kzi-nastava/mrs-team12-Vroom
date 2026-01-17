import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
    const router = inject(Router)
    const authService = inject(AuthService)

    if(!authService.isLoggedIn){
        router.navigate(['/login'])
        return false
    }

    const userType = localStorage.getItem('user_type')
    const expectedRoles = route.data['roles'] as Array<string>;

    if(expectedRoles && !expectedRoles.includes(userType || '')){
        router.navigate(['/login'])
        return false
    }

    return true
}