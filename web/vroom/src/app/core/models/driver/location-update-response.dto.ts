import { PointResponse } from "./point-response.dto";

export interface LocationUpdate {
  driverId: number;
  point: PointResponse;
  status: String;  
}