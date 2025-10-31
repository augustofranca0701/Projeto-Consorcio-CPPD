import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderMyGroupsComponent } from './header-my-groups.component';

describe('HeaderDetailsGroupComponent', () => {
  let component: HeaderMyGroupsComponent;
  let fixture: ComponentFixture<HeaderMyGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderMyGroupsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderMyGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
