import { Component, OnInit, ElementRef, HostListener, ViewChild, ChangeDetectorRef, Output, EventEmitter } from '@angular/core';
import * as L from 'leaflet'
import { MapService } from '../../core/services/map.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AddressSuggestionDTO } from '../../core/models/address/address-suggestion-response.dto';
import { HttpClient } from '@angular/common/http';
import { RouteQuoteEstimationDTO } from '../../core/models/address/route-quote-estimation.dto';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-route-estimation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './route-estimation.html',
  styleUrl: './route-estimation.css',
})
export class RouteEstimation{
  startLocation: String = ''
  endLocation: String = ''

  price: String = ''
  time: String = ''

  startSuggestions: AddressSuggestionDTO[] = [];
  endSuggestions: AddressSuggestionDTO[] = [];

  showStartSuggestions: boolean = false;
  showEndSuggestions: boolean =  false;

  startCoords?: { lat: number; lng: number };
  endCoords?: { lat: number; lng: number };

  calculating: boolean = false;
  error: String = ''


  @ViewChild('startInput', { static: false }) startInput?: ElementRef;
  @ViewChild('startDropdown', { static: false }) startDropdown?: ElementRef;

  @ViewChild('endInput', { static: false }) endInput?: ElementRef;
  @ViewChild('endDropdown', { static: false }) endDropdown?: ElementRef;

  @Output() startCoordsChange = new EventEmitter<{lat:number,lng:number}>();
  @Output() endCoordsChange = new EventEmitter<{lat:number,lng:number}>();

  constructor(private mapService: MapService,private http: HttpClient, private eRef:ElementRef, private cdr: ChangeDetectorRef) {}

  @HostListener('document:click', ['$event'])
  clickout(event: MouseEvent) {
    this.showEndSuggestions = false
    this.showStartSuggestions = false
  }

  async searchStart(): Promise<void>{
    this.startCoords = undefined;
    const location: string = 'Novi Sad, '+this.startLocation.trim().toString()

    this.mapService.locationSuggestion(location).subscribe({
      next: (data: AddressSuggestionDTO[]) => {
        this.startSuggestions = data;
        this.showStartSuggestions = true;
      },
      error: (err) => {
        this.startSuggestions = [];
      }
    })
  }

  async searchEnd(): Promise<void>{
    this.endCoords = undefined;
    const location: string = 'Novi Sad, '+this.endLocation.trim().toString()

    this.mapService.locationSuggestion(location).subscribe({
      next: (data: AddressSuggestionDTO[]) => {
        this.endSuggestions = data;
        this.showEndSuggestions = true;
      },
      error: (err) => {
        this.endSuggestions = [];
      }
    })
  }

  selectStart(location: AddressSuggestionDTO): void{
    this.startLocation = String(location.label)
    this.startCoords = {lat: location.lat, lng: location.lon}
    this.startSuggestions = []
    this.showStartSuggestions = false
  }

  selectEnd(location: any): void{
    this.endLocation = String(location.label)
    this.endCoords = {lat: location.lat, lng: location.lon}
    this.endSuggestions = []
    this.showEndSuggestions = false
  }

  private async tryGeocode(type: 'start' | 'end'): Promise<boolean>{
    const address = type === 'start' ? this.startLocation.trim().toString() : this.endLocation.trim().toString();
    const query = `Novi Sad, ${address}`;

    try {
        const data = await lastValueFrom(this.mapService.geocodeLocation(query));
        
        if (data) {
            if (type === 'start') {
                this.startCoords = { lat: data.lat, lng: data.lon };
            } else {
                this.endCoords = { lat: data.lat, lng: data.lon };
            }
        }else
          return false
        return true
    } catch (error) {
        console.error(`Geocoding failed for ${type}:`, error);
        return false
    }
  }


  async onSubmit(): Promise<void>{
    console.log(this.startCoords)
    console.log(this.endCoords)
    this.calculating = true;

    const startValid = this.startCoords || await this.tryGeocode('start');
    const endValid = this.endCoords || await this.tryGeocode('end');

    if (!startValid || !endValid) {
      this.calculating = false;
      this.error = 'Unable to get a quote for these locations, please try other ones or again later';
      this.cdr.detectChanges();
      return; 
    }

    console.log(this.startCoords)
    console.log(this.endCoords)

    const start = this.startCoords?.lat+','+this.startCoords?.lng
    const end = this.endCoords?.lat+','+this.endCoords?.lng

    this.mapService.routeQuote(start, end).subscribe({
      next: (data: RouteQuoteEstimationDTO) => {
        this.price = String(data.price)
        this.time = String(data.time)
        this.calculating = false; 
        this.error = ''
        this.startCoordsChange.emit(this.startCoords)
        this.endCoordsChange.emit(this.endCoords)
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.calculating = false; 
        this.startCoords = undefined
        this.endCoords = undefined
        this.price = ''
        this.time = ''
        this.error = 'An error occurred'
        this.cdr.detectChanges();
      }
    })
  }

}
