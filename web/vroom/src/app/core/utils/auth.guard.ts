import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
    const router = inject(Router)
    const token = localStorage.getItem('jwt')
    const userType = localStorage.getItem('user_type')

    if(!token){
        router.navigate(['/login'])
        return false
    }

    const expectedRoles = route.data['roles'] as Array<string>;

    if(expectedRoles && !expectedRoles.includes(userType || '')){
        router.navigate(['/'])
        return false
    }

    return true
}