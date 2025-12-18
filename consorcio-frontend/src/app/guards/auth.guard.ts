// src/app/guards/auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from '../services/user.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.userService.user$.pipe(
      map(user => {
        if (user) return true;
        // se não há user em memória, tenta ver se existe token no storage
        const token = localStorage.getItem('token');
        if (token) {
          return true;
        }
        return this.router.parseUrl('/login');
      })
    );
  }
}
