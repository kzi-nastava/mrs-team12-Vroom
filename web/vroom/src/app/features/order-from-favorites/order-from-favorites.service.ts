import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { FavoriteRoute, OrderFromFavoriteRequest } from "./favorite-route.model";

@Injectable({ providedIn: 'root' })
export class FavoriteRoutesService {

  private baseUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

getFavorites(): Observable<FavoriteRoute[]> {
  const token = localStorage.getItem('jwt');

  return this.http.get<FavoriteRoute[]>(
    `${this.baseUrl}/favorites`,
    { headers: { Authorization: `Bearer ${token}` } }
  ).pipe(
    map(routes => 
      routes.map(r => ({
        id: r.id,
        name: r.name,
        route: {
          startLocationLat: r.route.startLocationLat,
          startLocationLng: r.route.startLocationLng,
          endLocationLat: r.route.endLocationLat,
          endLocationLng: r.route.endLocationLng
        }
      }))
    )
  );
}


  orderFromFavorite(request: OrderFromFavoriteRequest) {
    const token = localStorage.getItem('jwt');

    return this.http.post(
      `${this.baseUrl}/order/favorite`,
      request,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );
  }
}
