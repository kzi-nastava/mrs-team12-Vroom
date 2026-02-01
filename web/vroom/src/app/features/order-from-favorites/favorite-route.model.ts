export interface RouteDTO {
  startLocationLat: number;
  startLocationLng: number;
  endLocationLat: number;
  endLocationLng: number;
}

export interface FavoriteRoute {
  id: number;
  name: string;
  route: RouteDTO;
  startAddress: string;
  endAddress: string;
}

export interface OrderFromFavoriteRequest {
  favoriteRouteId: number;
  vehicleType: string;
  babiesAllowed: boolean;
  petsAllowed: boolean;
  scheduledTime: string | null;
}

export interface OrderFromFavoriteRequest {
  favoriteRouteId: number;
  vehicleType: string;
  babiesAllowed: boolean;
  petsAllowed: boolean;
  scheduledTime: string | null;
}

