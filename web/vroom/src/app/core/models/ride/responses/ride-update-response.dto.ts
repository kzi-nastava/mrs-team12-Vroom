import { PointResponseDTO } from '../../driver/point-response.dto';

export interface RideUpdateResponseDTO {
    currentLocation: PointResponseDTO;
    timeLeft: number; 
}