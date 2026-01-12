import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
    providedIn: "root"
})
export class GeolocationService{
    private geoUrl = ''

    constructor(private http: HttpClient){}

    getLocation(){
        
    }
}