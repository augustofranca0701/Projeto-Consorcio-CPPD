import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {MatDividerModule} from '@angular/material/divider';
import { ModalUploadComponent } from '../../../components/modal-upload/modal-upload.component';
import { MatDialog} from '@angular/material/dialog';
import { ApiService } from '../../../services/api.service';
import { UserPayments } from '../../../../models/Payment/user-payments.model';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-not-paid',
  standalone: true,
  imports: [MatButtonModule, MatDividerModule, CommonModule],
  templateUrl: './not-paid.component.html',
  styleUrl: './not-paid.component.css'
})
export class NotPaidComponent {
  @Input() boletos: UserPayments[] = [];

  constructor(
    public dialog: MatDialog,
    private apiService: ApiService,
    private userService: UserService,private router: Router
  ) {
    this.obterBoletosNaoPagos();
  }

  openDialog(boletoId: string, enterAnimationDuration: string, exitAnimationDuration: string): void {
    this.dialog.open(ModalUploadComponent, {
      width: '374px',
      enterAnimationDuration,
      exitAnimationDuration,
      data: { idBoleto: boletoId }
    });
  }

  obterBoletosNaoPagos() {
    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.apiService.getPayments(user.id!)
      .subscribe(boletos => {
        this.boletos = boletos.filter(boleto => !boleto.isPaid);
      });
  }

  downloadBoleto() {
    const url = '../../../../assets/Modelo-de-Boleto.pdf';
    const a = document.createElement('a');
    a.href = url;
    a.download = 'Modelo-de-Boleto.pdf';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
}
