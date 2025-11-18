import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { CardGroupsConsortiumComponent } from '../../components/card-groups-consortium/card-groups-consortium.component';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import { filter } from 'rxjs';
import { VisibilityService } from '../../services/visibility.service';
import { UserService } from '../../services/user.service';

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

  constructor(
    private router: Router,
    private userService: UserService
  ) { }

  ngOnInit(): void {

    // Verificar login
    const user = this.userService.getUser();
    if (!user) this.router.navigate(['/login']);

    this.filterCategory = 'all';
  }

  scrollToSection(): void {
    const targetSection = document.querySelector('#category');
    if (targetSection) {
      targetSection.scrollIntoView({ behavior: 'smooth' });
    }
  }
}
