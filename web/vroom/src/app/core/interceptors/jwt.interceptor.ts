import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, throwError } from "rxjs";

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const token = localStorage.getItem('jwt')
    let authReq = req

    if(token){
        authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        })
    }

    return next(authReq).pipe(
        catchError((err) => {
            if(err.status === 401){
                localStorage.removeItem('jwt');
                localStorage.removeItem('expires')
                localStorage.removeItem('user_id');
                localStorage.removeItem('user_type');

                router.navigate(['/login'])
            }
            
            return throwError(() => err);
        })
    )
}