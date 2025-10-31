import { Component } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonModule } from '@angular/material/button';
import { HeaderDetailsGroupComponent } from '../../components/header-details-group/header-details-group.component';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { Group } from '../../../models/Group/group.model';
import { empty } from 'rxjs';



@Component({
  selector: 'app-detalhes-do-grupo',
  standalone: true,
  imports: [MatInputModule, MatFormFieldModule, MatSlideToggleModule, MatButtonModule, HeaderDetailsGroupComponent, MatIconModule, MatTooltipModule, RouterLink, RouterLinkActive],
  templateUrl: './detalhes-do-grupo.component.html',
  styleUrl: './detalhes-do-grupo.component.css'
})
export class DetalhesDoGrupoComponent {

 

  openExternalLink(): void {
    window.open('https://wa.me/5562981687434', '_blank');
  }
}
