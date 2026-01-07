import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth-service/auth.service';
import { AuthResponse } from '../../models/AuthResponse';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { TestBed, ComponentFixture } from '@angular/core/testing';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authServiceMock: any;
  let routerMock: any;
  let formBuilder: FormBuilder;
  let fixture: any;

  beforeEach(() => {
    authServiceMock = {
      login: jest.fn()
    };

    routerMock = {
      navigate: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        LoginComponent,
        FormBuilder,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    formBuilder = TestBed.inject(FormBuilder);

    fixture.detectChanges();
  });

  it('should initialize loginForm with email and password controls', () => {
    // When
    component.ngOnInit();

    // Then
    expect(component.loginForm).toBeDefined();
    expect(component.loginForm.get('email')).toBeDefined();
    expect(component.loginForm.get('password')).toBeDefined();
  });

  it('should call authService.login and navigate on successful login', () => {
    // Given
    component.ngOnInit();
    component.loginForm.setValue({ email: 'test@example.com', password: 'password123' });
    
    const mockResponse: AuthResponse = { tokenJWT: 'fake-jwt-token' };

    authServiceMock.login.mockReturnValue(of(mockResponse));

    const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

    // Mock document.cookie setter
    let cookie = '';
    Object.defineProperty(document, 'cookie', {
      configurable: true,
      get: () => cookie,
      set: (val: string) => { cookie = val; }
    });
  
    // When
    component.login();
  
    // Then
    expect(authServiceMock.login).toHaveBeenCalledWith('test@example.com', 'password123');
    expect(cookie).toContain(`jwt=${mockResponse.tokenJWT}`);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(consoleSpy).toHaveBeenCalledWith('Login successful');
  });
  
  it('should set errorMessage and log error on login failure', () => {
    // Given
    component.ngOnInit();
    component.loginForm.setValue({ email: 'fail@example.com', password: 'wrongpass' });
    const error = new Error('Invalid credentials');
    authServiceMock.login.mockReturnValue(throwError(() => error));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    // When
    component.login();

    // Then
    expect(authServiceMock.login).toHaveBeenCalledWith('fail@example.com', 'wrongpass');
    expect(component.errorMessage).toBe(error.message);
    expect(consoleSpy).toHaveBeenCalledWith('Login failed:', error);
  });

  it('should complete destroy$ on ngOnDestroy', () => {
    // Given
    component.ngOnInit();
    const nextSpy = jest.spyOn(component['destroy$'], 'next');
    const completeSpy = jest.spyOn(component['destroy$'], 'complete');

    // When
    component.ngOnDestroy();

    // Then
    expect(nextSpy).toHaveBeenCalled();
    expect(completeSpy).toHaveBeenCalled();
  });
});
