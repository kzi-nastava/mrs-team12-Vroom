export interface PanicNotificationDTO{
    id: number;
    rideID: number;
    activatedBy: string;
    activatedAt: Date | string;
}