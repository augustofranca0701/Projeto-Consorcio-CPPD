import { Component, OnInit } from '@angular/core';
import { VisibilityService } from '../../services/visibility.service';
import { FormBuilder, ReactiveFormsModule, FormsModule, FormGroup, Validators } from '@angular/forms';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import {MatButtonModule} from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../services/api.service';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarModule, MatSnackBarVerticalPosition} from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';


@Component({
    selector: 'app-register-screen',
    standalone: true,
    templateUrl: './register-screen.component.html',
    styleUrl: './register-screen.component.css',
    providers: [
      {
        provide: STEPPER_GLOBAL_OPTIONS,
        useValue: {showError: false},
      },
    ],
    imports: [MatStepperModule, ReactiveFormsModule, MatFormFieldModule,
      MatInputModule, MatButtonModule, MatInputModule,FormsModule,
      RouterOutlet, RouterLink, RouterLinkActive,MatIconModule, MatSnackBarModule, CommonModule]
})
export class RegisterScreenComponent implements OnInit {
  signUpForm: FormGroup;
  firstFormGroup: FormGroup = this.fb.group({
    email: [''],
    password: [''],
    confirmPassword: [''],
  });
  secondFormGroup: FormGroup = this.fb.group({
    name: [''], // Adicione esta linha
    cpf: [''],
    phone: [''],
  });

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
  }

  ngOnInit(): void {
  }

  onStepChange(step: number) {
    if (step === 0) {
      this.firstStepCompleted = true;
    } else if (step === 1) {
      this.secondStepCompleted = true;
    }
  }

  signUp() {
    if (this.signUpForm.valid) {
      let formData = this.signUpForm.value

      formData.nome = formData.nome;
      formData.email = formData.email;
      formData.password = formData.password;
      formData.cpf = formData.cpf;
      formData.phone = formData.phone;
      formData.address = formData.address;
      formData.complement = formData.complement;
      formData.state = formData.state;
      formData.city = formData.city;

      this.apiService.postSignUp(formData).subscribe(
        response => {
          console.log('User created successfully:', response);
          this.snackBar.open('Cadastro realizado com sucesso!', 'Fechar', {
            duration: 3000,
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
          });
        },
        error => {
          console.error('Error creating user:', error);
          this.snackBar.open('Ocorreu um erro ao realizar o cadastro. Por favor, tente novamente.', 'Fechar', {
            duration: 3000,
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
          });
        }
      );
    } else {
      console.log('Form is invalid', this.signUpForm);
    }
  }
}

