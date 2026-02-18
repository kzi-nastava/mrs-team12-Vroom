import { Component, OnInit } from '@angular/core'; 
import { CommonModule, } from '@angular/common';
import { Observable, BehaviorSubject } from 'rxjs';
import { FavoriteRoute } from './favorite-route.model';
import { FavoriteRoutesService } from './order-from-favorites.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order-from-favorites',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-from-favorites.html',
  styleUrls: ['./order-from-favorites.css']
})
export class OrderFromFavorites implements OnInit {

  private favoriteRoutesSubject = new BehaviorSubject<FavoriteRoute[]>([]);
favoriteRoutes$ = this.favoriteRoutesSubject.asObservable();


  private errorMessageSubject = new BehaviorSubject<string | null>(null);
  errorMessage$ = this.errorMessageSubject.asObservable(); 


  routeOptions: { [key: number]: { vehicleType: string, babiesAllowed: boolean, petsAllowed: boolean,  scheduledTime: string | null; } } = {};

  constructor(private favoritesService: FavoriteRoutesService) {}

  ngOnInit(): void {
this.favoritesService.getFavorites().subscribe(favs => {
  this.favoriteRoutesSubject.next(favs);

  favs.forEach(fav => {
    this.routeOptions[fav.id] = {
      vehicleType: 'STANDARD',
      babiesAllowed: false,
      petsAllowed: false,
      scheduledTime: null
    };
  });
});
  }

  useRoute(route: FavoriteRoute) {
    this.errorMessageSubject.next(null);

    const options = this.routeOptions[route.id];
    let scheduledTime: string | null = null;

  if (options.scheduledTime) {
    const now = new Date();
    const [hours, minutes] = options.scheduledTime.split(':');

    const scheduled = new Date();
    scheduled.setHours(+hours, +minutes, 0, 0);

    if (scheduled < now) {
      this.errorMessageSubject.next('Scheduled time cannot be in the past');
      return;
    }

    const max = new Date();
    max.setHours(max.getHours() + 5);

    if (scheduled > max) {
      this.errorMessageSubject.next('Scheduled time cannot be more than 5 hours ahead');
      return;
    }

    scheduledTime = scheduled.toISOString();
  }
    const request = {
      favoriteRouteId: route.id,
      vehicleType: options.vehicleType,
      babiesAllowed: options.babiesAllowed,
      petsAllowed: options.petsAllowed,
      scheduledTime: scheduledTime
    };

    this.favoritesService.orderFromFavorite(request)
      .subscribe({
        next: res => {
          alert('Ride successfully ordered!');
        },
        error: err => {
          const message = err.error?.message || err.message || 'Unknown error occurred';
          this.errorMessageSubject.next(message);
          console.error('Ride order failed', err);
        }
      });
  }



  getMinTime(): string {
  const now = new Date();
  return now.toISOString().substring(11, 16); // HH:mm
}

getMaxTime(): string {
  const max = new Date();
  max.setHours(max.getHours() + 5);
  return max.toISOString().substring(11, 16);
}
removeFromFavorites(route: FavoriteRoute) {

  if (!confirm('Are you sure you want to remove this favorite route?')) {
    return;
  }

  this.favoritesService.removeFromFavorites(route.id)
    .subscribe({
      next: () => {
        const current = this.favoriteRoutesSubject.value;
        const updated = current.filter(r => r.id !== route.id);
        this.favoriteRoutesSubject.next(updated);

      },
      error: err => {
        const message = err.error?.message || err.message || 'Delete failed';
        this.errorMessageSubject.next(message);
        console.error('Delete failed', err);
      }
    });
}
  
}
