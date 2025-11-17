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


  fazerLogin()
{
  const email = this.email;
  const password = this.password;

  const body = { email: email.value, password: password.value };

  this.apiService.postLogin(body).subscribe(
    (response: User) => {
      this.userService.setUser(response);
      this.router.navigate(['/']);
    },
    error => {
      console.error('Erro ao realizar login.');
      this.snackBar.open('Dados incorretos!', 'Fechar', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000
      });
    }
  );
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

  ngOnInit(): void {

  }

}
