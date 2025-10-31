import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackPaymentsComponent } from './track-payments.component';

describe('TrackPaymentsComponent', () => {
  let component: TrackPaymentsComponent;
  let fixture: ComponentFixture<TrackPaymentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackPaymentsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TrackPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
