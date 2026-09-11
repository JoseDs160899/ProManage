import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Proyecto } from '../models/proyecto.model';
import { ProyectoService } from '../services/proyecto.service';

@Component({
  selector: 'app-project-delete',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './project-delete.component.html',
  styleUrl: './project-delete.component.css'
})
export class ProjectDeleteComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly proyectoService = inject(ProyectoService);

  proyecto?: Proyecto;
  error = '';
  eliminando = false;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.proyectoService.obtener(id).subscribe({
      next: (p: Proyecto) => (this.proyecto = p),
      error: () => (this.error = 'Proyecto no encontrado.')
    });
  }

  confirmar(): void {
    if (!this.proyecto?.id) {
      return;
    }
    this.eliminando = true;
    this.proyectoService.eliminar(this.proyecto.id).subscribe({
      next: () => this.router.navigate(['/projects']),
      error: () => {
        this.error = 'No se pudo eliminar el proyecto.';
        this.eliminando = false;
      }
    });
  }
}
