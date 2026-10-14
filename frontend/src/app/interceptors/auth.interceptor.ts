import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Agrega el encabezado Authorization: Bearer <token> a las peticiones hacia la API
 * (excepto el login) y, ante un 401/403, limpia la sesión y redirige al login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.token;

  const esLogin = req.url.includes('/api/auth/login');
  const peticion = token && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      // Solo 401 = sesión inválida. 403 = autenticado pero sin permiso (no cerrar sesión).
      if (error.status === 401 && !esLogin) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
