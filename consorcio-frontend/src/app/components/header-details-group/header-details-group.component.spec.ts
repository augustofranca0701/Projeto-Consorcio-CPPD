import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderDetailsGroupComponent } from './header-details-group.component';

describe('HeaderDetailsGroupComponent', () => {
  let component: HeaderDetailsGroupComponent;
  let fixture: ComponentFixture<HeaderDetailsGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderDetailsGroupComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HeaderDetailsGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
