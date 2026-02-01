export interface StoppedRideResponseDTO{
    driverID: number;
    startTime: string | Date;
    endTime: string | Date;
    status: String;
    price: number;
}