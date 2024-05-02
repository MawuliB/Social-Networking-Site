import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToastGComponent } from './toast-g.component';

describe('ToastGComponent', () => {
  let component: ToastGComponent;
  let fixture: ComponentFixture<ToastGComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastGComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ToastGComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
