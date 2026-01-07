import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ExpenseService } from './expense.service';
import { JwtService } from '../jwt-service/jwt.service';
import { ExpenseRequest } from '../../models/ExpenseRequest';
import { ExpenseResponse } from '../../models/ExpenseResponse';
import { ExpenseCategory } from '../../models/enums/ExpenseCategory';

describe('ExpenseService', () => {
  let service: ExpenseService;
  let jwtService: JwtService;
  let httpTestingController: HttpTestingController;
  const baseUrl: string = 'http://localhost:8080/api/expenses';
  const mockEmail = 'test@example.com';
  const mockToken = 'mock-jwt-token';
  const mockHeaders = { Authorization: `Bearer ${mockToken}` };
  const mockJwtServiceResponse = { email: mockEmail, headers: mockHeaders };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ExpenseService,
        {
          provide: JwtService,
          useValue: {
            getEmailAndHeaders: jest.fn(() => mockJwtServiceResponse),
          },
        },
      ],
    });

    service = TestBed.inject(ExpenseService);
    jwtService = TestBed.inject(JwtService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all expenses for the user', () => {
    // Given
    const mockExpenses: ExpenseResponse[] = [{ id: 1, name: 'Rent', description: 'Monthly Rent', amount: 1000, date: '2023-01-01', category: ExpenseCategory.HOUSING, userId: 123 }];

    // When
    service.getAllExpenses().subscribe((expenses) => {
      // Then
      expect(expenses).toEqual(mockExpenses);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(mockExpenses);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });

  it('should create a new expense for the user', () => {
    // Given
    const newExpense: ExpenseRequest = { name: 'Groceries', description: 'Weekly groceries', amount: 150, date: '2023-01-05', category: ExpenseCategory.FOOD };
    
    const createdExpense: ExpenseResponse = { id: 2, name: newExpense.name, description: newExpense.description || '', amount: newExpense.amount, date: newExpense.date, category: newExpense.category, userId: 123 };

    // When
    service.createExpense(newExpense).subscribe((expense) => {
      // Then
      expect(expense).toEqual(createdExpense);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newExpense);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(createdExpense);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });

  it('should update the specified expense for the user', () => {
    // Given
    const expenseId = 1;
    
    const updatedExpense: ExpenseRequest = { name: 'Updated Rent', description: 'Updated Monthly Rent', amount: 1100, date: '2023-01-15', category: ExpenseCategory.HOUSING };

    const responseExpense: ExpenseResponse = { id: expenseId, name: updatedExpense.name, description: updatedExpense.description || '', amount: updatedExpense.amount, date: updatedExpense.date, category: updatedExpense.category, userId: 123 };

    // When
    service.updateExpense(expenseId, updatedExpense).subscribe((expense) => {
      // Then
      expect(expense).toEqual(responseExpense);
    });

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}/${expenseId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedExpense);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(responseExpense);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });

  it('should delete the specified expense for the user', () => {
    // Given
    const expenseId = 1;

    // When
    service.deleteExpense(expenseId).subscribe();

    const req = httpTestingController.expectOne(`${baseUrl}/${mockEmail}/${expenseId}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);
    req.flush(null);

    expect(jwtService.getEmailAndHeaders).toHaveBeenCalled();
  });
});
