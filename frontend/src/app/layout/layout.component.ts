import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  get username(): string {
    return this.authService.username || 'Usuario';
  }

  get rol(): string {
    return this.authService.rol;
  }

  get iniciales(): string {
    return (this.authService.username || 'U').substring(0, 2).toUpperCase();
  }

  salir(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
