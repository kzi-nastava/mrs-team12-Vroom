import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RideStatisticsService } from './ride-statistics.service';
import { RideReportDTO, DailyRideReportDTO } from './ride-report.model';
import { CommonModule } from '@angular/common';
import { ChartData, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';


@Component({
  selector: 'app-ride-statistics',
  templateUrl: './ride-statistics.component.html',
  styleUrls: ['./ride-statistics.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgChartsModule]   
})
export class RideStatisticsComponent implements OnInit {

  form: FormGroup;
  report: RideReportDTO | null = null;
  role: 'REGISTERED_USER' | 'DRIVER' | 'ADMIN' = 'REGISTERED_USER'; 


 

 kmChartData: ChartData<'bar'> = {
  labels: [],
  datasets: [
    { label: 'Distance (km) per Day', data: [] }
  ]
};

kmChartOptions: ChartOptions<'bar'> = {
  responsive: true
};
  ridesChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      { label: 'Rides per Day', data: [], backgroundColor: '#42A5F5' }
    ]
  };

  moneyChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      { label: 'Money per Day', data: [], backgroundColor: '#66BB6A' }
    ]
  };

  ridesChartOptions: ChartOptions<'bar'> = { responsive: true };
  moneyChartOptions: ChartOptions<'bar'> = { responsive: true };

  constructor(
    private fb: FormBuilder,
    private rideStatsService: RideStatisticsService
  ) {
    this.form = this.fb.group({
      from: [''],
      to: [''],
      userId: [''],     
      driverId: ['']    
    });
  }

ngOnInit(): void {
  const userType = localStorage.getItem('user_type');

  switch (userType) {
    case 'DRIVER':
      this.role = 'DRIVER';
      break;
    case 'ADMIN':
      this.role = 'ADMIN';
      break;
    default:
      this.role = 'REGISTERED_USER';
  }
}

fetchReport() {
  const { from, to, userId, driverId } = this.form.value;

  const fromDate = from ? new Date(from) : null;
  const toDate   = to ? new Date(to) : null;

  const formatDate = (date: Date | null): string => {
    if (!date) return '';
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0'); 
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  };

  const fromStr = formatDate(fromDate);
  const toStr = formatDate(toDate);

  if (this.role === 'REGISTERED_USER') {
    this.rideStatsService.getPassengerReport(userId, fromStr, toStr).subscribe({
      next: res => {
        this.report = res;
        this.updateCharts();
      },
      error: err => console.error(err)
    });
  

  } else if (this.role === 'DRIVER') {
    this.rideStatsService.getDriverReport(driverId, fromStr, toStr).subscribe({
       next: res => {
        this.report = res;
        this.updateCharts();
      },
      error: err => console.error(err)
    });

  } else if (this.role === 'ADMIN') {
    this.rideStatsService.getAdminReport(fromStr, toStr).subscribe({
      next: res => this.report = res,
      error: err => console.error(err)
    });
  }
}
updateCharts() {
  if (!this.report) return;

  const labels = this.report.daily.map(d => d.date);

  this.ridesChartData.labels = labels;
  this.ridesChartData.datasets[0].data =
    this.report.daily.map(d => d.rideCount);

  this.moneyChartData.labels = labels;
  this.moneyChartData.datasets[0].data =
    this.report.daily.map(d => d.money);

  this.kmChartData.labels = labels;
  this.kmChartData.datasets[0].data =
    this.report.daily.map(d => d.km);
}

  totalMoney(): string {
    return this.report ? this.report.totalMoney.toFixed(2) : '0.00';
  }

  totalRides(): number {
    return this.report ? this.report.totalRides : 0;
  }

totalKm(): string {
  if (!this.report) return '0.00';

  const sum = this.report.daily
    .reduce((acc, d) => acc + (d.km ?? 0), 0);

  return sum.toFixed(2);
}

averageRidesPerDay(): string {
  if (!this.report || this.report.daily.length === 0) return '0';

  const days = this.report.daily.length;
  const avg = this.report.totalRides / days;

  return avg.toFixed(2);
}

averageMoneyPerDay(): string {
  if (!this.report || this.report.daily.length === 0) return '0.00';

  const days = this.report.daily.length;
  const avg = this.report.totalMoney / days;

  return avg.toFixed(2);
}

averageKmPerDay(): string {
  if (!this.report || this.report.daily.length === 0) return '0.00';

  const totalKm = this.report.daily
    .reduce((acc, d) => acc + (d.km ?? 0), 0);

  const avg = totalKm / this.report.daily.length;
  return avg.toFixed(2);
}

}