import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterDriver } from './register-driver';

describe('RegisterDriver', () => {
  let component: RegisterDriver;
  let fixture: ComponentFixture<RegisterDriver>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterDriver]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterDriver);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
