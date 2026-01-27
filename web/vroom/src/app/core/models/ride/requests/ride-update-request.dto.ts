import { PointResponseDTO } from '../../driver/point-response.dto'

export interface RideUpdateRequestDTO{
    location: PointResponseDTO,
    status: String
}