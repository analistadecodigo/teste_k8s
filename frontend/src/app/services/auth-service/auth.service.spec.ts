import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse } from '../../models/AuthResponse';
import { RegisterResponse } from '../../models/RegisterResponse';
import { JwtService } from '../jwt-service/jwt.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;
  let jwtService: JwtService;
  const baseUrl: string = 'http://localhost:8080/api/auth';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        {
          provide: JwtService,
          useValue: {
            removeTokenFromCookie: jest.fn(),
          },
        },
      ],
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
    jwtService = TestBed.inject(JwtService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in a user and return an AuthResponse', () => {
    // Given
    const email = 'test@example.com';
    const password = 'password123';
    const mockAuthResponse: AuthResponse = { tokenJWT: 'mock-token' };
    // When
    service.login(email, password).subscribe((response) => {
      // Then
      expect(response).toEqual(mockAuthResponse);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email, password });
    req.flush(mockAuthResponse);
  });

  it('should register a new user and return a RegisterResponse', () => {
    // Given
    const name = 'Test User';
    const email = 'newuser@example.com';
    const password = 'newpassword123';
    const mockRegisterResponse: RegisterResponse = { id: 1, name: 'Test User', email: 'newuser@example.com' };
    // When
    service.register(name, email, password).subscribe((response) => {
      // Then
      expect(response).toEqual(mockRegisterResponse);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/register`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name, email, password });
    req.flush(mockRegisterResponse);
  });

  it('should call removeTokenFromCookie when logout is called', () => {
    // When
    service.logout();

    // Then
    expect(jwtService.removeTokenFromCookie).toHaveBeenCalled();
  });
});
