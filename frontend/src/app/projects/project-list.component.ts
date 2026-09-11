import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Proyecto } from '../models/proyecto.model';
import { ProyectoService } from '../services/proyecto.service';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent implements OnInit {
  private readonly proyectoService = inject(ProyectoService);

  proyectos: Proyecto[] = [];
  cargando = true;
  error = '';

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';
    this.proyectoService.listar().subscribe({
      next: (data: Proyecto[]) => {
        this.proyectos = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo cargar la lista. Verifica que el backend este en el puerto 8081.';
        this.cargando = false;
      }
    });
  }

  get total(): number {
    return this.proyectos.length;
  }

  get enProgreso(): number {
    return this.proyectos.filter((p) => p.estado === 'EN_PROGRESO').length;
  }

  get completados(): number {
    return this.proyectos.filter((p) => p.estado === 'COMPLETADA').length;
  }

  get pendientes(): number {
    return this.proyectos.filter((p) => p.estado === 'PENDIENTE').length;
  }

  prioridadBadge(prioridad: string): string {
    switch (prioridad) {
      case 'ALTA':
        return 'text-bg-danger';
      case 'MEDIA':
        return 'text-bg-warning';
      default:
        return 'text-bg-success';
    }
  }

  estadoBadge(estado: string): string {
    switch (estado) {
      case 'EN_PROGRESO':
        return 'text-bg-primary';
      case 'COMPLETADA':
        return 'text-bg-success';
      default:
        return 'text-bg-secondary';
    }
  }

  etiquetaPrioridad(prioridad: string): string {
    switch (prioridad) {
      case 'ALTA':
        return 'Alta';
      case 'MEDIA':
        return 'Media';
      default:
        return 'Baja';
    }
  }

  etiquetaEstado(estado: string): string {
    switch (estado) {
      case 'EN_PROGRESO':
        return 'En progreso';
      case 'COMPLETADA':
        return 'Completada';
      default:
        return 'Pendiente';
    }
  }
}
