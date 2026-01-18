export interface Vehicle {
  brand: string;
  model: string;
  numberOfSeats: number;
  licensePlate: string;
  babiesAllowed: boolean;
  petsAllowed: boolean;
}

export interface Profile {
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  email: string;
  vehicle?: Vehicle;
  activeHoursLast24h?: number;
}