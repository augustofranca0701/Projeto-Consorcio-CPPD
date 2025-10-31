import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalhesDoGrupoUsuarioComponent } from './detalhes-do-grupo-usuario.component';

describe('DetalhesDoGrupoUsuarioComponent', () => {
  let component: DetalhesDoGrupoUsuarioComponent;
  let fixture: ComponentFixture<DetalhesDoGrupoUsuarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalhesDoGrupoUsuarioComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DetalhesDoGrupoUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
