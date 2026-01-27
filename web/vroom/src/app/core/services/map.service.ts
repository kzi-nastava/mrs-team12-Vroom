import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as L from 'leaflet'
import { HttpParams } from '@angular/common/http';
import { AddressSuggestionDTO } from '../models/address/response/address-suggestion-response.dto';
import { RouteQuoteEstimationDTO } from '../models/address/response/route-quote-estimation.dto';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { MapActionType } from '../models/map/enums/map-action-type.enum';
import { MapAction } from '../models/map/interfaces/map-action.interface';

@Injectable({
  providedIn: 'root'  
})
export class MapService{
    private geoUrl = 'http://localhost:8080/api/geo'
    private routeUrl = 'http://localhost:8080/api/routes'
    private mapActionSource = new BehaviorSubject<MapAction | null>(null);
    mapAction$ = this.mapActionSource.asObservable();

    constructor(private http: HttpClient) {}

    routeQuote(startLocation: string, endLocation: string, stops: string | undefined){
        let params
        if(stops !== undefined){
            params = new HttpParams().set('startLocation', startLocation).set('endLocation', endLocation).set('stops', stops);
        }
            
        else 
            params = new HttpParams().set('startLocation', startLocation).set('endLocation', endLocation)

        return this.http.get<RouteQuoteEstimationDTO>(this.routeUrl+`/quote`, {params})
    }

    locationSuggestion(location: string){
        const params = new HttpParams().set('location', location);
        return this.http.get<AddressSuggestionDTO[]>(this.geoUrl+`/autocomplete-address`, {params})
    }

    geocodeLocation(location: string): Observable<AddressSuggestionDTO>{
        const params = new HttpParams().set('location', location);
        return this.http.get<AddressSuggestionDTO>(this.geoUrl+`/geocode-address`, {params})
    }


    drawRoute(start: any, end: any, stops: any[]) {
        this.mapActionSource.next({
            type: MapActionType.DRAW_ROUTE,
            payload: { start, end, stops }
        });
    }

    clearMap() {
        this.mapActionSource.next({ type: MapActionType.CLEAR_MAP });
    }

    rideDurationInit(rideID: string) {
        this.mapActionSource.next({ 
          type: MapActionType.RIDE_DURATION,
          payload: { rideID }
        });
    }

    panicRideInit(rideID: string) {
      this.mapActionSource.next({
        type: MapActionType.PANIC_RIDE,
        payload: { rideID }
      })
    }


async getRouteCoordinates(payload: any): Promise<RouteResponse | null> {
  const coordinates: [number, number][] = [];

  if (payload.start) coordinates.push([payload.start.lng, payload.start.lat]);
  payload.stops?.forEach((stop: any) => coordinates.push([stop.lng, stop.lat]));
  if (payload.end) coordinates.push([payload.end.lng, payload.end.lat]);

  if (coordinates.length < 2) {
    console.error('You need at least two coordinates');
    return null;
  }

  try {
    const coordString = coordinates.map(c => c.join(',')).join(';');


    const response: any = await this.http
  .get(`http://localhost:8080/api/routes/osrm-route`, { params: { coords: coordString } })
  .toPromise();

    if (response?.routes?.length > 0) {
      return response.routes[0];
    } else {
      return null;
    }

  } catch (e) {
    console.error('Error fetching route', e);
    return null;
  }
}



showVehicles() {
  this.mapActionSource.next({ 
    type: MapActionType.SHOW_VEHICLES 
  });
}

}