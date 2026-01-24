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

  favoriteRoutes$!: Observable<FavoriteRoute[]>;


  private errorMessageSubject = new BehaviorSubject<string | null>(null);
  errorMessage$ = this.errorMessageSubject.asObservable(); 


  routeOptions: { [key: number]: { vehicleType: string, babiesAllowed: boolean, petsAllowed: boolean } } = {};

  constructor(private favoritesService: FavoriteRoutesService) {}

  ngOnInit(): void {
    this.favoriteRoutes$ = this.favoritesService.getFavorites();
    this.favoriteRoutes$.subscribe(favs => {
      favs.forEach(fav => {
        this.routeOptions[fav.id] = {
          vehicleType: 'STANDARD',
          babiesAllowed: false,
          petsAllowed: false
        };
      });
    });
  }

  useRoute(route: FavoriteRoute) {
    this.errorMessageSubject.next(null);

    const options = this.routeOptions[route.id];

    const request = {
      favoriteRouteId: route.id,
      vehicleType: options.vehicleType,
      babiesAllowed: options.babiesAllowed,
      petsAllowed: options.petsAllowed,
      scheduledTime: null
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

  removeFromFavorites(route: FavoriteRoute) {
    console.log('TODO remove', route.id);
  }

  
}
