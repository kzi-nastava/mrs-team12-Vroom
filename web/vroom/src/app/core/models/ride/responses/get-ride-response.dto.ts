

export interface GetRideResponseDTO{
    rideID: number,
    driver: {
        firstName : string,
        lastName : string,
        email : string,
        gender : string,
        vehicle :{
            brand: string,
            model: string
        }
    },
    route: {
        startAddress : string,
        endAddress : string
    },
    price: number
    scheduledTime: Date
    status:string

}
