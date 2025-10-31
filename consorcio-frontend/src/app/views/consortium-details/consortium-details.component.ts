import { Component } from '@angular/core';
import { BannerComponent } from '../../components/banner/banner.component';
import { CardComponent } from '../../components/card/card.component';
import { MatButtonModule } from '@angular/material/button';
import { HeaderDetailsGroupComponent } from '../../components/header-details-group/header-details-group.component';


@Component({
  selector: 'app-consortium-details',
  standalone: true,
  imports: [BannerComponent, CardComponent, MatButtonModule, HeaderDetailsGroupComponent],
  templateUrl: './consortium-details.component.html',
  styleUrl: './consortium-details.component.css'
})
export class ConsortiumDetailsComponent {

}
