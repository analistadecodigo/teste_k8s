import { IncomeCategory } from "./enums/IncomeCategory";

export interface IncomeResponse {
  id: number;
  name: string;
  description: string;
  amount: number;
  date: string; 
  userId: number;
  category: IncomeCategory;
}