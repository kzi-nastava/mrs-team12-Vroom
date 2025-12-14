import { Routes } from '@angular/router';
import {Register} from './features/register/register'
import {Login} from './features/login/login'

export const routes: Routes = [
    {path: 'login', component: Login},
    {path: 'register', component: Register}
];
