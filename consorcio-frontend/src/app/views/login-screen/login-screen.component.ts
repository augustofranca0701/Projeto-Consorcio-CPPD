// src/app/views/login-screen/login-screen.component.ts
import {ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { VisibilityService } from '../../services/visibility.service';
import { Validators, FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {merge} from 'rxjs';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { User } from '../../../models/User/user.model';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { HttpResponse } from '@angular/common/http';

@Component({
    selector: 'app-login-screen',
    standalone: true,
    templateUrl: './login-screen.component.html',
    styleUrl: './login-screen.component.css',
    imports: [MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, FormsModule, ReactiveFormsModule, RouterOutlet, RouterLink, RouterLinkActive, CommonModule]
})
export class LoginScreenComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  hide = true;
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required]);
  errorMessage = '';

  constructor(private visibilityService: VisibilityService, private apiService: ApiService,
    private snackBar: MatSnackBar, private userService: UserService, private router: Router) {
    this.visibilityService.setShowComponent(false);
    merge(this.email.statusChanges, this.email.valueChanges)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.updateErrorMessage());
  }

  ngOnInit(): void {}

  fazerLogin() {
    const body = { email: this.email.value, password: this.password.value };

    this.apiService.postLogin(body).subscribe({
      next: (resp: HttpResponse<any>) => {
        const respBody = resp.body;

        // 1) token no body (ex.: { token: '...', user: { ... } })
        if (respBody && respBody.token) {
          const token = respBody.token as string;
          const user = respBody.user as User | undefined;
          if (user) {
            this.userService.setUser(user, token);
            this.router.navigate(['/']);
            return;
          }
          // se não veio user, salva token e tenta /me
          localStorage.setItem('token', token);
          this.apiService.getMe().subscribe({
            next: (me: User) => {
              this.userService.setUser(me, token);
              this.router.navigate(['/']);
            },
            error: () => {
              this.snackBar.open('Erro ao validar token.', 'Fechar', {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000
              });
            }
          });
          return;
        }

        // 2) token no header (Authorization: Bearer ...)
        const authHeader = resp.headers?.get('Authorization') || resp.headers?.get('authorization');
        if (authHeader) {
          const token = authHeader.replace('Bearer ', '');
          // tentar usar body como user
          if (respBody && respBody.user) {
            this.userService.setUser(respBody.user as User, token);
            this.router.navigate(['/']);
            return;
          }
          localStorage.setItem('token', token);
          this.apiService.getMe().subscribe({
            next: (me: User) => {
              this.userService.setUser(me, token);
              this.router.navigate(['/']);
            },
            error: () => {
              this.snackBar.open('Erro ao validar token.', 'Fechar', {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000
              });
            }
          });
          return;
        }

        // 3) caso cookie HttpOnly (nenhum token exposto): backend pode ter setado cookie e retornado user no body
        if (respBody && respBody.user) {
          this.userService.setUser(respBody.user as User);
          this.router.navigate(['/']);
          return;
        }

        // 4) último recurso: se o body for diretamente o user (como antes)
        if (respBody && respBody.email) {
          this.userService.setUser(respBody as User);
          this.router.navigate(['/']);
          return;
        }

        // fallback: resposta inesperada
        console.warn('Resposta de login inesperada:', resp);
        this.snackBar.open('Erro no login. Resposta inesperada.', 'Fechar', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Erro ao realizar login.', error);
        this.snackBar.open('Dados incorretos!', 'Fechar', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000
        });
      }
    });
  }

  updateErrorMessage() {
    if (this.email.hasError('required')) {
      this.errorMessage = 'Preencha este campo!';
    } else if (this.email.hasError('email')) {
      this.errorMessage = 'Insira um e-mail válido';
    } else {
      this.errorMessage = '';
    }
  }
}
