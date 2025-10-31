import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderAccountProfileComponent } from './header-account-profile.component';

describe('HeaderAccountProfileComponent', () => {
  let component: HeaderAccountProfileComponent;
  let fixture: ComponentFixture<HeaderAccountProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderAccountProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderAccountProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
