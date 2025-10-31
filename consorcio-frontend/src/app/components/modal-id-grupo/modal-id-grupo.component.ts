import { Component } from '@angular/core';
import {MatDialogRef, MatDialogModule} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-modal-id-grupo',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, FormsModule],
  templateUrl: './modal-id-grupo.component.html',
  styleUrl: './modal-id-grupo.component.css'
})
export class ModalIdGrupoComponent {
  constructor(public dialogRef: MatDialogRef<ModalIdGrupoComponent>) {
  }
}
