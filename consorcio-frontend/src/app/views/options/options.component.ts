import { Component } from '@angular/core';
import { HeaderMyGroupsComponent } from "../../components/header-my-groups/header-my-groups.component";
import { BannerComponent } from "../../components/banner/banner.component";
import { CardComponent } from "../../components/card/card.component";
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { User } from '../../../models/User/user.model';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';

@Component({
    selector: 'app-options',
    standalone: true,
    templateUrl: './options.component.html',
    styleUrl: './options.component.css',
    imports: [HeaderMyGroupsComponent, BannerComponent, CardComponent, MatIconModule, MatFormFieldModule, MatInputModule, FormsModule, MatTooltipModule, MatButtonModule, RouterLink, RouterLinkActive, CommonModule],
})
export class OptionsComponent {
  hide = true;

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

  constructor(private apiService: ApiService, private userService: UserService, private router: Router) {
    this.obterDadosUsuario();
  }

  toggleHide() {
    this.hide = !this.hide;
  }
  openExternalLink(): void {
    window.open('https://wa.me/5562981687434', '_blank');
  }

  obterDadosUsuario() {
    // obtém o id do usuário logado com segurança; redireciona para /login se necessário
    const userId = this.userService.requireUserIdOrRedirect();
    if (!userId) return;

    this.apiService.getUser(userId)
      .subscribe({
        next: dados => this.dados = dados,
        error: err => {
          console.error('Erro ao obter dados do usuário:', err);
          // opcional: mostrar snackbar
        }
      });
  }

  formatPhoneNumber(phone: string): string {
    if (!phone) return '';
    const match = phone.match(/^(\d{2})(\d{1})(\d{4})(\d{4})$/);
    if (match) {
      return `(${match[1]}) ${match[2]} ${match[3]}-${match[4]}`;
    }
    return phone;
  }
}
