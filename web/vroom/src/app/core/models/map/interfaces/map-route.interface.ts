import { PointResponseDTO } from "../../driver/point-response.dto";

export interface MapRouteDTO{
    start: PointResponseDTO | undefined;
    end: PointResponseDTO | undefined;
    stops: PointResponseDTO[]
}