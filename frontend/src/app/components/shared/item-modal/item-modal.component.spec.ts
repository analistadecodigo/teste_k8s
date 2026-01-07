import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ItemModalComponent } from './item-modal.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { IncomeService } from '../../../services/income-service/income.service';
import { ExpenseService } from '../../../services/expense-service/expense.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';


describe('ItemModalComponent', () => {
  let component: ItemModalComponent;
  let fixture: ComponentFixture<ItemModalComponent>;
  let incomeServiceMock: any;
  let expenseServiceMock: any;
  let dialogRefMock: any;

  const mockDialogDataIncome = {
    item: null,
    itemType: 'income'
  };

  const mockDialogDataExpense = {
    item: null,
    itemType: 'expense'
  };

  beforeEach(async () => {
    incomeServiceMock = {
      createIncome: jest.fn().mockReturnValue(of({})),
      updateIncome: jest.fn().mockReturnValue(of({})),
      getAllIncomes: jest.fn().mockReturnValue(of([]))
    };
    expenseServiceMock = {
      createExpense: jest.fn().mockReturnValue(of({})),
      updateExpense: jest.fn().mockReturnValue(of({})),
      getAllExpenses: jest.fn().mockReturnValue(of([]))
    };
    dialogRefMock = {
      close: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [ItemModalComponent],
      imports: [
        ReactiveFormsModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule
      ],
      providers: [
        FormBuilder,
        { provide: IncomeService, useValue: incomeServiceMock },
        { provide: ExpenseService, useValue: expenseServiceMock },
        { provide: MatDialogRef, useValue: dialogRefMock },
        { provide: MAT_DIALOG_DATA, useValue: mockDialogDataIncome },
        { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  function setupComponentWithData(data: any) {
    TestBed.overrideProvider(MAT_DIALOG_DATA, { useValue: data });
    fixture = TestBed.createComponent(ItemModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create the component and initialize form with income categories', () => {
    setupComponentWithData(mockDialogDataIncome);
    expect(component).toBeTruthy();
    expect(component.categories).toEqual(Object.values(component.categories));
    expect(component.itemForm).toBeDefined();
  });

  it('should load expense categories when itemType is expense', () => {
    setupComponentWithData(mockDialogDataExpense);
    expect(component.categories).toEqual(Object.values(component.categories));
  });

  it('should patch form values when editing an item', () => {
    const editItem = {
      id: 1,
      name: 'Test Income',
      description: 'Description',
      amount: 100,
      category: 'Salary',
      date: new Date().toISOString()
    };
    setupComponentWithData({ item: editItem, itemType: 'income' });
    expect(component.isEditMode).toBe(true);
    expect(component.itemForm.value.name).toBe(editItem.name);
    expect(component.itemForm.value.amount).toBe(editItem.amount);
  });

  it('should create income and close dialog on save when in create income mode', () => {
    setupComponentWithData(mockDialogDataIncome);
    component.itemForm.setValue({
      id: null,
      name: 'New Income',
      description: 'Desc',
      amount: 200,
      category: 'Salary',
      date: new Date()
    });
    component.save();
    expect(incomeServiceMock.createIncome).toHaveBeenCalled();
    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('should update income and close dialog on save when in edit income mode', () => {
    const editItem = {
      id: 2,
      name: 'Edit Income',
      description: 'Desc',
      amount: 300,
      category: 'Bonus',
      date: new Date().toISOString()
    };
    setupComponentWithData({ item: editItem, itemType: 'income' });
    component.itemForm.patchValue({ id: editItem.id });
    component.save();
    expect(incomeServiceMock.updateIncome).toHaveBeenCalledWith(editItem.id, expect.any(Object));
    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('should create expense and close dialog on save when in create expense mode', () => {
    setupComponentWithData(mockDialogDataExpense);
    component.itemForm.setValue({
      id: null,
      name: 'New Expense',
      description: 'Desc',
      amount: 150,
      category: 'Food',
      date: new Date()
    });
    component.save();
    expect(expenseServiceMock.createExpense).toHaveBeenCalled();
    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('should update expense and close dialog on save when in edit expense mode', () => {
    const editItem = {
      id: 3,
      name: 'Edit Expense',
      description: 'Desc',
      amount: 120,
      category: 'Transport',
      date: new Date().toISOString()
    };
    setupComponentWithData({ item: editItem, itemType: 'expense' });
    component.itemForm.patchValue({ id: editItem.id });
    component.save();
    expect(expenseServiceMock.updateExpense).toHaveBeenCalledWith(editItem.id, expect.any(Object));
    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('should patch date when updateDate is called', () => {
    setupComponentWithData(mockDialogDataIncome);
    const newDate = new Date(2025, 6, 18);
    component.updateDate({ value: newDate } as any);
    expect(component.itemForm.value.date).toEqual(newDate);
  });

  it('should close the dialog when close() is called', () => {
    setupComponentWithData(mockDialogDataIncome);
    component.close();
    expect(dialogRefMock.close).toHaveBeenCalledWith(false);
  });

  it('should complete destroy$ on ngOnDestroy', () => {
    setupComponentWithData(mockDialogDataIncome);
    const spyNext = jest.spyOn(component['destroy$'], 'next');
    const spyComplete = jest.spyOn(component['destroy$'], 'complete');
    component.ngOnDestroy();
    expect(spyNext).toHaveBeenCalled();
    expect(spyComplete).toHaveBeenCalled();
  });
});
