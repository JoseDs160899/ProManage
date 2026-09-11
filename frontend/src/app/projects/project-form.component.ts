import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Proyecto } from '../models/proyecto.model';
import { ProyectoService } from '../services/proyecto.service';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.css'
})
export class ProjectFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly proyectoService = inject(ProyectoService);

  editing = false;
  proyectoId?: number;
  enviando = false;
  error = '';

  form = this.fb.group({
    titulo: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: [''],
    fechaVencimiento: ['', Validators.required],
    prioridad: ['MEDIA', Validators.required],
    estado: ['PENDIENTE', Validators.required]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editing = true;
      this.proyectoId = Number(idParam);
      this.proyectoService.obtener(this.proyectoId).subscribe({
        next: (p: Proyecto) => {
          this.form.patchValue({
            titulo: p.titulo,
            descripcion: p.descripcion,
            fechaVencimiento: p.fechaVencimiento,
            prioridad: p.prioridad,
            estado: p.estado
          });
        },
        error: () => {
          this.error = 'No se pudo cargar el proyecto.';
        }
      });
    }
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.enviando = true;
    this.error = '';
    const payload = this.form.getRawValue() as {
      titulo: string;
      descripcion: string;
      fechaVencimiento: string;
      prioridad: string;
      estado: string;
    };

    const request$ = this.editing && this.proyectoId
      ? this.proyectoService.actualizar(this.proyectoId, payload)
      : this.proyectoService.crear(payload);

    request$.subscribe({
      next: () => this.router.navigate(['/projects']),
      error: () => {
        this.error = 'No se pudo guardar el proyecto.';
        this.enviando = false;
      }
    });
  }
}
