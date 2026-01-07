import { DashboardComponent } from './dashboard.component';
import { FinancialService } from '../../services/financial-service/financial.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth-service/auth.service';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let financialServiceMock: jest.Mocked<FinancialService>;
  let routerMock: jest.Mocked<Router>;
  let httpClientMock: jest.Mocked<HttpClient>;
  let authServiceMock: jest.Mocked<AuthService>;

  beforeEach(() => {
    financialServiceMock = {
      getFinancialSummary: jest.fn()
    } as unknown as jest.Mocked<FinancialService>;

    routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    httpClientMock = {
      get: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    authServiceMock = {
      logout: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    TestBed.configureTestingModule({
      providers: [
        DashboardComponent,
        { provide: FinancialService, useValue: financialServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: HttpClient, useValue: httpClientMock },
        { provide: AuthService, useValue: authServiceMock }
      ]
    });

    component = TestBed.inject(DashboardComponent);
  });

  it('should call getFinancialSummary on init and set financialSummary', () => {
    // Given
    financialServiceMock.getFinancialSummary.mockReturnValue(of({
      name: 'Test User',
      totalIncome: 1000,
      totalExpenses: 500,
      balance: 500
    }));

    // When
    component.ngOnInit();

    // Then
    expect(financialServiceMock.getFinancialSummary).toHaveBeenCalled();
    expect(component.financialSummary.name).toBe('Test User');
    expect(component.financialSummary.totalIncome).toBe(1000);
    expect(component.financialSummary.totalExpenses).toBe(500);
    expect(component.financialSummary.balance).toBe(500);
  });

  it('should log error if getFinancialSummary fails', () => {
    // Given
    const error = new Error('Failed');
    financialServiceMock.getFinancialSummary.mockReturnValue(throwError(() => error));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    // When
    component.ngOnInit();

    // Then
    expect(consoleSpy).toHaveBeenCalledWith('Error fetching financial summary:', error);
  });

  it('should navigate to /gerenciar-renda and log message when gerenciarRenda is called', () => {
    // Given
    jest.spyOn(console, 'log').mockImplementation(() => {});

    // When
    component.gerenciarRenda();

    // Then
    expect(routerMock.navigate).toHaveBeenCalledWith(['/gerenciar-renda']);
    expect(console.log).toHaveBeenCalledWith('Navigating to Gerenciar Renda');
  });

  it('should navigate to /gerenciar-despesa when gerenciarDespesa is called', () => {
    // When
    component.gerenciarDespesa();

    // Then
    expect(routerMock.navigate).toHaveBeenCalledWith(['/gerenciar-despesa']);
  });

  it('should complete destroy$ subject on ngOnDestroy', () => {
    // Given
    const nextSpy = jest.spyOn(component['destroy$'], 'next');
    const completeSpy = jest.spyOn(component['destroy$'], 'complete');

    // When
    component.ngOnDestroy();

    // Then
    expect(nextSpy).toHaveBeenCalled();
    expect(completeSpy).toHaveBeenCalled();
  });

  it('should call authService.logout and navigate to /login when logout is called', () => {
    // When
    component.logout();

    // Then
    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });
});
