import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsortiumDetailsComponent } from './consortium-details.component';

describe('ConsortiumDetailsComponent', () => {
  let component: ConsortiumDetailsComponent;
  let fixture: ComponentFixture<ConsortiumDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsortiumDetailsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConsortiumDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
