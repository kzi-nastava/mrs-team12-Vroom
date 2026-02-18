export interface DailyRideReportDTO {
  date: string; 
  rideCount: number;
  money: number;
  km: number;
}

export interface RideReportDTO {
  daily: DailyRideReportDTO[];
  totalRides: number;
  totalMoney: number;
}