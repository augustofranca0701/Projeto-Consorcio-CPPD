import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserPayments } from '../../../models/Payment/user-payments.model';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarModule, MatSnackBarVerticalPosition} from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-modal-upload',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, FormsModule, CommonModule, MatSnackBarModule],
  templateUrl: './modal-upload.component.html',
  styleUrl: './modal-upload.component.css'
})


export class ModalUploadComponent implements OnInit {
  boletos: UserPayments[] = [];
  selectedFile: File | null = null;
  pagamentos: UserPayments[] = [];
  pagamentosCarregados = false;
  idBoleto: string;
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  constructor(
    public dialogRef: MatDialogRef<ModalUploadComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { idBoleto: string },
    private apiService: ApiService,
    private userService: UserService,private router: Router
  ) {
    this.idBoleto = data.idBoleto;
  }

  ngOnInit(): void {
    if (!this.idBoleto) {
      console.log('ID do boleto não fornecido');
    }
  }

  onUploadCardClick() {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  enviarPagamento() {
    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    if (this.idBoleto) {
      this.apiService.makePayment(parseFloat(this.idBoleto), user.id!).pipe(
        catchError(error => {
          console.error('Erro ao enviar o pagamento:', error);
          this.snackBar.open('Erro ao enviar o pagamento. Por favor, tente novamente mais tarde.', 'Fechar', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 5000,
          });
          return of(null);
        })
      ).subscribe(response => {
        if (response) {
          console.log('Pagamento enviado com sucesso!', response);
          this.dialogRef.close();
          this.snackBar.open('Pagamento enviado com sucesso!', 'Fechar', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
          });
        }
      });
    } else {
      console.error('ID do boleto não fornecido');
    }
  }

  isImageType(type: string | undefined) {
    return type && type.startsWith('image/');
  }
}
