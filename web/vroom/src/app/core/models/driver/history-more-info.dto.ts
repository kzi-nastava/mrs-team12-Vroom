

export interface HistoryMoreInfoDTO{
    rideID : number,
    passengers : string[],
    status : string,
    cancelReason : string,
    complaints : string[],
    driverRating : number,
    vehicleRating : number,
    comment : string
}