import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse } from '../../models/AuthResponse';
import { RegisterResponse } from '../../models/RegisterResponse';
import { JwtService } from '../jwt-service/jwt.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl: string = 'http://localhost:8080/api/auth';

  constructor(private readonly http: HttpClient, private readonly jwtService: JwtService) { }

  login(email: string, password: string): Observable<AuthResponse>{
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { email, password });
  }

  register(name: string, email: string, password: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/register`, { name, email, password });
  }

  logout(): void {
    this.jwtService.removeTokenFromCookie();
  }
}
