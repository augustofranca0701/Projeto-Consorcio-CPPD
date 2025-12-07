// src/app/interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

const PUBLIC_URLS = [
  '/auth/register',
  '/auth/login',
  '/health',
  '/ping'
];

function isPublicUrl(url: string | null): boolean {
  if (!url) return false;
  return PUBLIC_URLS.some(u => url.includes(u));
}

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Se a requisição for para rota pública, não tocar
    if (isPublicUrl(req.url)) {
      return next.handle(req).pipe(
        catchError((err: HttpErrorResponse) => {
          // Log minimal para depuração
          if (err.status === 401) {
            console.warn('401 em rota pública:', req.url, err.error ?? err.message);
          }
          return throwError(() => err);
        })
      );
    }

    // Caso contrário, só acrescenta Authorization se houver token
    const token = localStorage.getItem('token');
    const authReq = token ? req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    }) : req;

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        // loga e repropaga — tratamento global continua
        if (err.status === 401) {
          console.warn('401 interceptado (recurso protegido):', req.url);
        }
        return throwError(() => err);
      })
    );
  }
}
