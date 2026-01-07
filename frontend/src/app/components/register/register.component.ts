import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { AuthService } from '../../services/auth-service/auth.service';
import { RegisterResponse } from '../../models/RegisterResponse';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit, OnDestroy{
  private readonly destroy$ = new Subject<void>();
  public registerForm!: FormGroup;
  public errorMessage: string | null = null;

  constructor(private readonly authService: AuthService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router 
  ) { }
  
  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['',[Validators.required, Validators.minLength(8)]],
    });
  }

  register(): void {
    const { username, email, password } = this.registerForm.value;

    this.authService.register(username, email, password)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: RegisterResponse) => {
          console.log('Registration successful', response);
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Registration failed', error);
          this.errorMessage = error.message;
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
