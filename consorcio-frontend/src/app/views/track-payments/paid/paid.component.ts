import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {MatDividerModule} from '@angular/material/divider';
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
    private apiService: ApiService,private userService: UserService,private router: Router
  ){
    this.obterBoletosPagos()
  }

  obterBoletosPagos() {
    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.apiService.getPayments(user.id!)
      .subscribe(boletos => {
        this.boletos = boletos.filter(boleto => boleto.isPaid);
      });
  }
}
