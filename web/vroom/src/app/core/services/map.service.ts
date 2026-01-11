import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as L from 'leaflet'
import { HttpParams } from '@angular/common/http';
import { AddressSuggestionDTO } from '../models/address/address-suggestion-response.dto';
import { RouteQuoteEstimationDTO } from '../models/address/route-quote-estimation.dto';

@Injectable({
  providedIn: 'root'  
})
export class MapService{
    private geoUrl = 'http://localhost:8080/api/geo'
    private routeUrl = 'http://localhost:8080/api/routes'

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

    routeQuote(startLocation: string, endLocation: string){
        const params = new HttpParams().set('startLocation', startLocation).set('endLocation', endLocation);
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