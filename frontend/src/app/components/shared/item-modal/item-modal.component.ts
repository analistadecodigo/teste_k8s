import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { IncomeService } from '../../../services/income-service/income.service';
import { ExpenseService } from '../../../services/expense-service/expense.service';
import { IncomeRequest } from '../../../models/IncomeRequest';
import { ExpenseRequest } from '../../../models/ExpenseRequest';
import { IncomeResponse } from '../../../models/IncomeResponse';
import { ExpenseResponse } from '../../../models/ExpenseResponse';
import { IncomeCategory } from '../../../models/enums/IncomeCategory';
import { ExpenseCategory } from '../../../models/enums/ExpenseCategory';
import { ListItem } from '../../../models/ListItem';
import { ItemType } from '../../../models/types/ItemType';
import { ItemRequest } from '../../../models/types/ItemRequest';

@Component({
  selector: 'app-item-modal',
  templateUrl: './item-modal.component.html',
  styleUrls: ['./item-modal.component.css']
})
export class ItemModalComponent implements OnInit, OnDestroy {
  itemForm!: FormGroup;
  isEditMode = false;
  private readonly destroy$ = new Subject<void>();
  categories: string[] = [];
  itemType!: ItemType;

  constructor(
    private readonly fb: FormBuilder,
    private readonly incomeService: IncomeService,
    private readonly expenseService: ExpenseService,
    private readonly dialogRef: MatDialogRef<ItemModalComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: { item: ListItem | null, itemType: ItemType }
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.itemType = this.data.itemType;
    if (this.data.item) {
      this.isEditMode = true;
      this.patchFormValues();
    }
    this.loadCategories();
  }

  private initializeForm(): void {
    this.itemForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      description: [''],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      category: ['', Validators.required],
      date: ['', Validators.required]
    });
  }

  private loadCategories(): void {
    if (this.itemType === 'income') {
      this.categories = Object.values(IncomeCategory);
    } else {
      this.categories = Object.values(ExpenseCategory);
    }
  }

  private patchFormValues(): void {
    const formattedDate = new Date(this.data.item!.date);
    this.itemForm.patchValue({
      id: this.data.item!.id || null,
      name: this.data.item!.name,
      description: this.data.item!.description,
      amount: this.data.item!.amount,
      category: this.data.item!.category,
      date: formattedDate
    });
  }

  updateDate(event: MatDatepickerInputEvent<Date>) {
    this.itemForm.patchValue({
      date: event.value
    });
  }

  save(): void {
    const item: ItemRequest = {
      name: this.itemForm.value.name,
      description: this.itemForm.value.description,
      amount: this.itemForm.value.amount,
      category: this.itemForm.value.category,
      date: this.itemForm.value.date.toISOString()
    };

    if (this.isEditMode && this.data.item) {
      if (this.itemType === 'income') {
        this.updateIncome(item as IncomeRequest);
      }
      this.updateExpense(item as ExpenseRequest);
    } else {
      if (this.itemType === 'income') {
        this.createIncome(item as IncomeRequest);
      } else { 
        this.createExpense(item as ExpenseRequest);
      }
    }
  }

  private updateIncome(item: IncomeRequest): void {
    const updatedItem: any = {
      ...item,
      id: this.data.item!.id
    };
    this.incomeService.updateIncome(this.itemForm.get('id')?.value, updatedItem)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          console.log('Income updated successfully');
          this.getAllIncomes();
          this.dialogRef.close(item);
        },
        error: (error) => {
          console.log('id: ', this.itemForm.value.id);
        }
      });
  }

  private updateExpense(item: ExpenseRequest): void {
    const updatedItem: any = {
      ...item,
      id: this.data.item!.id
    };
    this.expenseService.updateExpense(this.itemForm.get('id')?.value, updatedItem)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.dialogRef.close(item);
          console.log('Expense updated successfully');
          this.getAllExpenses();
        },
        error: (error: any) => {
          console.error('Error updating expense:', error);
        }
      });
  }

  private createIncome(item: IncomeRequest): void {
    this.incomeService.createIncome(item)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.dialogRef.close(item);
          this.getAllIncomes();
          console.log('Income created successfully');
        },
        error: (error: any) => {
          console.error('Error creating income:', error);
        }
      });
  }

  private createExpense(item: ExpenseRequest): void {
    this.expenseService.createExpense(item)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.dialogRef.close(item);
          this.getAllExpenses();
        },
        error: (error: any) => {
          console.error('Error creating expense:', error);
        }
      });
  }

  private getAllIncomes(): void {
    this.incomeService.getAllIncomes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (incomes: IncomeResponse[]) => {
          console.log('Incomes fetched successfully:', incomes);
        },
        error: (error) => {
          console.error('Error fetching incomes:', error);
        }
      });
  }

  private getAllExpenses(): void {
    this.expenseService.getAllExpenses()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (expenses: ExpenseResponse[]) => {
          console.log('Expenses fetched successfully:', expenses);
        },
        error: (error) => {
          console.error('Error fetching expenses:', error);
        }
      });
  }

  close(): void {
    this.dialogRef.close(false);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
