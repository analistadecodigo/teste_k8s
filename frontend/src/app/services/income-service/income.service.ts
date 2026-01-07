import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IncomeResponse } from '../../models/IncomeResponse';
import { Observable } from 'rxjs';
import { JwtService } from '../jwt-service/jwt.service';
import { IncomeRequest } from '../../models/IncomeRequest';

@Injectable({
  providedIn: 'root'
})
export class IncomeService {
  private readonly baseUrl: string = 'http://localhost:8080/api/incomes';

  constructor(private readonly http: HttpClient,
    private readonly jwtService: JwtService
  ) { }

  getAllIncomes(): Observable<IncomeResponse[]> {
    const { email, headers } = this.jwtService.getEmailAndHeaders();
    
    return this.http.get<IncomeResponse[]>(`${this.baseUrl}/${email}`, { headers });
  }

  createIncome(income: IncomeRequest): Observable<IncomeResponse> {
    const { email, headers } = this.jwtService.getEmailAndHeaders();  
    
    return this.http.post<IncomeResponse>(`${this.baseUrl}/${email}`, income, { headers });
  }

  updateIncome(idRenda: number, updatedIncome: IncomeRequest): Observable<IncomeResponse> {
    const { email, headers } = this.jwtService.getEmailAndHeaders();

    return this.http.put<IncomeResponse>(`${this.baseUrl}/${email}/${idRenda}`, updatedIncome, { headers });
  }

  deleteIncome(idRenda: number): Observable<void> {
    const { email, headers } = this.jwtService.getEmailAndHeaders();

    return this.http.delete<void>(`${this.baseUrl}/${email}/${idRenda}`, { headers });
  }

}