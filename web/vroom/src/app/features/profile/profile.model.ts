export interface Profile {
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  email: string;

  vehicle?: any;
  activeHoursLast24h?: number;
}
