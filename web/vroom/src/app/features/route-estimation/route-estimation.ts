import { Component, OnInit, ElementRef, HostListener, ViewChild, ChangeDetectorRef, Output, EventEmitter, NgModule, OnDestroy } from '@angular/core';
import * as L from 'leaflet'
import { MapService } from '../../core/services/map.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgModel } from '@angular/forms';
import { AddressSuggestionDTO } from '../../core/models/address/response/address-suggestion-response.dto';
import { HttpClient } from '@angular/common/http';
import { RouteQuoteEstimationDTO } from '../../core/models/address/response/route-quote-estimation.dto';
import { debounceTime, distinctUntilChanged, lastValueFrom, map, Subject, switchMap, takeUntil } from 'rxjs';
import { Stop } from '../../core/models/address/interfaces/stop-point.interface';

@Component({
  selector: 'app-route-estimation',
  standalone: true,  
  imports: [CommonModule, FormsModule],
  templateUrl: './route-estimation.html',
  styleUrl: './route-estimation.css',
})
export class RouteEstimation implements OnInit, OnDestroy{
  private startSearchSubject = new Subject<string>()
  private endSearchSubject = new Subject<string>()
  private stopSearchSubject = new Subject<{index: number, query: string}>()
  private destroy$ = new Subject<void>()

  startLocation: String = ''
  endLocation: String = ''
  stopLocations: string[] = []

  stopIdCounter = 0;
  stops: Stop[] = [];
  stopSuggestions: AddressSuggestionDTO[][] = [];
  showStopSuggestions: boolean[] = [];

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
  @Output() stopsCoordsChange = new EventEmitter<Array<{ lat: number; lng: number }>>();

  constructor(private mapService: MapService,private http: HttpClient, private eRef:ElementRef, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
      this.startSearchSubject.pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => this.mapService.locationSuggestion('Novi Sad, ' + query))
      ).subscribe(data => {
        this.startSuggestions = data;
        this.showStartSuggestions = true;
        this.cdr.detectChanges();
      });

      this.endSearchSubject.pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => this.mapService.locationSuggestion('Novi Sad, ' + query))
      ).subscribe(data => {
        this.endSuggestions = data;
        this.showEndSuggestions = true;
        this.cdr.detectChanges();
      });

      this.stopSearchSubject.pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged((prev, curr) => prev.query === curr.query && prev.index === curr.index),
        switchMap(data => 
          this.mapService.locationSuggestion('Novi Sad, ' + data.query).pipe(
            map(suggestions => ({ suggestions, index: data.index }))
          )
        )
      ).subscribe(result => {
        this.stopSuggestions[result.index] = result.suggestions;
        this.showStopSuggestions[result.index] = true;
        this.cdr.detectChanges();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  @HostListener('document:mousedown', ['$event'])
  clickout(event: MouseEvent) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.showEndSuggestions = false;
      this.showStartSuggestions = false;
      this.showStopSuggestions = this.showStopSuggestions.map(() => false);
    }
  }

  searchStart(): void{
    this.startCoords = undefined;
    const location: string = 'Novi Sad, '+this.startLocation.trim().toString()
    if(this.startLocation.trim().length < 3) return

    this.startSearchSubject.next(location)
  }

  searchEnd(): void{
    this.endCoords = undefined;
    const location: string = 'Novi Sad, '+this.endLocation.trim().toString()
    if(this.endLocation.trim().length < 3) return

    this.endSearchSubject.next(location)
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

  private async tryStopGeocode(): Promise<boolean>{
    for(let i = 0; i < this.stops.length; i++){
      if (this.stops[i].coords) continue;
      const query = `Novi Sad, ${this.stops[i].address}`;
      try {
          const data = await lastValueFrom(this.mapService.geocodeLocation(query));
          if (data) {
              this.stops[i].coords = {lat: data.lat, lng: data.lon}
          }else
            return false
      } catch (error) {
          return false
      }
    }

    return true
  }


  async onSubmit(): Promise<void>{
    this.calculating = true;

    const startValid = this.startCoords || await this.tryGeocode('start');
    const endValid = this.endCoords || await this.tryGeocode('end');

    const stopsValid = await this.tryStopGeocode()

    if (!startValid || !endValid || !stopsValid) {
      this.calculating = false;
      this.error = 'Unable to get a quote for these locations, please try other ones or again later';
      this.cdr.detectChanges();
      return; 
    }

    const start = this.startCoords?.lat+','+this.startCoords?.lng
    const end = this.endCoords?.lat+','+this.endCoords?.lng

    const stops = this.stops.length > 0
        ? this.stops
            .filter(stop => stop.coords)
            .map(stop => `${stop.coords!.lat},${stop.coords!.lng}`)
            .join(';')
      : undefined;


    this.mapService.routeQuote(start, end, stops).subscribe({
      next: (data: RouteQuoteEstimationDTO) => {
        this.price = String(data.price)
        this.time = String(data.time)
        this.calculating = false; 
        this.error = ''
        /*this.startCoordsChange.emit(this.startCoords)
        this.endCoordsChange.emit(this.endCoords)
        this.stopsCoordsChange.emit(
            this.stops
                .filter(stop => stop.coords !== null)
                .map(stop => stop.coords!)
        );*/

        this.mapService.updateRouteOnMap(
          this.startCoords, 
          this.endCoords, 
          this.stops.filter(s => s.coords).map(s => s.coords!)
        )

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

  addStop() {
    this.stops.push({
      id: ++this.stopIdCounter,
      address: '',
      coords: null
    });

    this.stopSuggestions.push([]);
    this.showStopSuggestions.push(false);
  }


  selectStop(i: number, location: AddressSuggestionDTO) {
  this.stops[i].address = location.label;
  this.stops[i].coords = { lat: location.lat, lng: location.lon };
  this.showStopSuggestions[i] = false;
}

  updateStopLocation(index: number, value: string) {
    this.stops[index].address = value;
  }

  searchStop(i: any){
    const query = this.stops[i].address.trim();

    if (!query || query.length < 3) {
      this.stopSuggestions[i] = [];
      this.showStopSuggestions[i] = false;
      this.stops[i].coords = null;
      this.cdr.detectChanges();
      return;
    }

    const location: string = `Novi Sad, ${query}`;

    this.stopSearchSubject.next({ index: i, query: query });
  }

  removeStop(index: number) {
    this.stops.splice(index, 1);
    this.stopSuggestions.splice(index, 1);
    this.showStopSuggestions.splice(index, 1);
  }

  trackByStopId(index: number, stop: Stop): number {
    return stop.id;
  }
}
