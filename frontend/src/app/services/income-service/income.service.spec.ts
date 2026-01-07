import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { IncomeService } from './income.service';
import { JwtService } from '../jwt-service/jwt.service';
import { IncomeRequest } from '../../models/IncomeRequest';
import { IncomeResponse } from '../../models/IncomeResponse';
import { IncomeCategory } from '../../models/enums/IncomeCategory';

describe('IncomeService', () => {
  let service: IncomeService;
  let jwtService: JwtService;
  let httpTestingController: HttpTestingController;
  const baseUrl: string = 'http://localhost:8080/api/incomes';
  const mockEmail = 'test@example.com';
  const mockToken = 'mock-jwt-token';
  const mockHeaders = { Authorization: `Bearer ${mockToken}` };
  const mockJwtServiceResponse = { email: mockEmail, headers: mockHeaders };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        IncomeService,
        {
          provide: JwtService,
          useValue: {
            getEmailAndHeaders: jest.fn(() => mockJwtServiceResponse),
          },
        },
      ],
    });

    service = TestBed.inject(IncomeService);
    jwtService = TestBed.inject(JwtService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all incomes for the user', () => {
    // Given
    const mockIncomes: IncomeResponse[] = [{ id: 1, name: 'Salary', description: 'Monthly Salary', amount: 3000, date: '2023-01-01', category: IncomeCategory.EARNINGS, userId: 123 }];
    // When
    service.getAllIncomes().subscribe((incomes) => {
      // Then
      expect(incomes).toEqual(mockIncomes);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(mockIncomes);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });

  it('should create a new income for the user', () => {
    // Given
    const newIncome: IncomeRequest = { name: 'Bonus', description: 'Annual Bonus', amount: 500, date: '2023-02-01', category: IncomeCategory.COMMISSION };
    const createdIncome: IncomeResponse = { id: 2, name: newIncome.name, description: newIncome.description || '', amount: newIncome.amount, date: newIncome.date, category: newIncome.category, userId: 123 };
    // When
    service.createIncome(newIncome).subscribe((income) => {
      // Then
      expect(income).toEqual(createdIncome);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newIncome);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(createdIncome);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });

  it('should update the specified income for the user', () => {
    // Given
    const incomeId = 1;
    const updatedIncome: IncomeRequest = { name: 'Updated Salary', description: 'Updated Monthly Salary', amount: 3500, date: '2023-01-15', category: IncomeCategory.EARNINGS };
    const responseIncome: IncomeResponse = { id: incomeId, name: updatedIncome.name, description: updatedIncome.description || '', amount: updatedIncome.amount, date: updatedIncome.date, category: updatedIncome.category, userId: 123 };
    // When
    service.updateIncome(incomeId, updatedIncome).subscribe((income) => {
      // Then
      expect(income).toEqual(responseIncome);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}/${incomeId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedIncome);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(responseIncome);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });

  it('should delete the specified income for the user', () => {
    // Given
    const incomeId = 1;

    // When
    service.deleteIncome(incomeId).subscribe();

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}/${incomeId}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(null);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });
});
