import { ChangeDetectionStrategy, ChangeDetectorRef, Component } from '@angular/core';
import { AdminService } from '../../core/services/admin.service'
import { OnInit } from '@angular/core';
import { PricelistDTO } from '../../core/models/admin/pricelist.dto';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-define-pricelist',
  imports: [FormsModule],
  templateUrl: './admin-define-pricelist.html',
  styleUrl: './admin-define-pricelist.css',
})
export class AdminDefinePricelist implements OnInit {
  currentStandard : number = 0.0;
  currentLuxury : number = 0.0;
  currentMinivan : number = 0.0;

  newStandard : number = 0.0;
  newLuxury : number = 0.0;
  newMinivan : number = 0.0;

  constructor(
    private adminService : AdminService,
    private cdr : ChangeDetectorRef
  ){}

  ngOnInit(){
    this.loadPricelist();
  }

  loadPricelist(){
    this.adminService.getActivePricelist().subscribe({
      next: (data : PricelistDTO) => {
        this.currentStandard = data.priceStandard;
        this.currentLuxury = data.priceLuxury;
        this.currentMinivan = data.priceMinivan;

        this.cdr.detectChanges();
      }
    });
  }

  clearFields(){
    this.newStandard = 0.0;
    this.newLuxury = 0.0;
    this.newMinivan = 0.0;
  }

  savePricelist(){
    const dto : PricelistDTO = {
      priceStandard: this.newStandard,
      priceLuxury: this.newLuxury,
      priceMinivan: this.newMinivan
    };

    this.adminService.setPricelist(dto).subscribe({
      next: () => {
        alert('Pricelist updated successfully');
        this.loadPricelist();
        this.clearFields();
        this.cdr.detectChanges();
      }
    })
  }
}
