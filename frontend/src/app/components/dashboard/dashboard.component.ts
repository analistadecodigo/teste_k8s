import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { FinancialService } from '../../services/financial-service/financial.service';
import { FinancialSummary } from '../../models/FinancialSummary';
import { AuthService } from '../../services/auth-service/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();
  public financialSummary!: FinancialSummary;

  constructor(
    private readonly router: Router,
    private readonly financialService: FinancialService,
    private readonly authService: AuthService
  ) { }

  ngOnInit(): void {
    this.getFinancialSummary();
  }

  getFinancialSummary() {
    this.financialService.getFinancialSummary()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (summary: FinancialSummary) => {
          this.financialSummary = {
            name: summary.name,
            totalIncome: summary.totalIncome,
            totalExpenses: summary.totalExpenses,
            balance: summary.balance
          };
          console.log('Financial Summary:', this.financialSummary);
        },
        error: (error) => {
          console.error('Error fetching financial summary:', error);
        }
      });
  }

  gerenciarRenda(): void {
    console.log('Navigating to Gerenciar Renda');
    this.router.navigate(['/gerenciar-renda']);
  }

  gerenciarDespesa(): void {
    this.router.navigate(['/gerenciar-despesa']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
