import { Routes } from '@angular/router';
import {Login} from './features/login/login'
import { Profile } from './features/profile/profile';

export const routes: Routes = [
    {path: 'login', component: Login},
    { path: 'profile', component: Profile }
];
