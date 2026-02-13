export interface Ride {
  rideID: number;
  driverId: number;        
  passengerId: number;     
  route: {
    id: number;
    startAddress: string;
    endAddress: string;
  };
  startTime: string | null;  
  endTime: string | null;
  scheduledTime: string | null;  
  passengers: string[] | null;   
  price: number | null;
  status: RideStatus;
  cancelReason: string | null;
  isScheduled: boolean;  
  complaints: string[] | null;
  panicActivated: boolean;
  panicNotificationId?: number | null;
  driverRating: number | null;
  vehicleRating: number | null;
  comment: string | null;
  vehicle?: Vehicle;
}

export interface Vehicle {
  id: number;
  brand: string;
  model: string;
  type: string;       
  licenceNumber: string;
  numberOfSeats: number | null;
  babiesAllowed: boolean | null;
  petsAllowed: boolean | null;
  ratingCount: number;
  ratingSum: number;
}

export enum RideStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  DENIED = 'DENIED',
  ONGOING = 'ONGOING',
  CANCELLED_BY_USER = 'CANCELLED_BY_USER',
  CANCELLED_BY_DRIVER = 'CANCELLED_BY_DRIVER',
  FINISHED = 'FINISHED'
}
