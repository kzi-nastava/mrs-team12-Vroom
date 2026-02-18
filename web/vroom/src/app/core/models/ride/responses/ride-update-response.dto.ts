import { PointResponseDTO } from '../../driver/point-response.dto';

export interface RideUpdateResponseDTO {
    driverID: number,
    currentLocation: PointResponseDTO;
    timeLeft: number; 
    status: String;
}