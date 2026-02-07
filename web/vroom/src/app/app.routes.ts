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
import { AdminDriverRequestsComponent } from './features/admin-driver-requests/admin-driver-requests.component';
import {UserActiveRide} from './features/user-active-ride/user-active-ride';
import { AdminUsersComponent } from './features/admin-users/admin-users.component';
import { RideStatisticsComponent } from './features/ride-statistics/ride-statistics.model';
import { RideHistory } from './features/ride-history/ride-history';
import { AdminHomePage } from './features/admin-home-page/admin-home-page';
import { AdminDefinePricelist } from './features/admin-define-pricelist/admin-define-pricelist'
import { AdminActiveRides } from './features/admin-active-rides/admin-active-rides';

export const routes: Routes = [
    {path: 'login', component: Login},
    {path:'forgot-password', component: ForgotPassword},
    { path: 'profile', component: Profile },
    {path: 'register', component: Register},
    {path: 'driver-ride-history', component: DriverRideHistory},
    {path: 'driver-active-ride', component: DriverActiveRide},
    {path: 'order-from-favorites', component: OrderFromFavorites},
    {path: 'register-driver', component: RegisterDriver},
    { path: 'admin-driver-requests', component: AdminDriverRequestsComponent },
    { path: 'admin-users', component: AdminUsersComponent},
    {path: 'active', component: UserActiveRide},
    {path: "ride-statistics", component: RideStatisticsComponent},
    {path: 'admin', component: AdminHomePage},
    {path: 'pricelist', component: AdminDefinePricelist},
    {path: 'active-rides', component: AdminActiveRides},
    {path: '', component: MainView, 
        children: [ 
            {path: 'route-estimation', component: RouteEstimation },
            {path: 'order-a-ride', component: OrderARide}, 
            {path: 'ride-duration', component: RideDuration},
            {path: 'review', component: RideEnd},
            {path: 'panic-feed', component: PanicFeed},
            {path: 'ride-history', component: RideHistory}
        ]
    }
];
