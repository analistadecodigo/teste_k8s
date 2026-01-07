import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class JwtService {

  constructor() { }

  decodeToken(token: string): string {
    return jwtDecode(token);
  }

  getTokenFromCookie(): string {
    const cookieName = "jwt";
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
      const [name, value] = cookie.trim().split('=');
      if (name === cookieName) {
        return decodeURIComponent(value);
      }
    }
    return '';
  }

  getEmailFromToken(): string {
    const token = this.getTokenFromCookie();

    const decodedToken: any = this.decodeToken(token);
    
    return token !== '' ? decodedToken.sub : '';
  }

  createHeaders(): { Authorization: string } {
    const token = this.getTokenFromCookie();

    return { Authorization: `Bearer ${token}` };
  }

  getEmailAndHeaders(): { email: string, headers: { Authorization: string } } {
    const email = this.getEmailFromToken();

    const headers = this.createHeaders();

    return { email, headers };
  }

  removeTokenFromCookie(): void {
    document.cookie = "jwt=;";
  }
}
