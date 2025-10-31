import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetalhesDoGrupoComponent } from './detalhes-do-grupo.component';

describe('DetalhesDoGrupoComponent', () => {
  let component: DetalhesDoGrupoComponent;
  let fixture: ComponentFixture<DetalhesDoGrupoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalhesDoGrupoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DetalhesDoGrupoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
