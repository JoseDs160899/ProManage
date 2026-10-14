import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = 'http://localhost:8081/api/auth';
  private readonly TOKEN_KEY = 'promanage_token';
  private readonly USER_KEY = 'promanage_user';
  private readonly ROL_KEY = 'promanage_rol';

  constructor(private http: HttpClient) {}

  login(credenciales: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credenciales).pipe(
      tap((resp) => {
        localStorage.setItem(this.TOKEN_KEY, resp.token);
        localStorage.setItem(this.USER_KEY, resp.username);
        localStorage.setItem(this.ROL_KEY, resp.rol);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.ROL_KEY);
  }

  get token(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  get username(): string {
    return localStorage.getItem(this.USER_KEY) ?? '';
  }

  get rol(): string {
    return localStorage.getItem(this.ROL_KEY) ?? '';
  }

  get autenticado(): boolean {
    return !!this.token;
  }

  get esAdmin(): boolean {
    return this.rol === 'ADMIN';
  }
}
