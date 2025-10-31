import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FormActionService {

  private formActionSubject = new Subject<void>();

  formAction$ = this.formActionSubject.asObservable();

  triggerFormAction() {
    this.formActionSubject.next();
  }

  constructor() { }
}
