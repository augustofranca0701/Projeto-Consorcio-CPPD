import { Component, Input } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';

@Component({
  selector: 'app-banner',
  standalone: true,
  imports: [MatToolbar],
  templateUrl: './banner.component.html',
  styleUrl: './banner.component.css'
})
export class BannerComponent {
  @Input() backgroundColor?: string;
  @Input() title?: string;
  @Input() subtitle?: string;
  @Input() createdDate?: string;
  @Input() participantCount?: number;

  shouldDisplay(): boolean {
    return !!this.backgroundColor || !!this.title || !!this.subtitle || !!this.createdDate || !!this.participantCount;
  }
}
