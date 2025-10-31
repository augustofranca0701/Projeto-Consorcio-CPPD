import {ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { VisibilityService } from '../../services/visibility.service';
import { Validators, FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {merge} from 'rxjs';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { gsap } from 'gsap';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { User } from '../../../models/User/user.model';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
@Component({
    selector: 'app-login-screen',
    standalone: true,
    templateUrl: './login-screen.component.html',
    styleUrl: './login-screen.component.css',
    imports: [MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, FormsModule, ReactiveFormsModule, RouterOutlet, RouterLink, RouterLinkActive, CommonModule]
})

export class LoginScreenComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  hide = true;
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required]);
  errorMessage = '';

  constructor(private visibilityService: VisibilityService, private apiService: ApiService, 
    private snackBar: MatSnackBar, private userService: UserService, private router: Router) {
    this.checkAnimationExecuted();
    this.visibilityService.setShowComponent(false);
    merge(this.email.statusChanges, this.email.valueChanges)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.updateErrorMessage());
  }


  fazerLogin()
{
  const email = this.email;
  const password = this.password;

  const body = { email: email.value, password: password.value };

  this.apiService.postLogin(body).subscribe(
    (response: User) => {
      this.userService.setUser(response);
      this.router.navigate(['/']);
    },
    error => {
      console.error('Erro ao realizar login.');
      this.snackBar.open('Dados incorretos!', 'Fechar', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000
      });
    }
  );
}


  checkAnimationExecuted(): void {
    if (!localStorage.getItem('animationExecuted')) {
      this.animateCounters();
      this.animateDigits();
      this.animateLoader();
      this.animateLoadingScreen();
    }
  }

  updateErrorMessage() {
    if (this.email.hasError('required')) {
      this.errorMessage = 'Preencha este campo!';
    } else if (this.email.hasError('email')) {
      this.errorMessage = 'Insira um e-mail válido';
    } else {
      this.errorMessage = '';
    }
  }

  ngOnInit(): void {
    this.initAnimations(); // Inicializar animações
  }


  private initAnimations(): void {
    // Inicializar animações GSAP
    this.animateCounters();
    this.animateDigits();
    this.animateLoader();
    this.animateLoadingScreen();
  }

  private animateCounters(): void {
    const counter3 = document.querySelector(".counter-3");

    if (counter3) {
      for (let i = 0; i < 2; i++) {
        for (let j = 0; j < 10; j++) {
          const div = document.createElement("div");
          div.className = "num";
          div.textContent = j.toString();
          counter3.appendChild(div);
        }
      }
      const finalDiv = document.createElement("div");
      finalDiv.className = "num";
      finalDiv.textContent = "0";
      counter3.appendChild(finalDiv);

      this.animate(counter3, 5);
      this.animate(document.querySelector(".counter-4"), 2);
      this.animate(document.querySelector(".counter-2"), 6);
      this.animate(document.querySelector(".counter-1"), 2, 4);
    }
  }

  private animate(counter: Element | null, duration: number, delay: number = 0): void {
    if (counter) {
      const numHeight = (counter.querySelector(".num") as HTMLElement).clientHeight;
      const totalDistance = (counter.querySelectorAll(".num").length - 1) * numHeight;
      gsap.to(counter, {
        y: -totalDistance,
        duration: duration,
        delay: delay,
        ease: "power2.inOut",
      });
    }
  }

  private animateDigits(): void {
    gsap.to(".digit", {
      top: "-150px",
      stagger: {
        amount: 0.25,
      },
      delay: 6,
      duration: 1,
      ease: "power4.inOut",
    });
  }

  private animateLoader(): void {
    gsap.from(".loader-1", {
      width: 0,
      duration: 6,
      ease: "power2.inOut",
    });

    gsap.to(".loader", {
      background: "none",
      delay: 6,
      duration: 0.1,
    });

    gsap.to(".loader-1", {
      rotate: 90,
      duration: 0.5,
      delay: 6,
    });

    gsap.to(".loader", {
      scale: 500,
      duration: 1,
      delay: 7,
      ease: "power2.inOut",
    });

    gsap.to(".loader", {
      duration: 1,
      delay: 7,
      ease: "power2.inOut",
    });
  }

  private animateLoadingScreen(): void {
    gsap.to(".loading-screen", {
      opacity: 0,
      duration: 0.5,
      delay: 7.5,
      ease: "power1.inOut",
    });
  }
}
