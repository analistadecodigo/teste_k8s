import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtService } from '../jwt-service/jwt.service';
import { Observable } from 'rxjs';
import { FinancialSummary } from '../../models/FinancialSummary';

@Injectable({
  providedIn: 'root'
})
export class FinancialService {
  private readonly baseUrl = 'http://localhost:8080/api/financial-summary';

  constructor(private readonly http: HttpClient,
    private readonly jwtService: JwtService
  ) { }

  getFinancialSummary(): Observable<FinancialSummary> {
    const { email, headers } = this.jwtService.getEmailAndHeaders();

    return this.http.get<FinancialSummary>(`${this.baseUrl}/${email}`, {
      headers: headers
    });
  }
}
