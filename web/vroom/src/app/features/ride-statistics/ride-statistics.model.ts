import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RideStatisticsService } from './ride-statistics.service';
import { RideReportDTO, DailyRideReportDTO } from './ride-report.model';
import { CommonModule } from '@angular/common';
import { ChartData, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';


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
 @ViewChild('ridesChart') ridesChart?: BaseChartDirective;
  @ViewChild('moneyChart') moneyChart?: BaseChartDirective;
  @ViewChild('kmChart') kmChart?: BaseChartDirective;

 

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

    const formatDate = (date: string | null) => {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
    };

    const fromStr = formatDate(from);
    const toStr = formatDate(to);

    if (this.role === 'REGISTERED_USER') {
      this.rideStatsService.getPassengerReport(userId, fromStr, toStr).subscribe({
        next: res => { this.report = res; this.updateCharts(); },
        error: err => console.error(err)
      });
    } else if (this.role === 'DRIVER') {
      this.rideStatsService.getDriverReport(driverId, fromStr, toStr).subscribe({
        next: res => { this.report = res; this.updateCharts(); },
        error: err => console.error(err)
      });
    } else if (this.role === 'ADMIN') {
      if (userId) {
        this.rideStatsService.getAdminUserReport(userId, fromStr, toStr).subscribe({
          next: res => { this.report = JSON.parse(JSON.stringify(res)); this.updateCharts(); },
          error: err => console.error(err)
        });
      } else if (driverId) {
        this.rideStatsService.getAdminDriverReport(driverId, fromStr, toStr).subscribe({
          next: res => { this.report = JSON.parse(JSON.stringify(res)); this.updateCharts(); },
          error: err => console.error(err)
        });
      }
    }
  }


  fetchAllUsersReport() {
    const { from, to } = this.form.value;
    const formatDate = (date: string | null) => {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
    };
    const fromStr = formatDate(from);
    const toStr = formatDate(to);

    this.rideStatsService.getAdminAllUsersReport(fromStr, toStr).subscribe({
      next: res => { this.report = JSON.parse(JSON.stringify(res)); this.updateCharts(); },
      error: err => console.error(err)
    });
  }


  fetchAllDriversReport() {
    const { from, to } = this.form.value;
    const formatDate = (date: string | null) => {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
    };
    const fromStr = formatDate(from);
    const toStr = formatDate(to);

    this.rideStatsService.getAdminAllDriversReport(fromStr, toStr).subscribe({
      next: res => { this.report = JSON.parse(JSON.stringify(res)); this.updateCharts(); },
      error: err => console.error(err)
    });
  }
updateCharts() {
  if (!this.report) return;

  const labels = this.report.daily.map(d => d.date);
  const ridesData = this.report.daily.map(d => d.rideCount);
  const moneyData = this.report.daily.map(d => d.money);
  const kmData = this.report.daily.map(d => d.km);


  this.ridesChartData = {
    labels,
    datasets: [
      { label: 'Rides per Day', data: ridesData, backgroundColor: '#42A5F5' }
    ]
  };

  this.moneyChartData = {
    labels,
    datasets: [
      { label: 'Money per Day', data: moneyData, backgroundColor: '#66BB6A' }
    ]
  };

  this.kmChartData = {
    labels,
    datasets: [
      { label: 'Distance (km) per Day', data: kmData }
    ]
  };


  setTimeout(() => {
    this.ridesChart?.chart?.update();
    this.moneyChart?.chart?.update();
    this.kmChart?.chart?.update();
  }, 0);
}
totalMoney(): string {
  return this.report ? this.report.totalMoney.toFixed(2) : '0.00';
}

totalRides(): number {
  return this.report ? this.report.totalRides : 0;
}

totalKm(): string {
  if (!this.report) return '0.00';
  const sum = this.report.daily.reduce((acc, d) => acc + (d.km ?? 0), 0);
  return sum.toFixed(2);
}

averageRidesPerDay(): string {
  if (!this.report || this.report.daily.length === 0) return '0';
  const avg = this.report.totalRides / this.report.daily.length;
  return avg.toFixed(2);
}

averageMoneyPerDay(): string {
  if (!this.report || this.report.daily.length === 0) return '0.00';
  const avg = this.report.totalMoney / this.report.daily.length;
  return avg.toFixed(2);
}

averageKmPerDay(): string {
  if (!this.report || this.report.daily.length === 0) return '0.00';
  const totalKm = this.report.daily.reduce((acc, d) => acc + (d.km ?? 0), 0);
  const avg = totalKm / this.report.daily.length;
  return avg.toFixed(2);
}


}