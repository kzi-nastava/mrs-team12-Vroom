import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderFromFavorites } from './order-from-favorites';

describe('OrderFromFavorites', () => {
  let component: OrderFromFavorites;
  let fixture: ComponentFixture<OrderFromFavorites>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderFromFavorites]
    }).compileComponents();

    fixture = TestBed.createComponent(OrderFromFavorites);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});