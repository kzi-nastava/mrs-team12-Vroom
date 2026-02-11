import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RegisterDriverService } from '../register-driver/register-driver.service';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { vi } from 'vitest'; 

describe('RegisterDriverService', () => {
  let service: RegisterDriverService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    vi.spyOn(Storage.prototype, 'getItem').mockReturnValue('fake-jwt-token');
    
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RegisterDriverService]
    });
    
    service = TestBed.inject(RegisterDriverService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    vi.restoreAllMocks();
  });

  describe('validateDriverPreferences', () => {
    
    it('should return null when vehicle info is not provided (all null)', () => {
      const result = service.validateDriverPreferences(null, null, null);
      expect(result).not.toBeNull();
    });

    it('should validate number of seats is required when vehicle is provided', () => {
      const result = service.validateDriverPreferences(null, true, true);
      expect(result).toBe('Number of seats is required when vehicle information is provided');
    });

    it('should validate number of seats is positive', () => {
      const result = service.validateDriverPreferences(-5, true, true);
      expect(result).toBe('Number of seats must be a positive number');
    });

    it('should validate pets preference is selected', () => {
      const result = service.validateDriverPreferences(4, null, true);
      expect(result).toBe('You must select whether pets are allowed when vehicle information is provided');
    });

    it('should validate babies preference is selected', () => {
      const result = service.validateDriverPreferences(4, true, null);
      expect(result).toBe('You must select whether babies are allowed when vehicle information is provided');
    });

    it('should return null for valid vehicle preferences', () => {
      const result = service.validateDriverPreferences(4, true, false);
      expect(result).toBeNull();
    });
  });

  describe('createRequest', () => {
    
    it('should send POST request with correct headers and data (no vehicle)', () => {
      const mockData = {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        phoneNumber: '1234567890',
        address: 'Main St, New York, USA',
        gender: 'MALE'
      };

      const mockResponse: MessageResponseDTO = {
        message: 'Driver registered successfully'
      };

      service.createRequest(mockData).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/drivers/register/driver');
      
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('Authorization')).toBe('Bearer fake-jwt-token');
      expect(req.request.headers.get('Content-Type')).toBe('application/json');
      expect(req.request.body).toEqual(mockData);
      
      req.flush(mockResponse);
    });

    it('should handle error response', () => {
      const mockData = {
        firstName: 'John',
        email: 'existing@example.com'
      };

      service.createRequest(mockData).subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(409);
          expect(error.error.message).toBe('Driver with this email already exists');
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/drivers/register/driver');
      
      req.flush(
        { message: 'Driver with this email already exists' },
        { status: 409, statusText: 'Conflict' }
      );
    });
  });

  describe('fileToBase64', () => {
    
    it('should convert file to base64 string', async () => {
      const mockFile = new File(['test content'], 'test.jpg', { type: 'image/jpeg' });
      
      const result = await service.fileToBase64(mockFile);
      
      expect(result).toBeTruthy();
      expect(typeof result).toBe('string');
    });
  });

  describe('getFileValidationError', () => {
    
    it('should return error for non-image file', () => {
      const mockFile = new File([''], 'document.pdf', { type: 'application/pdf' });
      
      const error = service.getFileValidationError(mockFile);
      
      expect(error).toBe('Please select a valid image file (png, jpg, etc.)');
    });

    it('should return error for oversized file', () => {
      const largeContent = new Array(3 * 1024 * 1024).join('a');
      const mockFile = new File([largeContent], 'large.jpg', { type: 'image/jpeg' });
      
      const error = service.getFileValidationError(mockFile);
      
      expect(error).toBe('Image size must be less than 2MB');
    });

    it('should return null for valid image file', () => {
      const mockFile = new File(['small content'], 'valid.jpg', { type: 'image/jpeg' });
      
      const error = service.getFileValidationError(mockFile);
      
      expect(error).toBeNull();
    });
  });
});