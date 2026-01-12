import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderARide } from './order-a-ride';

describe('OrderARide', () => {
  let component: OrderARide;
  let fixture: ComponentFixture<OrderARide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderARide]
    }).compileComponents();

    fixture = TestBed.createComponent(OrderARide);
    component = fixture.componentInstance;

    // fake DOM element za mapu
    const mapDiv = document.createElement('div');
    mapDiv.id = 'map';
    mapDiv.style.height = '400px';
    document.body.appendChild(mapDiv);

    fixture.detectChanges();
  });

  afterEach(() => {
    const mapDiv = document.getElementById('map');
    if (mapDiv) {
      document.body.removeChild(mapDiv);
    }
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
