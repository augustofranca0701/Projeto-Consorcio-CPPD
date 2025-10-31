import { Component } from '@angular/core';
import { BannerComponent } from '../../components/banner/banner.component';
import { HeaderDetailsGroupComponent } from '../../components/header-details-group/header-details-group.component';
import { CardComponent } from '../../components/card/card.component';
import { MatButtonModule } from '@angular/material/button';
import { NotPaidComponent } from './not-paid/not-paid.component';
import { PaidComponent } from "./paid/paid.component";
import { UserPayments } from '../../../models/Payment/user-payments.model';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';


@Component({
    selector: 'app-track-payments',
    standalone: true,
    templateUrl: './track-payments.component.html',
    styleUrls: ['./track-payments.component.css'],
    imports: [BannerComponent, HeaderDetailsGroupComponent, CardComponent, MatButtonModule, NotPaidComponent, PaidComponent, CommonModule]
})
export class TrackPaymentsComponent {
  totalPaid: number = 0;
  totalPending: number = 0;
  totalOverdue: number = 0;
  allPaidBoletos: UserPayments[] = [];
  allNotPaidBoletos: UserPayments[] = [];
  showFullList = false;
  limit = 3;


  constructor(private apiService: ApiService,private userService: UserService,private router: Router) {
    this.loadData();
  }

  loadData() {
    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.apiService.getPayments(user.id!)
      .subscribe(boletos => {
        this.allPaidBoletos = boletos.filter(boleto => boleto.isPaid);
        this.allNotPaidBoletos = boletos.filter(boleto => !boleto.isPaid);

        this.calculateTotals();
      });
  }

  calculateTotals() {
    const today = new Date();
    this.totalPaid = this.allPaidBoletos.reduce((acc, boleto) => acc + parseFloat(boleto.valor), 0);
    this.totalPending = this.allNotPaidBoletos
      .filter(boleto => new Date(boleto.dataVencimento) >= today)
      .reduce((acc, boleto) => acc + parseFloat(boleto.valor), 0);
    this.totalOverdue = this.allNotPaidBoletos
      .filter(boleto => new Date(boleto.dataVencimento) < today)
      .reduce((acc, boleto) => acc + parseFloat(boleto.valor), 0);
  }

  get limitedPaidBoletos() {
    return this.showFullList ? this.allPaidBoletos : this.allPaidBoletos.slice(0, this.limit);
  }

  get limitedNotPaidBoletos() {
    return this.showFullList ? this.allNotPaidBoletos : this.allNotPaidBoletos.slice(0, this.limit);
  }

  toggleShowAll() {
    this.showFullList = !this.showFullList;
  }
}
