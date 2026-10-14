import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarea } from '../models/tarea.model';

@Injectable({ providedIn: 'root' })
export class TareaService {
  private readonly apiUrl = 'http://localhost:8081/api/tareas';

  constructor(private http: HttpClient) {}

  listarPorProyecto(proyectoId: number): Observable<Tarea[]> {
    return this.http.get<Tarea[]>(`${this.apiUrl}/proyecto/${proyectoId}`);
  }

  listarPendientes(proyectoId: number): Observable<Tarea[]> {
    return this.http.get<Tarea[]>(`${this.apiUrl}/proyecto/${proyectoId}/pendientes`);
  }

  crear(proyectoId: number, titulo: string): Observable<Tarea> {
    return this.http.post<Tarea>(`${this.apiUrl}/proyecto/${proyectoId}`, {
      titulo,
      completada: false
    });
  }

  completar(id: number, valor = true): Observable<Tarea> {
    return this.http.patch<Tarea>(`${this.apiUrl}/${id}/completar?valor=${valor}`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
