import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly apiUrl = 'http://localhost:8081/api/usuarios';

  constructor(private http: HttpClient) {}

  listarOperadores(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}/operadores`);
  }
}
