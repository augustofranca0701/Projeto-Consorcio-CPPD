import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardGroupsConsortiumComponent } from './card-groups-consortium.component';

describe('CardGroupsConsortiumComponent', () => {
  let component: CardGroupsConsortiumComponent;
  let fixture: ComponentFixture<CardGroupsConsortiumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardGroupsConsortiumComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CardGroupsConsortiumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
