import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth-service/auth.service';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    authServiceMock = {
      register: jest.fn()
    };

    routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize registerForm on ngOnInit', () => {
    // Given - ngOnInit called automatically by Angular during fixture creation

    // Then
    expect(component.registerForm).toBeDefined();
    expect(component.registerForm.controls['username']).toBeDefined();
    expect(component.registerForm.controls['email']).toBeDefined();
    expect(component.registerForm.controls['password']).toBeDefined();
  });

  it('should call authService.register and navigate on successful registration', () => {
    // Given
    const mockResponse = { userId: 1, message: 'Registered successfully' };
    authServiceMock.register.mockReturnValue(of(mockResponse));
    component.registerForm.setValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

    // When
    component.register();

    // Then
    expect(authServiceMock.register).toHaveBeenCalledWith('testuser', 'test@example.com', 'password123');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
    expect(consoleSpy).toHaveBeenCalledWith('Registration successful', mockResponse);
  });

  it('should set errorMessage and log error on registration failure', () => {
    // Given
    const error = new Error('Registration error');
    authServiceMock.register.mockReturnValue(throwError(() => error));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    component.registerForm.setValue({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });

    // When
    component.register();

    // Then
    expect(authServiceMock.register).toHaveBeenCalled();
    expect(component.errorMessage).toBe(error.message);
    expect(consoleSpy).toHaveBeenCalledWith('Registration failed', error);
  });

  it('should complete destroy$ on ngOnDestroy', () => {
    // Given
    const spyNext = jest.spyOn(component['destroy$'], 'next');
    const spyComplete = jest.spyOn(component['destroy$'], 'complete');

    // When
    component.ngOnDestroy();

    // Then
    expect(spyNext).toHaveBeenCalled();
    expect(spyComplete).toHaveBeenCalled();
  });
});
