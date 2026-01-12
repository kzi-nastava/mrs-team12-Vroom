import { Routes } from '@angular/router';
import {Register} from './features/register/register'
import {Login} from './features/login/login'
import { ForgotPassword } from './features/forgot-password/forgot-password';
import { Profile } from './features/profile/profile';
import { DriverRideHistory } from './features/driver-ride-history/driver-ride-history';
import { CancelStopRide } from './features/cancel-stop-ride/cancel-stop-ride';

export const routes: Routes = [
    {path: 'login', component: Login},
    {path:'forgot-password', component: ForgotPassword},
    { path: 'profile', component: Profile },
    {path: 'register', component: Register},
    {path: 'driver-ride-history', component: DriverRideHistory},
    {path: 'cancel-ride', component:CancelStopRide}
];
