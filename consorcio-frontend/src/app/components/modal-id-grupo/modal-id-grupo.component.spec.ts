import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalIdGrupoComponent } from './modal-id-grupo.component';

describe('ModalIdGrupoComponent', () => {
  let component: ModalIdGrupoComponent;
  let fixture: ComponentFixture<ModalIdGrupoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalIdGrupoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ModalIdGrupoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
