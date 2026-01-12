export interface Stop {
  id: number;
  address: string;
  coords: { lat: number; lng: number } | null;
}