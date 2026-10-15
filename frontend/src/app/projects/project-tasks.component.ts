import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Proyecto } from '../models/proyecto.model';
import { Tarea } from '../models/tarea.model';
import { ProyectoService } from '../services/proyecto.service';
import { TareaService } from '../services/tarea.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-project-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './project-tasks.component.html',
  styleUrl: './project-tasks.component.css'
})
export class ProjectTasksComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly proyectoService = inject(ProyectoService);
  private readonly tareaService = inject(TareaService);
  private readonly authService = inject(AuthService);

  proyecto?: Proyecto;
  tareas: Tarea[] = [];
  proyectoId = 0;
  cargando = true;
  error = '';
  nuevaTarea = '';
  enviando = false;
  soloPendientes = false;

  get esAdmin(): boolean {
    return this.authService.esAdmin;
  }

  get esOperador(): boolean {
    return this.authService.rol === 'OPERADOR';
  }

  get total(): number {
    return this.tareas.length;
  }

  get completadas(): number {
    return this.tareas.filter((t) => t.completada).length;
  }

  ngOnInit(): void {
    this.proyectoId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarProyecto();
    this.cargarTareas();
  }

  /** ADMIN siempre puede cambiar; OPERADOR solo puede marcar pendientes (no desmarcar). */
  puedeInteractuar(tarea: Tarea): boolean {
    if (this.esAdmin) {
      return true;
    }
    return this.esOperador && !tarea.completada;
  }

  cargarProyecto(): void {
    this.proyectoService.obtener(this.proyectoId).subscribe({
      next: (p) => (this.proyecto = p),
      error: () => (this.error = 'No se pudo cargar el proyecto.')
    });
  }

  cargarTareas(): void {
    this.cargando = true;
    this.error = '';
    const req$ = this.soloPendientes
      ? this.tareaService.listarPendientes(this.proyectoId)
      : this.tareaService.listarPorProyecto(this.proyectoId);

    req$.subscribe({
      next: (data) => {
        this.tareas = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las tareas.';
        this.cargando = false;
      }
    });
  }

  cambiarFiltro(soloPendientes: boolean): void {
    this.soloPendientes = soloPendientes;
    this.cargarTareas();
  }

  agregar(): void {
    const titulo = this.nuevaTarea.trim();
    if (!titulo || !this.esAdmin) {
      return;
    }
    this.enviando = true;
    this.tareaService.crear(this.proyectoId, titulo).subscribe({
      next: () => {
        this.nuevaTarea = '';
        this.enviando = false;
        this.cargarTareas();
        this.cargarProyecto();
      },
      error: () => {
        this.error = 'No se pudo crear la tarea.';
        this.enviando = false;
      }
    });
  }

  toggleCompletada(tarea: Tarea): void {
    if (!tarea.id || !this.puedeInteractuar(tarea)) {
      return;
    }

    const nuevoValor = !tarea.completada;
    // OPERADOR solo completa; no desmarca
    if (this.esOperador && !nuevoValor) {
      return;
    }

    this.tareaService.completar(tarea.id, nuevoValor).subscribe({
      next: (actualizada) => {
        tarea.completada = actualizada.completada;
        this.cargarProyecto();
        if (this.soloPendientes) {
          this.cargarTareas();
        }
      },
      error: (err) => {
        this.error = err.status === 403
          ? 'No tienes permiso para desmarcar esta tarea.'
          : 'No se pudo actualizar la tarea.';
      }
    });
  }

  eliminar(tarea: Tarea): void {
    if (!this.esAdmin || !tarea.id) {
      return;
    }
    if (!confirm(`¿Eliminar la tarea "${tarea.titulo}"?`)) {
      return;
    }
    this.tareaService.eliminar(tarea.id).subscribe({
      next: () => {
        this.cargarTareas();
        this.cargarProyecto();
      },
      error: () => (this.error = 'No se pudo eliminar la tarea.')
    });
  }

  etiquetaEstado(estado?: string): string {
    switch (estado) {
      case 'EN_PROGRESO':
        return 'En progreso';
      case 'COMPLETADA':
        return 'Completada';
      default:
        return 'Pendiente';
    }
  }

  estadoBadge(estado?: string): string {
    switch (estado) {
      case 'EN_PROGRESO':
        return 'text-bg-primary';
      case 'COMPLETADA':
        return 'text-bg-success';
      default:
        return 'text-bg-secondary';
    }
  }
}
