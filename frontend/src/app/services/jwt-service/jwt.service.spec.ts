import { TestBed } from '@angular/core/testing';
import { JwtService } from './jwt.service';
import { jwtDecode } from 'jwt-decode';

jest.mock('jwt-decode', () => ({
  jwtDecode: jest.fn(),
}));

describe('JwtService', () => {
  let service: JwtService;
  let originalDocumentCookie: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JwtService],
    });
    service = TestBed.inject(JwtService);

    originalDocumentCookie = document.cookie;

    // Clear cookies for a clean test environment
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: '',
    });
  });

  afterEach(() => {
    // Restore original document.cookie
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: originalDocumentCookie,
    });
    jest.clearAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should decode a JWT token', () => {
    // Given
    const token = 'mock-jwt-token';
    const decodedPayload = { sub: 'test@example.com', iat: 1516239022 };
    (jwtDecode as jest.Mock).mockReturnValue(decodedPayload);

    // When
    const result = service.decodeToken(token);

    // Then
    expect(jwtDecode).toHaveBeenCalledWith(token);
    expect(result).toEqual(decodedPayload);
  });

  it('should return the token from the cookie if it exists', () => {
    // Given
    const mockToken = 'mock-jwt-token-from-cookie';
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: `jwt=${mockToken}; otherCookie=value`,
    });

    // When
    const result = service.getTokenFromCookie();

    // Then
    expect(result).toBe(mockToken);
  });

  it('should return an empty string if the token is not found in the cookie', () => {
    // Given
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: 'otherCookie=value',
    });

    // When
    const result = service.getTokenFromCookie();

    // Then
    expect(result).toBe('');
  });

  it('should return the email from the decoded token', () => {
    // Given
    const mockToken = 'mock-jwt-token';
    const mockEmail = 'user@example.com';
    const decodedPayload = { sub: mockEmail };
    jest.spyOn(service, 'getTokenFromCookie').mockReturnValue(mockToken);
    (jwtDecode as jest.Mock).mockReturnValue(decodedPayload);

    // When
    const result = service.getEmailFromToken();

    // Then
    expect(service.getTokenFromCookie).toHaveBeenCalled();
    expect(jwtDecode).toHaveBeenCalledWith(mockToken);
    expect(result).toBe(mockEmail);
  });

  it('should return an empty string if the token is empty when getting email', () => {
    // Given
    jest.spyOn(service, 'getTokenFromCookie').mockReturnValue('');

    // When
    const result = service.getEmailFromToken();

    // Then
    expect(service.getTokenFromCookie).toHaveBeenCalled();
    expect(result).toBe('');
  });

  it('should create authorization headers with the token', () => {
    // Given
    const mockToken = 'mock-jwt-token';
    jest.spyOn(service, 'getTokenFromCookie').mockReturnValue(mockToken);

    // When
    const result = service.createHeaders();

    // Then
    expect(service.getTokenFromCookie).toHaveBeenCalled();
    expect(result).toEqual({ Authorization: `Bearer ${mockToken}` });
  });

  it('should return email and headers when getEmailAndHeaders is called', () => {
    // Given
    const mockEmail = 'user@example.com';
    const mockToken = 'mock-jwt-token';
    const mockHeaders = { Authorization: `Bearer ${mockToken}` };

    jest.spyOn(service, 'getEmailFromToken').mockReturnValue(mockEmail);
    jest.spyOn(service, 'createHeaders').mockReturnValue(mockHeaders);

    // When
    const result = service.getEmailAndHeaders();

    // Then
    expect(service.getEmailFromToken).toHaveBeenCalled();
    expect(service.createHeaders).toHaveBeenCalled();
    expect(result).toEqual({ email: mockEmail, headers: mockHeaders });
  });

  it('should remove the JWT token from the cookie', () => {
    // Given
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: 'jwt=mock-token; otherCookie=value',
    });

    // When
    service.removeTokenFromCookie();

    // Then
    expect(document.cookie).toBe('jwt=;');
  });
});
