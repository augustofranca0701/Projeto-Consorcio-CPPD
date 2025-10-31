import { Component, OnInit } from '@angular/core';
import { HeaderDetailsGroupComponent } from "../../components/header-details-group/header-details-group.component";
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatButtonModule} from '@angular/material/button';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarModule, MatSnackBarVerticalPosition} from '@angular/material/snack-bar';


@Component({
  selector: 'app-create-groups',
  standalone: true,
  templateUrl: './create-groups.component.html',
  styleUrl: './create-groups.component.css',
  imports: [HeaderDetailsGroupComponent, MatInputModule, MatFormFieldModule,
    MatSlideToggleModule,MatButtonModule, MatIconModule, MatTooltipModule, RouterLink, RouterLinkActive,
  CommonModule, FormsModule, ReactiveFormsModule, MatSnackBarModule]
})
export class CreateGroupsComponent{

  groupForm: FormGroup ;
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.groupForm = this.fb.group({
      nomeGrupo: ['', Validators.required],
      valorParcelas: [0, [Validators.required, Validators.min(1)]],
      valorCreditos: [0, [Validators.required, Validators.min(1)]],
      quantidadePessoas: [0, [Validators.required, Validators.min(1)]],
      privado: [false],
      idUser: [0],
    });
  }

criarGrupo() {
  if (this.groupForm.valid) {
    let groupData = this.groupForm.value;
    let dataCriacao = new Date();0
    let quantidadeParcelas = groupData.quantidadePessoas;
    let dataFinal = new Date(dataCriacao.getTime());
    dataFinal.setMonth(dataCriacao.getMonth() + quantidadeParcelas);

    groupData.dataCriacao = dataCriacao;
    groupData.dataFinal = dataFinal;
    groupData.duracaoMeses = quantidadeParcelas;
    groupData.name = groupData.nomeGrupo;
    groupData.valorTotal = groupData.valorCreditos;
    groupData.meses = groupData.quantidadePessoas;

    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.apiService.postGroup(user.id!, groupData).subscribe(
      response => {
        console.log('Grupo criado com sucesso:', response);
        this.snackBar.open('Grupo criado com sucesso!', 'Fechar', {
          duration: 3000,
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
        });
      },
      error => {
        console.error('Erro ao criar grupo:', error);
        this.snackBar.open('Erro ao criar grupo. Por favor, tente novamente.', 'Fechar', {
          duration: 3000,
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
        });
      }
    );
  } else {
    console.log('Formulário inválido', this.groupForm);
  }
}


openExternalLink(): void {
  window.open('https://wa.me/5562981687434', '_blank');
}


}
