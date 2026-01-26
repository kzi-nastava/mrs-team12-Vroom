export interface RideHistoryResponseDTO {
    rideId: number;
    startAddress: string;
    endAddress: string;
    startTime: Date;
    status: string;
    price: number;
    panicActivated: boolean;
}