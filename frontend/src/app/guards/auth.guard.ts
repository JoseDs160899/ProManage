import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Impide el acceso a las rutas protegidas si no hay sesión activa. */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.autenticado) {
    return true;
  }
  router.navigate(['/login']);
  return false;
};
