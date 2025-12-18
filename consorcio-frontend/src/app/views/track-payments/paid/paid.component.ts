import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { ApiService } from '../../../services/api.service';
import { UserPayments } from '../../../../models/Payment/user-payments.model';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-paid',
  standalone: true,
  imports: [MatButtonModule, MatDividerModule, CommonModule],
  templateUrl: './paid.component.html',
  styleUrl: './paid.component.css'
})
export class PaidComponent {
  @Input() boletos: UserPayments[] = [];

  constructor(
    private apiService: ApiService,
    private userService: UserService,
    private router: Router
  ) {
    this.obterBoletosPagos();
  }

  obterBoletosPagos() {
    // obtém o id do usuário logado com segurança; redireciona para /login se necessário
    const userId = this.userService.requireUserIdOrRedirect();
    if (!userId) return;

    this.apiService.getPayments(userId)
      .subscribe({
        next: boletos => {
          this.boletos = boletos.filter(boleto => boleto.isPaid);
        },
        error: err => {
          console.error('Erro ao obter boletos pagos:', err);
        }
      });
  }
}
