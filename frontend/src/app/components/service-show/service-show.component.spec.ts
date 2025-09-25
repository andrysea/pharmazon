import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceShowComponent } from './service-show.component';

describe('ServiceShowComponent', () => {
  let component: ServiceShowComponent;
  let fixture: ComponentFixture<ServiceShowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServiceShowComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ServiceShowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
