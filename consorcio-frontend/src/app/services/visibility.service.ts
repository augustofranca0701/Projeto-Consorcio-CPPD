import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VisibilityService {
  private showComponent: boolean = true;

  constructor() { }

  setShowComponent(value: boolean): void {
    this.showComponent = value;
  }

  getShowComponent(): boolean {
    return this.showComponent;
  }
}
