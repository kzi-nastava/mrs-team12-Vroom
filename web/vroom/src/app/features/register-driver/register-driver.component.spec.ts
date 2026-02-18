import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterDriver } from './register-driver';
import { RegisterDriverService } from './register-driver.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { MessageResponseDTO } from '../../core/models/message-response.dto';

describe('RegisterDriver - Registration without vehicle', () => {
  let component: RegisterDriver;
  let fixture: ComponentFixture<RegisterDriver>;
  let service: RegisterDriverService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterDriver, HttpClientTestingModule],
      providers: [RegisterDriverService]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterDriver);
    component = fixture.componentInstance;
    service = TestBed.inject(RegisterDriverService);
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  describe('Form validation - Personal information only', () => {
    
    it('should show error when required personal fields are empty', async () => {
      component.firstName = '';
      component.lastName = '';
      component.email = '';
      
      await component.onSubmit();
      
      expect(component.error).toBe('All personal information fields are required.');
      expect(component.isLoading).toBeFalsy();
    });

    it('should show error when phone number contains non-digits', async () => {
      fillPersonalInfo(component);
      component.phoneNumber = '123-456-7890';
      
      await component.onSubmit();
      
      expect(component.error).toBe('Phone number must contain only digits.');
      expect(component.isLoading).toBeFalsy();
    });

    it('should pass validation with only personal info filled', async () => {
      fillPersonalInfo(component);
      spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Success' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      expect(component.error).toBe('');
    });
  });

  describe('Vehicle data validation logic', () => {
    
    it('should detect when no vehicle data is entered', () => {
      fillPersonalInfo(component);
      
      const hasAny = component['hasAnyVehicleData']();
      const hasAll = component['hasAllVehicleData']();
      
      expect(hasAny).toBeFalsy();
      expect(hasAll).toBeFalsy();
    });

    it('should detect partial vehicle data entry', () => {
      fillPersonalInfo(component);
      component.vehicleBrand = 'Toyota';
      
      const hasAny = component['hasAnyVehicleData']();
      const hasAll = component['hasAllVehicleData']();
      
      expect(hasAny).toBeTruthy();
      expect(hasAll).toBeFalsy();
    });

    it('should show error when vehicle data is partially filled', async () => {
      fillPersonalInfo(component);
      component.vehicleBrand = 'Toyota';
      component.vehicleModel = 'Camry';
      
      await component.onSubmit();
      
      expect(component.error).toBe('If you enter vehicle information, all vehicle fields must be completed.');
    });
  });

  describe('HTTP request - Registration without vehicle', () => {
    
    it('should send correct data structure without vehicle fields', async () => {
      fillPersonalInfo(component);
      
      const createRequestSpy = spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Driver registered successfully' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      expect(createRequestSpy).toHaveBeenCalledTimes(1);
      
      const sentData = createRequestSpy.calls.argsFor(0)[0];
      
      expect(sentData.firstName).toBe('John');
      expect(sentData.lastName).toBe('Doe');
      expect(sentData.email).toBe('john@example.com');
      expect(sentData.phoneNumber).toBe('1234567890');
      expect(sentData.address).toBe('Main St, New York, USA');
      expect(sentData.gender).toBe('MALE');
      
      expect(sentData.brand).toBeUndefined();
      expect(sentData.model).toBeUndefined();
      expect(sentData.type).toBeUndefined();
      expect(sentData.licenceNumber).toBeUndefined();
      expect(sentData.numberOfSeats).toBeUndefined();
      expect(sentData.petsAllowed).toBeUndefined();
      expect(sentData.babiesAllowed).toBeUndefined();
    });

    it('should handle successful registration response', async () => {
      fillPersonalInfo(component);
      
      spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Driver registered! Activation mail sent.' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      expect(component.success).toBe('Driver registered! Activation mail sent.');
      expect(component.error).toBe('');
      expect(component.isLoading).toBeFalsy();
    });

    it('should reset form after successful registration', async () => {
      fillPersonalInfo(component);
      component.profilePic = new File([''], 'test.jpg');
      
      spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Success' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      expect(component.firstName).toBe('');
      expect(component.lastName).toBe('');
      expect(component.email).toBe('');
      expect(component.phoneNumber).toBe('');
      expect(component.profilePic).toBeNull();
    });
  });

  describe('Error handling', () => {
    
  it('should handle 409 Conflict (user already exists)', async () => {
    fillPersonalInfo(component);
    
    const errorResponse = {
      status: 409,
      error: { message: 'Driver with this email already exists' }
    };
    
    spyOn(service, 'createRequest').and.returnValue(
      throwError(() => errorResponse)
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('Driver with this email already exists');
    expect(component.isLoading).toBeFalsy();
  });

  it('should handle 503 Service Unavailable', async () => {
    fillPersonalInfo(component);
    
    const errorResponse = {
      status: 503,
      error: { message: 'Service unavailable' }
    };
    
    spyOn(service, 'createRequest').and.returnValue(
      throwError(() => errorResponse)
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('Service unavailable');
  });

  it('should handle generic server error', async () => {
    fillPersonalInfo(component);
    
    const errorResponse = {
      status: 500,
      error: { message: 'Internal error' }
    };
    
    spyOn(service, 'createRequest').and.returnValue(
      throwError(() => errorResponse)
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('Internal error');
  });

  it('should show fallback message for 409 when no error message provided', async () => {
    fillPersonalInfo(component);
    
    const errorResponse = {
      status: 409,
      error: {}
    };
    
   spyOn(service, 'createRequest').and.returnValue(
      throwError(() => errorResponse)
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('User already exists');
  });

  it('should show fallback message for 503 when no error message provided', async () => {
    fillPersonalInfo(component);
    
    const errorResponse = {
      status: 503,
      error: {}
    };
    
    spyOn(service, 'createRequest').and.returnValue(
      throwError(() => errorResponse)
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('Service temporarily unavailable');
  });

  it('should show fallback message for 500 when no error message provided', async () => {
    fillPersonalInfo(component);
    
    const errorResponse = {
      status: 500,
      error: {}
    };
    
    spyOn(service, 'createRequest').and.returnValue(
      throwError(() => errorResponse)
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('Internal server error');
  });

  it('should handle network error', async () => {
    fillPersonalInfo(component);
    
    spyOn(service, 'createRequest').and.returnValue(
      throwError(() => new Error('Network error'))
    );
    
    await component.onSubmit();
    
    expect(component.error).toBe('Unexpected error occurred.');
  });
});

  describe('Profile picture handling', () => {
    
    it('should convert profile picture to base64', async () => {
      fillPersonalInfo(component);
      
      const mockFile = new File(['fake-image-content'], 'profile.jpg', { type: 'image/jpeg' });
      component.profilePic = mockFile;
      
      const base64Spy = spyOn(service, 'fileToBase64').and.returnValue(Promise.reject(new Error('Conversion failed')));
      
      spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Success' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      expect(base64Spy).toHaveBeenCalledWith(mockFile);
    });

    it('should handle base64 conversion error', async () => {
      fillPersonalInfo(component);
      
      const mockFile = new File([''], 'profile.jpg');
      component.profilePic = mockFile;
      
      spyOn(service, 'fileToBase64').and.returnValue(Promise.reject(new Error('Conversion failed')));
      
      await component.onSubmit();
      
      expect(component.error).toBe('Failed to process profile image.');
      expect(component.isLoading).toBeFalsy();
    });

    it('should send request without profilePhoto if no file selected', async () => {
      fillPersonalInfo(component);
      component.profilePic = null;
      
      const createRequestSpy = spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Success' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      const sentData = createRequestSpy.calls.argsFor(0)[0];
      expect(sentData.profilePhoto).toBeUndefined();
    });
  });

  describe('Loading state management', () => {
    
    it('should set loading to true when submitting', async () => {
      fillPersonalInfo(component);
      
     spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Success' } as MessageResponseDTO)
      );
      
      const submitPromise = component.onSubmit();
      
      expect(component.isLoading).toBeTruthy();
      
      await submitPromise;
    });

    it('should set loading to false after successful submit', async () => {
      fillPersonalInfo(component);
      
      spyOn(service, 'createRequest').and.returnValue(
        of({ message: 'Success' } as MessageResponseDTO)
      );
      
      await component.onSubmit();
      
      expect(component.isLoading).toBeFalsy();
    });

    it('should set loading to false after failed submit', async () => {
      fillPersonalInfo(component);
      
      spyOn(service, 'createRequest').and.returnValue(
        throwError(() => ({ status: 500 }))
      );
      
      await component.onSubmit();
      
      expect(component.isLoading).toBeFalsy();
    });
  });
});

function fillPersonalInfo(component: RegisterDriver): void {
  component.firstName = 'John';
  component.lastName = 'Doe';
  component.country = 'USA';
  component.city = 'New York';
  component.street = 'Main St';
  component.gender = 'MALE';
  component.email = 'john@example.com';
  component.phoneNumber = '1234567890';
}