import { Component, OnInit } from '@angular/core';
import { HeaderAccountProfileComponent } from "../../../components/header-account-profile/header-account-profile.component";
import { MatFormFieldModule } from '@angular/material/form-field';
import { HeaderMyGroupsComponent } from "../../../components/header-my-groups/header-my-groups.component";
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { User } from '../../../../models/User/user.model';
import { ApiService } from '../../../services/api.service';
import { CommonModule } from '@angular/common';
import { Validators, FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { FormActionService } from "../../../services/formAction.service";
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarModule, MatSnackBarVerticalPosition} from '@angular/material/snack-bar';
import { UserService } from '../../../services/user.service';
@Component({
    selector: 'app-edit-profile',
    standalone: true,
    templateUrl: './edit-profile.component.html',
    styleUrl: './edit-profile.component.css',
    imports: [MatFormFieldModule, HeaderMyGroupsComponent, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, RouterOutlet, RouterLink, RouterLinkActive, HeaderMyGroupsComponent, CommonModule,  HeaderAccountProfileComponent, MatSnackBarModule, FormsModule, ReactiveFormsModule,]
})
export class EditProfileComponent implements OnInit{

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  //form
  name = new FormControl('', [Validators.required]);
  phone = new FormControl('', [Validators.required]);
  address = new FormControl('', [Validators.required]);
  complement = new FormControl('', [Validators.required]);
  state = new FormControl('', [Validators.required]);
  city = new FormControl('', [Validators.required]);

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
    private userService: UserService,
    private router: Router
  ) {
    this.obterDadosUsuario();
  }

  ngOnInit(): void {
    this.formActionService.formAction$.subscribe(() => {
      this.updateUser();
    });
  }

  obterDadosUsuario() {
    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.apiService.getUser(user.id!)
      .subscribe(dados => {
        this.dados = dados;
        this.name.setValue(this.dados.name);
        this.phone.setValue(this.formatPhoneNumber(this.dados.phone));
        this.address.setValue(this.dados.address);
        this.complement.setValue(this.dados.complement);
        this.state.setValue(this.dados.state);
        this.city.setValue(this.dados.city);
  });
  }


  formatPhoneNumber(phone: string): string {
    const match = phone.match(/^(\d{2})(\d{1})(\d{4})(\d{4})$/);
    if (match) {
      return `(${match[1]}) ${match[2]} ${match[3]}-${match[4]}`;
    }
    return phone;
  }

  updateUser() {
    const nameValue = this.name.value;
    const phoneValue = this.phone.value;
    const addressValue = this.address.value;
    const complementValue = this.complement.value;
    const stateValue = this.state.value;
    const cityValue = this.city.value;

    if (!nameValue || !phoneValue || !addressValue || !complementValue || !stateValue || !cityValue) {
      return;
    }

    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.apiService.updateUser({
      name: nameValue,
      phone: phoneValue,
      address: addressValue,
      complement: complementValue,
      state: stateValue,
      city: cityValue
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
  setDados(dado: User) {
    this.name.setValue (dado.name);
    this.phone.setValue  (dado.phone);
    this.address.setValue (dado.address);
    this.complement.setValue (dado.complement);
    this.state.setValue (dado.state);
    this.city.setValue (dado.city);
  }
}
