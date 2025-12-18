// src/app/services/user.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

export interface User {
  id?: number;
  name: string;
  email: string;
  password?: string;
  cpf?: string;
  phone?: string;
  address?: string;
  complement?: string;
  state?: string;
  city?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userSubject = new BehaviorSubject<User | null>(null);
  public user$: Observable<User | null> = this.userSubject.asObservable();

  constructor(private router: Router) {
    this.loadFromStorage();
  }

  /**
   * Define o usuário em memória e opcionalmente persiste o token no localStorage.
   * Aceita Partial<User> para casos em que você só tem campos parciais vindos do token.
   */
  setUser(user: User | Partial<User> | null, token?: string) {
    if (!user) {
      this.clearUser();
      return;
    }

    // normaliza para User parcial
    const normalized: User = {
      id: (user as any).id,
      name: (user as any).name || '',
      email: (user as any).email || '',
      password: (user as any).password || '',
      cpf: (user as any).cpf || '',
      phone: (user as any).phone || '',
      address: (user as any).address || '',
      complement: (user as any).complement || '',
      state: (user as any).state || '',
      city: (user as any).city || ''
    };

    this.userSubject.next(normalized);

    // salva token se fornecido
    if (token) {
      try {
        localStorage.setItem('token', token);
      } catch (e) {
        console.warn('Falha ao salvar token no storage', e);
      }
    }

    // persiste apenas campos mínimos do usuário
    try {
      localStorage.setItem('user', JSON.stringify({
        id: normalized.id,
        name: normalized.name,
        email: normalized.email
      }));
    } catch (e) {
      console.warn('Falha ao salvar user no storage:', e);
    }
  }

  getUser(): User | null {
    return this.userSubject.getValue();
  }

  /**
   * Retorna true se o usuário estiver logado (token ou user em storage).
   */
  isLogged(): boolean {
    return !!localStorage.getItem('token') || !!this.getUser();
  }

  logout() {
    try { localStorage.removeItem('token'); } catch {}
    try { localStorage.removeItem('user'); } catch {}
    this.userSubject.next(null);
    // redireciona para login por padrão
    this.router.navigate(['/login']);
  }

  clearUser() {
    try { localStorage.removeItem('token'); } catch {}
    try { localStorage.removeItem('user'); } catch {}
    this.userSubject.next(null);
  }

  // --- HELPERS ÚTEIS ---

  /**
   * Retorna userId (number) ou null
   */
  getUserId(): number | null {
    const u = this.getUser();
    return u && typeof u.id === 'number' ? u.id : null;
  }

  /**
   * Garante que haja userId; se não houver, limpa token/user e redireciona para /login.
   * Útil para chamadas nos components antes de usar apiService que exigem userId.
   */
  requireUserIdOrRedirect(): number | null {
    const id = this.getUserId();
    if (!id) {
      // limpa por precaução (evita estado inconsistente)
      this.clearUser();
      this.router.navigate(['/login']);
      return null;
    }
    return id;
  }

  // --- restauração / decodificação JWT ---

  private loadFromStorage() {
    // 1) tenta restaurar user salvo
    const rawUser = localStorage.getItem('user');
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser) as User;
        // só aplica se tiver ao menos email ou name
        if (parsed && (parsed.email || parsed.name)) {
          this.userSubject.next(parsed);
          return;
        }
      } catch {
        // continua para tentar token
      }
    }

    // 2) tenta decodificar token, se houver
    const token = localStorage.getItem('token');
    if (token) {
      const decoded = this.decodeJwtPayload(token);
      if (decoded) {
        const userFromToken: Partial<User> = {
          id: decoded.sub || decoded.id,
          name: decoded.name || decoded.nome || decoded.preferred_username || '',
          email: decoded.email || decoded.user_email || ''
        };
        // se houver algo útil, popula o subject
        if (userFromToken.email || userFromToken.name || userFromToken.id) {
          this.userSubject.next(userFromToken as User);
          return;
        }
      }
    }

    // fallback: sem user
    this.userSubject.next(null);
  }

  private decodeJwtPayload(token: string): any | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = parts[1];
      // base64url -> base64
      const b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(b64).split('').map((c) => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (e) {
      console.warn('decodeJwtPayload falhou:', e);
      return null;
    }
  }
}
