import { Component } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonModule } from '@angular/material/button';
import { HeaderDetailsGroupComponent } from '../../components/header-details-group/header-details-group.component';
import { BannerComponent } from '../../components/banner/banner.component';

@Component({
  selector: 'app-detalhes-do-grupo-usuario',
  standalone: true,
  imports: [BannerComponent, MatInputModule, MatFormFieldModule, MatSlideToggleModule, MatButtonModule, HeaderDetailsGroupComponent],
  templateUrl: './detalhes-do-grupo-usuario.component.html',
  styleUrl: './detalhes-do-grupo-usuario.component.css'
})
export class DetalhesDoGrupoUsuarioComponent {

}
