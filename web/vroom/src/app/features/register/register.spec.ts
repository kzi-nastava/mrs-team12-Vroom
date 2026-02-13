import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Register } from './register';
import { AuthService } from '../../core/services/auth.service';
import { Observable, of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

describe('Register Component', () => {
  let component: Register
  let fixture: ComponentFixture<Register>
  let authServiceMock: any

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['isPasswordValid', 'createRegisterRequest'])

    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents()

    fixture = TestBed.createComponent(Register)
    component = fixture.componentInstance
    
    fixture.detectChanges()
  })

  describe('Component creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy()
    })
  })

  describe('Password validation', () => {
    it('should show error if passwords do not match', () => {
      component.password = 'lozinka123';
      component.confirmPassword = 'drugalozinka'
      
      component.onSubmit()

      expect(component.error).toBe('Passwords must match')
      expect(component.isLoading).toBe(false)
    })

    it('should show error if password too weak', () => {
      component.password = '123'
      component.confirmPassword = '123'
      
      authServiceMock.isPasswordValid.and.returnValue('Password too weak')

      component.onSubmit()

      expect(component.error).toBe('Password too weak')
      expect(authServiceMock.createRegisterRequest).not.toHaveBeenCalled()
    })

    it('should show error if password is only whitespace', () => {
      component.password = '   '
      component.confirmPassword = '   '
      component.firstName = 'neko'
      component.email = 'test@test.com'

      authServiceMock.isPasswordValid.and.returnValue('Password too weak');

      component.onSubmit();
      expect(component.error).toBe('Password too weak')
    })

    it('should show error if password is empty but confirmPassword is filled', () => {
      component.password = '';
      component.confirmPassword = 'sifra123'
      component.firstName = 'Teo'
      component.email = 'test@test.com'

      component.onSubmit()

      expect(component.error).toBe('Passwords must match')
      expect(component.isLoading).toBe(false)
    })

    it('should show error if confirmPassword is empty but password is filled', () => {
      component.password = 'sifra123'
      component.confirmPassword = ''
      component.firstName = 'neko'
      component.email = 'test@test.com'

      component.onSubmit()

      expect(component.error).toBe('Passwords must match')
      expect(component.isLoading).toBe(false)
    })
  })

  describe('Form submission', () => {
    it('should set success message on successful registration', () => {
      component.firstName = 'John'
      component.lastName = 'Doe'
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'
      
      authServiceMock.isPasswordValid.and.returnValue(null)
      authServiceMock.createRegisterRequest.and.returnValue(of({ message: 'Registration Successful' }))

      component.onSubmit()

      expect(component.success).toBe('Registration Successful')
      expect(component.error).toBe('')
      expect(component.isLoading).toBe(false)
    })

    it('should prevent multiple submissions while loading', () => {
      component.firstName = 'neko'
      component.password = 'sifra'
      component.confirmPassword = 'sifra'

      authServiceMock.isPasswordValid.and.returnValue(null)
      authServiceMock.createRegisterRequest.and.returnValue(new Observable(() => {}))

      component.onSubmit()
      expect(component.isLoading).toBe(true)

      component.onSubmit();
      expect(authServiceMock.createRegisterRequest).toHaveBeenCalledTimes(1)
    })

    it('should handle user already exists - 409', () => {
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'
      authServiceMock.isPasswordValid.and.returnValue(null)

      const errorResponse = new HttpErrorResponse({
        error: { message: "User with this email already exists" },
        status: 409
      })
      authServiceMock.createRegisterRequest.and.returnValue(throwError(() => errorResponse))

      component.onSubmit()

      expect(component.error).toBe("User with this email already exists")
      expect(component.isLoading).toBe(false)
    })

    it('should handle 503 Service unavailable', () => {
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'
      authServiceMock.isPasswordValid.and.returnValue(null)

      const errorResponse = new HttpErrorResponse({ status: 503 })
      authServiceMock.createRegisterRequest.and.returnValue(throwError(() => errorResponse))

      component.onSubmit()

      expect(component.error).toBe('Service temporarily unavailable')
    })

    it('should handle 500 Internal Server Error', () => {
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'
      authServiceMock.isPasswordValid.and.returnValue(null)

      const errorResponse = new HttpErrorResponse({ status: 500 })
      authServiceMock.createRegisterRequest.and.returnValue(throwError(() => errorResponse))

      component.onSubmit()

      expect(component.error).toBe('Internal server error')
    })

    it('should handle unexpected HTTP error', () => {
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'
      authServiceMock.isPasswordValid.and.returnValue(null)

      const errorResponse = new HttpErrorResponse({ status: 402 })
      authServiceMock.createRegisterRequest.and.returnValue(throwError(() => errorResponse))

      component.onSubmit()
      expect(component.error).toBe('An unexpected error occurred')
    })

    it('should handle missing name', () => {
      component.firstName = ''
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'

      const errorResponse = new HttpErrorResponse({ status: 500 })
      authServiceMock.createRegisterRequest.and.returnValue(throwError(() => errorResponse))

      component.onSubmit()

      expect(component.error).toBe('Internal server error')
      expect(component.isLoading).toBe(false)
    })
  })

  describe('File upload', () => {
    it('should update profile picture when a file is selected', () => {
      const file = new File([''], 'discord nft.jpg', { type: 'image/jpeg' });
      const event = {
        target: {
          files: [file]
        }
      } as unknown as Event

      component.onFileChange(event);
      expect(component.profilePic).toBe(file)
    })

    it('should handle file not uploaded', () => {
      component.profilePic = null
      component.password = 'sifraNeka123'
      component.confirmPassword = 'sifraNeka123'
    
      authServiceMock.isPasswordValid.and.returnValue(null)
      authServiceMock.createRegisterRequest.and.returnValue(of({ message: 'ok' }))
      const appendSpy = spyOn(FormData.prototype, 'append')

      component.onSubmit()
      expect(appendSpy).not.toHaveBeenCalledWith('profilePhoto', jasmine.any(Object))
    })
  })

  describe('Data transformations', () => {
    it('should uppercase gender correctly even if lower case', () => {
      component.gender = 'female'
      component.password = 'sifraNeka123!'
      component.confirmPassword = 'sifraNeka123!'
      authServiceMock.isPasswordValid.and.returnValue(null)
      authServiceMock.createRegisterRequest.and.returnValue(of({ message: 'ok' }))

      component.onSubmit()
      expect(authServiceMock.createRegisterRequest).toHaveBeenCalled()
      
      const formDataArg = authServiceMock.createRegisterRequest.calls.mostRecent().args[0] as FormData
      expect(formDataArg.get('gender')).toBe('FEMALE')
    })

    it('should concatenate street, city, country correctly', () => {
      component.firstName = 'neko'
      component.lastName = 'nekic'
      component.street = 'bulevar'
      component.city = 'Novi Sad'
      component.country = 'Srbija'
      component.password = 'as'
      component.confirmPassword = 'as'

      authServiceMock.isPasswordValid.and.returnValue(null)
      authServiceMock.createRegisterRequest.and.returnValue(of({ message: 'ok' }))
      const appendSpy = spyOn(FormData.prototype, 'append')

      component.onSubmit();
      expect(appendSpy).toHaveBeenCalledWith(
        'address',
        jasmine.any(String)
      )
    })
  })
})