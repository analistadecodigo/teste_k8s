import { Component, OnDestroy, OnInit } from '@angular/core';
import { ExpenseResponse } from '../../models/ExpenseResponse';
import { Subject, takeUntil } from 'rxjs';
import { ExpenseService } from '../../services/expense-service/expense.service';
import { ListItem } from '../../models/ListItem';
import { ItemAction } from '../../models/enums/ItemAction';
import { MatDialog } from '@angular/material/dialog';
import { ItemModalComponent } from '../shared/item-modal/item-modal.component';

@Component({
  selector: 'app-despesa-dashboard',
  templateUrl: './despesa-dashboard.component.html',
  styleUrl: './despesa-dashboard.component.css'
})
export class DespesaDashboardComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();
  public despesas!: ListItem[];

  constructor(
    private readonly expenseService: ExpenseService,
    private readonly dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.getExpenses();
  }

  getExpenses(): void {
    this.expenseService.getAllExpenses()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (expenses: ExpenseResponse[]) => {
          this.despesas = expenses.map(expense => ({
            id: expense.id,
            name: expense.name,
            description: expense.description,
            amount: expense.amount,
            date: expense.date,
            userId: expense.userId,
            category: expense.category
          }));
        },
        error: (error) => {
          console.error('Error fetching expenses:', error);
        }
      });
  }

  handleExpenseAction(event: { item: ListItem, action: ItemAction }): void {
    switch (event.action) {
      case ItemAction.Create:
        this.getExpenses();
        break;
      case ItemAction.Update:
        this.getExpenses();
        break;
      case ItemAction.Delete:
        this.deleteExpense(event.item.id);
        break;
      
    }
  }

  openExpenseModal(item: ListItem | null): void {
    const dialogRef = this.dialog.open(ItemModalComponent, {
      width: '500px',
      data: item
    });

    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      if (result) {
        this.getExpenses();
      }
    });
  }

  deleteExpense(id: number): void {
    this.expenseService.deleteExpense(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          console.log('Expense deleted successfully');
          this.getExpenses();
        },
        error: (error) => {
          console.error('Error deleting expense:', error);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
