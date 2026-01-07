import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { RendaDashboardComponent } from './renda-dashboard.component';
import { IncomeService } from '../../services/income-service/income.service';
import { IncomeResponse } from '../../models/IncomeResponse';
import { ItemAction } from '../../models/enums/ItemAction';
import { ListItem } from '../../models/ListItem';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { IncomeCategory } from '../../models/enums/IncomeCategory';

describe('RendaDashboardComponent', () => {
  let component: RendaDashboardComponent;
  let fixture: ComponentFixture<RendaDashboardComponent>;
  let incomeService: jest.Mocked<IncomeService>;

  const mockIncomes: IncomeResponse[] = [
    {
      id: 1,
      name: 'Salário',
      description: 'Pagamento mensal',
      amount: 3000,
      date: '2023-01-01',
      userId: 1,
      category: IncomeCategory.PAYMENTS
    }
  ];

  const mappedItems: ListItem[] = mockIncomes.map(income => ({
    ...income
  }));

  beforeEach(async () => {
    const incomeServiceMock: Partial<jest.Mocked<IncomeService>> = {
      getAllIncomes: jest.fn().mockReturnValue(of([])),
      deleteIncome: jest.fn()
    };
  
    await TestBed.configureTestingModule({
      declarations: [RendaDashboardComponent],
      providers: [
        { provide: IncomeService, useValue: incomeServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  
    fixture = TestBed.createComponent(RendaDashboardComponent);
    component = fixture.componentInstance;
    incomeService = TestBed.inject(IncomeService) as jest.Mocked<IncomeService>;
  
    fixture.detectChanges();
  });
  
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call getAllIncomes on init and map results', () => {
    incomeService.getAllIncomes.mockReturnValue(of(mockIncomes));

    component.ngOnInit();

    expect(incomeService.getAllIncomes).toHaveBeenCalled();
    expect(component.rendas).toEqual(mappedItems);
  });

  it('should handle error when getAllIncomes fails', () => {
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    incomeService.getAllIncomes.mockReturnValue(throwError(() => new Error('API error')));

    component.getIncomes();

    expect(consoleSpy).toHaveBeenCalledWith('Error fetching incomes:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('should call getIncomes when handleIncomeAction receives Create', () => {
    const spy = jest.spyOn(component, 'getIncomes');
    incomeService.getAllIncomes.mockReturnValue(of(mockIncomes));

    component.handleIncomeAction({ item: mappedItems[0], action: ItemAction.Create });

    expect(spy).toHaveBeenCalled();
  });

  it('should call getIncomes when handleIncomeAction receives Update', () => {
    const spy = jest.spyOn(component, 'getIncomes');
    incomeService.getAllIncomes.mockReturnValue(of(mockIncomes));

    component.handleIncomeAction({ item: mappedItems[0], action: ItemAction.Update });

    expect(spy).toHaveBeenCalled();
  });

  it('should call deleteIncome and then getIncomes when handleIncomeAction receives Delete', () => {
    jest.spyOn(incomeService, 'deleteIncome').mockReturnValue(of(undefined));

    const spy = jest.spyOn(component, 'getIncomes'); 

    const itemToDelete = mappedItems[0];
  
    component.handleIncomeAction({
      action: ItemAction.Delete,
      item: itemToDelete
    });
  
    expect(incomeService.deleteIncome).toHaveBeenCalledWith(1);
    expect(spy).toHaveBeenCalled(); 
  });
  
  it('should log error when deleteIncome fails', () => {
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    incomeService.deleteIncome.mockReturnValue(throwError(() => new Error('Delete error')));

    component.deleteIncome(1);

    expect(consoleSpy).toHaveBeenCalledWith('Error deleting income:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('should complete destroy$ on ngOnDestroy', () => {
    const destroyNextSpy = jest.spyOn(component['destroy$'], 'next');
    const destroyCompleteSpy = jest.spyOn(component['destroy$'], 'complete');

    component.ngOnDestroy();

    expect(destroyNextSpy).toHaveBeenCalled();
    expect(destroyCompleteSpy).toHaveBeenCalled();
  });
});
