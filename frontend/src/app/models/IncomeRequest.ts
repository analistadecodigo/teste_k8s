import { IncomeCategory } from "./enums/IncomeCategory";

export interface IncomeRequest {
  name: string;
  description?: string;
  amount: number;
  date: string;
  category: IncomeCategory;
}