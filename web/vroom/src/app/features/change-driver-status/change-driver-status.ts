import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { DriverService } from '../../core/services/driver.service';

@Component({
  selector: 'app-change-driver-status',
  imports: [CommonModule],
  templateUrl: './change-driver-status.html',
  styleUrl: './change-driver-status.css',
})
export class ChangeDriverStatus implements OnInit{
  status: 'AVAILABLE' | 'UNAVAILABLE' = 'AVAILABLE'

  constructor(private driverService: DriverService) {}

  ngOnInit(): void {
    this.status =  'AVAILABLE'
  }

  onChangeStatus(){
    if(this.status === 'AVAILABLE')
      this.status = 'UNAVAILABLE'
    else if(this.status === 'UNAVAILABLE')
      this.status = 'AVAILABLE'

    this.driverService
      .createChangeStatusRequest(this.status)
      .subscribe()
      
  }
}
