import { PointResponseDTO } from "./point-response.dto";

export interface LocationUpdate {
  driverId: number;
  point: PointResponseDTO;
  status: string;  
}