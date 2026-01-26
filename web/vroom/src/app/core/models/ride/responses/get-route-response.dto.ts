import { PointResponseDTO } from "../../driver/point-response.dto";

export interface GetRouteResponseDTO {
  startLocationLat: number;
  startLocationLng: number;
  endLocationLat: number;
  endLocationLng: number;
  stops: PointResponseDTO[];
}