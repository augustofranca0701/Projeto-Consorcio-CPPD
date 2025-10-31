import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { CardGroupsConsortiumComponent } from '../../components/card-groups-consortium/card-groups-consortium.component';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import { gsap } from 'gsap';
import { filter } from 'rxjs';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { VisibilityService } from '../../services/visibility.service';
import { UserService } from '../../services/user.service';


gsap.registerPlugin(ScrollTrigger);

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterOutlet, MatButtonModule, MatToolbarModule, CardGroupsConsortiumComponent, FormsModule, ReactiveFormsModule, MatButtonToggleModule, MatButtonModule, MatButtonToggleModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  filterCategoryControl = new FormControl('');
  filterCategory?: string;


  scrollToSection(): void {
    const targetSection = document.querySelector('#category');
    if (targetSection) {
      targetSection.scrollIntoView({ behavior: 'smooth' });
    }
  }

  animationExecuted = false; // Variável de controle para a animação

  constructor(
    private router: Router,
    private visibilityService: VisibilityService,
    private cdr: ChangeDetectorRef , private userService: UserService
  ) { }

  ngOnInit(): void {

    // Obtém o usuário logado
    let user = this.userService.getUser();

    // Verifica se tem usuário logado
    if (user === undefined){this.router.navigate(['/login']);}

    this.filterCategory = 'all';
    if (this.router.url === '/') {
      this.visibilityService.setShowComponent(false); // Inicialmente esconder a navbar
      this.applyScrollReveal();
    } else {
      this.visibilityService.setShowComponent(true); // Se não estiver na página inicial, mostrar a navbar
    }

    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects === '/') {
        this.visibilityService.setShowComponent(false); // Inicialmente esconder a navbar
        this.applyScrollReveal();
      } else {
        this.visibilityService.setShowComponent(true); // Se não estiver na página inicial, mostrar a navbar
        this.animationExecuted = true; // Marcar que a animação foi executada para evitar o bug nas outras páginas
      }
    });

    // Adicionar um listener para verificar quando a navegação ocorre
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        // Verificar se a navegação ocorreu a partir da navbar
        if (event.url !== '/' && this.visibilityService.getShowComponent()) {
          // Navegação ocorreu a partir da navbar e a navbar está visível, então scroll para o topo da página
          window.scrollTo(0, 0);
        }
      }
    });
  }

  applyScrollReveal(): void {
    if (this.router.url === '/' && !this.animationExecuted) { // Verificar se está na página home e se a animação ainda não foi executada
      ScrollTrigger.create({
        trigger: '#header',
        start: 'top center',
        end: 'bottom center',
        onLeave: () => {
          this.showNavbarWithAnimation();
        },
        onEnterBack: () => {
          if (!this.animationExecuted) { // Verificar se a animação já foi executada antes de escondê-la novamente
            gsap.to('.navbar', { opacity: 0, y: -50, duration: 1, ease: 'power2.in', onComplete: () => {
              this.visibilityService.setShowComponent(false);
              this.cdr.detectChanges(); // Garantir a detecção de mudanças
            }});
          }
        }
      });
    } else {
      this.visibilityService.setShowComponent(true); // Se não estiver na página home, mostrar a navbar diretamente
    }
  }

  showNavbarWithAnimation(): void {
    if (this.router.url === '/' && !this.animationExecuted) { // Verificar se está na página home e se a animação ainda não foi executada
      this.visibilityService.setShowComponent(true);
      this.cdr.detectChanges(); // Garantir a detecção de mudanças
      setTimeout(() => {
        gsap.fromTo('.navbar', { opacity: 0, y: -50 }, { opacity: 1, y: 0, duration: 1, ease: 'power2.out' });
        this.animationExecuted = true; // Marcar que a animação foi executada
      }, 0);
    } else {
      this.visibilityService.setShowComponent(true); // Se não estiver na página home, mostrar a navbar diretamente
    }
  }

}
