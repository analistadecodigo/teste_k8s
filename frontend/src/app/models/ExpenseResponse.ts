import { ExpenseCategory } from './enums/ExpenseCategory';

export interface ExpenseResponse {
  id: number;
  name: string;
  description: string;
  amount: number;
  date: string;  
  category: ExpenseCategory;
  userId: number;
}
