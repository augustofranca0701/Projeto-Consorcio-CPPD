import { Component, OnInit } from '@angular/core';
import { VisibilityService } from '../../../services/visibility.service';
import { Validators, FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {merge} from 'rxjs';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { HeaderMyGroupsComponent } from "../../../components/header-my-groups/header-my-groups.component";
import { HeaderAccountProfileComponent } from "../../../components/header-account-profile/header-account-profile.component";
import { UpdateLogin } from '../../../../models/User/update-login.model';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarModule, MatSnackBarVerticalPosition} from '@angular/material/snack-bar';
import { ApiService } from '../../../services/api.service';
import { User } from '../../../../models/User/user.model';
import { FormActionService } from '../../../services/formAction.service';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/user.service';

@Component({
    selector: 'app-edit-account',
    standalone: true,
    templateUrl: './edit-account.component.html',
    styleUrl: './edit-account.component.css',
    imports: [MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, FormsModule, ReactiveFormsModule, RouterOutlet, RouterLink, RouterLinkActive, HeaderMyGroupsComponent, HeaderAccountProfileComponent, MatSnackBarModule, CommonModule]
})
export class EditAccountComponent implements OnInit{
  hide = true;
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  email = new FormControl('', [Validators.required, Validators.email]);
  errorMessage = '';
  password = new FormControl('', [Validators.required]);

  dados: User = {
    id: 2,
    name: '',
    email: '',
    password: '',
    cpf: '',
    phone: '',
    address: '',
    complement: '',
    state: '',
    city: '',
  }

  constructor(
    private formActionService: FormActionService,
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    private visibilityService: VisibilityService,private userService: UserService,private router: Router
  ) {
    this.visibilityService.setShowComponent(true);
    merge(this.email.statusChanges, this.email.valueChanges)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.updateErrorMessage());
      this.obterDadosUsuario();
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
  this.formActionService.formAction$.subscribe(() => {
    this.updateLogin();
  });
}

obterDadosUsuario() {
  // Obtém o usuário logado
  let user = this.userService.getUser();

  // Verifica se tem usuário logado
  if (user === undefined){this.router.navigate(['/login']);}

  this.apiService.getUser(user.id!).subscribe(dados => {
    this.dados = dados;
    this.email.setValue(this.dados.email);
    this.password.setValue(this.dados.password);
  });
}

updateLogin() {
  const emailValue = this.email.value;
  const passwordValue = this.password.value;
  if (!emailValue || !passwordValue) {
    return;
  }

  // Obtém o usuário logado
  let user = this.userService.getUser();

  // Verifica se tem usuário logado
  if (user === undefined){this.router.navigate(['/login']);}

  this.apiService.updateLogin({
    email: emailValue,
    password: passwordValue
  }, user.id!)

  .subscribe(
    () => {
      this.obterDadosUsuario();
      this.snackBar.open('Perfil atualizado com sucesso!', 'Fechar', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000
      });
    },
    error => {
      this.snackBar.open('Erro ao atualizar perfil.', 'Fechar', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000
      });
    }
  );
}

setDados(dado: UpdateLogin) {
  this.email.setValue (dado.email);
  this.password.setValue(dado.password);
}
}

