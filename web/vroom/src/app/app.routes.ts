import { Routes } from '@angular/router';
import {Register} from './features/register/register'
import {Login} from './features/login/login'
import { Profile } from './features/profile/profile';
import {Navbar} from './features/navbar/navbar'

export const routes: Routes = [
    {path: 'login', component: Login},
    {path: 'profile', component: Profile },
    {path: 'register', component: Register}
];
