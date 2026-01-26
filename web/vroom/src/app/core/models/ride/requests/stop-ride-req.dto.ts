export class StopRideRequestDTO{
    endTime!: string | Date;
    stopLat!: number;
    stopLng!: number;

    getLocation(successCallback: () => void){
        if(!navigator.geolocation) {
            alert('Geolocation is not supported by your browser')
            return
        }

        navigator.geolocation.getCurrentPosition(
        (position) => {
            this.stopLat = position.coords.latitude
            this.stopLng = position.coords.longitude
            successCallback()
        },
        (error) => {
            alert('Failed to get location')
        },
        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
        )
    }
}