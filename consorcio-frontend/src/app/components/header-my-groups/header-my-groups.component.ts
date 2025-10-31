import {Component, OnInit} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatButtonModule} from '@angular/material/button';
import { RouterLink, RouterLinkActive } from '@angular/router';
import {MatMenuModule} from '@angular/material/menu';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import { ModalIdGrupoComponent } from '../modal-id-grupo/modal-id-grupo.component';

@Component({
  selector: 'app-header-my-groups',
  standalone: true,
  imports: [MatButtonModule, MatTooltipModule, MatIconModule, RouterLink, RouterLinkActive, MatMenuModule, MatButtonToggleModule, FormsModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './header-my-groups.component.html',
  styleUrls: ['./header-my-groups.component.css']
})
export class HeaderMyGroupsComponent implements OnInit{
  filterCategoryControl = new FormControl('');
  filterCategory?: string;
  ngOnInit() {
    // Define o valor inicial como 'all' para selecionar o primeiro toggle
    this.filterCategory = 'all';
  }

  constructor(public dialog: MatDialog) {}

  openDialog(enterAnimationDuration: string, exitAnimationDuration: string): void {
    this.dialog.open(ModalIdGrupoComponent, {
      width: '580px',
      enterAnimationDuration,
      exitAnimationDuration,
    });
  }
}
