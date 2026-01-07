import { TestBed } from '@angular/core/testing';
import { FinancialService } from './financial.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { JwtService } from '../jwt-service/jwt.service';
import { FinancialSummary } from '../../models/FinancialSummary';

describe('FinancialService', () => {
  let service: FinancialService;
  let httpMock: HttpTestingController;
  let jwtServiceMock: { getEmailAndHeaders: jest.Mock };

  beforeEach(() => {
    jwtServiceMock = {
      getEmailAndHeaders: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        FinancialService,
        { provide: JwtService, useValue: jwtServiceMock }
      ]
    });

    service = TestBed.inject(FinancialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call getFinancialSummary and return data', () => {
    // Given
    const mockEmail = 'test@example.com';
    const mockHeaders = { Authorization: 'Bearer token' };
    const mockSummary: FinancialSummary = {
      name: 'User',
      totalIncome: 1000,
      totalExpenses: 500,
      balance: 500
    };

    jwtServiceMock.getEmailAndHeaders.mockReturnValue({
      email: mockEmail,
      headers: mockHeaders
    });

    // When
    service.getFinancialSummary().subscribe(summary => {
      // Then
      expect(summary).toEqual(mockSummary);
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/financial-summary/${mockEmail}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer token');

    req.flush(mockSummary);
  });
});
