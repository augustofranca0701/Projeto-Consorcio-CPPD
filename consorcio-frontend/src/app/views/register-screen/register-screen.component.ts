import { Component, OnInit } from '@angular/core';
import { VisibilityService } from '../../services/visibility.service';
import {
  FormBuilder,
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  Validators,
} from '@angular/forms';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../services/api.service';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarModule,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register-screen',
  standalone: true,
  templateUrl: './register-screen.component.html',
  styleUrl: './register-screen.component.css',
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { showError: false },
    },
  ],
  imports: [
    MatStepperModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatInputModule,
    FormsModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatIconModule,
    MatSnackBarModule,
    CommonModule,
  ],
})
export class RegisterScreenComponent implements OnInit {
  signUpForm: FormGroup;

  // declaração sem inicializar com this.fb (evita uso antes do constructor)
  firstFormGroup!: FormGroup;
  secondFormGroup!: FormGroup;

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  firstStepCompleted = false;
  secondStepCompleted = false;
  hide = true;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private visibilityService: VisibilityService,
    private snackBar: MatSnackBar,
    private apiService: ApiService
  ) {
    this.visibilityService.setShowComponent(false);

    // formulário principal (campos planos — mínima alteração)
    this.signUpForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      name: ['', Validators.required],
      cpf: ['', Validators.required],
      phone: ['', Validators.required],
      address: ['', Validators.required],
      complement: ['', Validators.required],
      state: ['', Validators.required],
      city: ['', Validators.required],
    });

    // solução minimal: apontar os stepControls para o mesmo FormGroup
    // assim o mat-stepper valida com os controles realmente usados no template
    this.firstFormGroup = this.signUpForm;
    this.secondFormGroup = this.signUpForm;
  }

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      this.apiService.getUsers().subscribe({
        next: (users) => {
          /* usar users */
        },
        error: (err) => console.error('Erro ao buscar users:', err),
      });
    } else {
      console.log('Sem token: pulando chamada GET /users');
    }
  }

  onStepChange(step: number) {
    if (step === 0) {
      this.firstStepCompleted = true;
    } else if (step === 1) {
      this.secondStepCompleted = true;
    }
  }

  nextStep(stepper: any, controlNames: string[]) {
    const invalid: string[] = [];

    for (const name of controlNames) {
      const ctrl = this.signUpForm.get(name);
      if (ctrl) {
        // marca como tocado/dirty para disparar mensagens de erro somente depois do clique
        ctrl.markAsTouched();
        ctrl.markAsDirty();
        if (ctrl.invalid) invalid.push(name);
      }
    }

    if (invalid.length === 0) {
      stepper.next();
    } else {
      // opcional: foco no primeiro campo inválido
      const firstInvalid = this.signUpForm.get(invalid[0]);
      if (firstInvalid) {
        // tenta focar via DOM — funciona se input tiver id ou você adaptar
        // document.getElementById(invalid[0])?.focus();
      }
    }
  }

  signUp() {
    if (this.signUpForm.valid) {
      let formData = { ...this.signUpForm.value };

      // mapear name -> nome se backend espera 'nome'
      formData.nome = formData.name;

      // (opcional) remover confirmPassword antes de enviar
      delete formData.confirmPassword;

      this.apiService.postSignUp(formData).subscribe(
        (response) => {
          console.log('User created successfully:', response);
          this.snackBar.open('Cadastro realizado com sucesso!', 'Fechar', {
            duration: 3000,
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
          });
        },
        (error) => {
          console.error('Error creating user:', error);
          this.snackBar.open(
            'Ocorreu um erro ao realizar o cadastro. Por favor, tente novamente.',
            'Fechar',
            {
              duration: 3000,
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
            }
          );
        }
      );
    } else {
      console.log('Form is invalid', this.signUpForm);
    }
  }
}
