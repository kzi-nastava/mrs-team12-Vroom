import { Routes } from '@angular/router';
import {Register} from './features/register/register'
import {Login} from './features/login/login'
import { ForgotPassword } from './features/forgot-password/forgot-password';
import { Profile } from './features/profile/profile';
import { DriverRideHistory } from './features/driver-ride-history/driver-ride-history';
import { DriverActiveRide} from './features/driver-active-ride/driver-active-ride';   
import { OrderFromFavorites } from './features/order-from-favorites/order-from-favorites';
import { OrderARide } from './features/order-a-ride/order-a-ride';
import { CancelRide } from './features/cancel-ride/cancel-ride';
import { RideDuration } from './features/ride-duration/ride-duration';
import { MainView } from './features/main-view/main-view';
import { RideEnd } from './features/ride-end/ride-end';
import {RegisterDriver} from './features/register-driver/register-driver'
import { RouteEstimation } from './features/route-estimation/route-estimation';
import { authGuard } from './core/utils/auth.guard';
import { ChangeDriverStatus } from './features/change-driver-status/change-driver-status';
import { PanicButton } from './features/panic-btn/panic-button';
import { PanicFeed } from './features/panic-feed/panic-feed';
import { StopRide } from './features/stop-ride/stop-ride';
import {UserActiveRide} from './features/user-active-ride/user-active-ride'

export const routes: Routes = [
    {path: 'login', component: Login},
    {path:'forgot-password', component: ForgotPassword},
    { path: 'profile', component: Profile },
    {path: 'register', component: Register},
    {path: 'driver-ride-history', component: DriverRideHistory},
    {path: 'driver-active-ride', component: DriverActiveRide},
    {path: 'order-from-favorites', component: OrderFromFavorites},
    {path: 'register-driver', component: RegisterDriver},
    {path: 'panic', component: PanicButton},
    {path: 'active', component: UserActiveRide},
    {path: '', component: MainView, 
        children: [ 
            {path: 'route-estimation', component: RouteEstimation },
            {path: 'order-a-ride', component: OrderARide}, 
            {path: 'ride-duration', component: RideDuration},
            {path: 'review', component: RideEnd},
            {path: 'panic-feed', component: PanicFeed}
        ]
    },
    {path: 'cancel-ride', component:CancelRide},
    {path: 'stop', component:StopRide}
];
