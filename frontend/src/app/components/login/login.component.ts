import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth-service/auth.service';
import { AuthResponse } from '../../models/AuthResponse';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'] 
})
export class LoginComponent implements OnInit, OnDestroy{
  private readonly destroy$ = new Subject<void>();
  public loginForm!: FormGroup; 
  public errorMessage: string | null = null;

  constructor(private readonly authService: AuthService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  login(): void {
    const { email, password } = this.loginForm.value;

    this.authService.login(email, password)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (response: AuthResponse) => {
        console.log('Login successful')
        document.cookie = `jwt=${response.tokenJWT}; path=/; secure`;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Login failed:', error);
        this.errorMessage = error.message;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
