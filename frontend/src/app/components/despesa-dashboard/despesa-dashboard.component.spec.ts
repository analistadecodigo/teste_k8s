import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DespesaDashboardComponent } from './despesa-dashboard.component';
import { ExpenseService } from '../../services/expense-service/expense.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { ListItem } from '../../models/ListItem';
import { ExpenseResponse } from '../../models/ExpenseResponse';
import { ItemAction } from '../../models/enums/ItemAction';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ExpenseCategory } from '../../models/enums/ExpenseCategory';

describe('DespesaDashboardComponent', () => {
  let component: DespesaDashboardComponent;
  let fixture: ComponentFixture<DespesaDashboardComponent>;
  let mockExpenseService: jest.Mocked<ExpenseService>;
  let mockDialog: jest.Mocked<MatDialog>;

  const mockExpenses: ExpenseResponse[] = [
    {
      id: 1,
      name: 'Aluguel',
      description: 'Pagamento do mês',
      amount: 1200,
      date: '2023-07-01',
      userId: 1,
      category: ExpenseCategory.HOUSING
    }
  ];

  beforeEach(async () => {
    mockExpenseService = {
      getAllExpenses: jest.fn(),
      deleteExpense: jest.fn()
    } as unknown as jest.Mocked<ExpenseService>;

    mockDialog = {
      open: jest.fn()
    } as unknown as jest.Mocked<MatDialog>;

    await TestBed.configureTestingModule({
      declarations: [DespesaDashboardComponent],
      providers: [
        { provide: ExpenseService, useValue: mockExpenseService },
        { provide: MatDialog, useValue: mockDialog }
      ],
      imports: [MatDialogModule],
      schemas: [NO_ERRORS_SCHEMA] // ignora app-item-list
    }).compileComponents();

    fixture = TestBed.createComponent(DespesaDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    // Then
    expect(component).toBeTruthy();
  });

  it('should fetch expenses on init', () => {
    // Given
    mockExpenseService.getAllExpenses.mockReturnValue(of(mockExpenses));

    // When
    component.ngOnInit();

    // Then
    expect(mockExpenseService.getAllExpenses).toHaveBeenCalled();
    expect(component.despesas.length).toBe(1);
    expect(component.despesas[0].name).toBe('Aluguel');
  });

  it('should handle error on getExpenses', () => {
    // Given
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    mockExpenseService.getAllExpenses.mockReturnValue(throwError(() => new Error('Erro ao buscar despesas')));

    // When
    component.getExpenses();

    // Then
    expect(consoleSpy).toHaveBeenCalledWith('Error fetching expenses:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('should delete expense when ItemAction.Delete is triggered', () => {
    // Given
    const expenseToDelete: ListItem = {
      id: 1, name: 'Aluguel', description: '', amount: 100, date: '', userId: 1, category: ''
    };
    
    mockExpenseService.deleteExpense.mockReturnValue(of());

    mockExpenseService.getAllExpenses.mockReturnValue(of([]));

    // When
    component.handleExpenseAction({ item: expenseToDelete, action: ItemAction.Delete });

    // Then
    expect(mockExpenseService.deleteExpense).toHaveBeenCalledWith(expenseToDelete.id);
  });

  it('should call getExpenses when ItemAction.Create is triggered', () => {
    // Given
    mockExpenseService.getAllExpenses.mockReturnValue(of([]));
    const spy = jest.spyOn(component, 'getExpenses');

    // When
    component.handleExpenseAction({ item: {} as ListItem, action: ItemAction.Create });

    // Then
    expect(spy).toHaveBeenCalled();
  });

  it('should call getExpenses when ItemAction.Update is triggered', () => {
    // Given
    mockExpenseService.getAllExpenses.mockReturnValue(of([]));
    const spy = jest.spyOn(component, 'getExpenses');

    // When
    component.handleExpenseAction({ item: {} as ListItem, action: ItemAction.Update });

    // Then
    expect(spy).toHaveBeenCalled();
  });

  it('should open modal and refresh if result is returned', () => {
    // Given
    const afterClosed$ = of(true); // Simula fechamento com valor
    mockDialog.open.mockReturnValue({ afterClosed: () => afterClosed$ } as any);
    const spy = jest.spyOn(component, 'getExpenses');

    // When
    component.openExpenseModal(null);

    // Then
    expect(mockDialog.open).toHaveBeenCalled();
    expect(spy).toHaveBeenCalled();
  });

  it('should open modal and not refresh if result is falsy', () => {
    // Given
    const afterClosed$ = of(null); // Simula fechamento sem valor
    mockDialog.open.mockReturnValue({ afterClosed: () => afterClosed$ } as any);
    const spy = jest.spyOn(component, 'getExpenses');

    // When
    component.openExpenseModal(null);

    // Then
    expect(mockDialog.open).toHaveBeenCalled();
    expect(spy).not.toHaveBeenCalled();
  });
});
