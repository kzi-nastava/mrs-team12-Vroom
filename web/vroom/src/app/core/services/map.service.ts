import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as L from 'leaflet'
import { HttpParams } from '@angular/common/http';
import { AddressSuggestionDTO } from '../models/address/response/address-suggestion-response.dto';
import { RouteQuoteEstimationDTO } from '../models/address/response/route-quote-estimation.dto';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'  
})
export class MapService{
    private geoUrl = 'http://localhost:8080/api/geo'
    private routeUrl = 'http://localhost:8080/api/routes'

    private routePointsSource = new Subject<{
        start: {lat: number, lng: number},
        end: {lat: number, lng: number},
        stops: Array<{lat: number, lng: number}>
    }>();

    routePoints$ = this.routePointsSource.asObservable();

    constructor(private http: HttpClient) {}

    configMap(map: any){
        map = L.map('map',{
          center: [45.2396, 19.8227],
          zoom: 6
        })
    
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(map);
    }
    
    updateRouteOnMap(start: any, end: any, stops: any[]) {
        this.routePointsSource.next({ start, end, stops });
    }

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

    geocodeLocation(location: string){
        const params = new HttpParams().set('location', location);
        return this.http.get<AddressSuggestionDTO>(this.geoUrl+`/geocode-address`, {params})
    }

}