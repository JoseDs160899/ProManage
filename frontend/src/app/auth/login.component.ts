import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  enviando = false;
  error = '';

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.enviando = true;
    this.error = '';
    const payload = this.form.getRawValue() as { username: string; password: string };

    this.authService.login(payload).subscribe({
      next: () => this.router.navigate(['/projects']),
      error: (err) => {
        this.error = err.status === 401
          ? 'Usuario o contraseña incorrectos.'
          : 'No se pudo conectar con el servidor (puerto 8081).';
        this.enviando = false;
      }
    });
  }
}
